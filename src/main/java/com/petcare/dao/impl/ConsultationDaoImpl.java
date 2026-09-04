package com.petcare.dao.impl;

import com.petcare.dao.ConsultationDao;
import com.petcare.enums.QueryStatus;
import com.petcare.exception.InsufficientStockException;
import com.petcare.exception.PetCareException;
import com.petcare.model.Consultation;
import com.petcare.model.ConsultationMedication;
import com.petcare.model.Medication;
import com.petcare.model.Owner;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDaoImpl implements ConsultationDao {

    // Reusing the other DAOs instead of duplicating their mapping logic here
    private final OwnerDaoImpl ownerDao = new OwnerDaoImpl();
    private final PetDaoImpl petDao = new PetDaoImpl();
    private final UserDaoImpl userDao = new UserDaoImpl();
    private final MedicationDaoImpl medicationDao = new MedicationDaoImpl();

    @Override
    public int save(Consultation consultation) throws PetCareException {
        String insertConsultationSql = "INSERT INTO consulta "
                + "(propietario_id, mascota_id, veterinario_id, fecha_consulta, motivo, diagnostico, tratamiento, estado, costo_total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insertDetailSql = "INSERT INTO consulta_medicamento (consulta_id, medicamento_id, cantidad, precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";

        String updateStockSql = "UPDATE medicamento SET stock_disponible = stock_disponible - ? WHERE id = ?";

        // Opening the connection manually here (not in try-with-resources) because we
        // need to control commit/rollback ourselves before it closes
        Connection connection = null;

        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            int consultationId = insertConsultation(connection, insertConsultationSql, consultation);

            double totalCost = 0;

            // Going through every medication used and inserting its detail row plus discounting stock
            for (ConsultationMedication detail : consultation.getMedicationsUsed()) {
                Medication medication = detail.getMedication();

                // Extra safety check right before touching the stock, on top of whatever the service already validated
                if (medication.getAvailableStock() < detail.getQuantity()) {
                    throw new InsufficientStockException(
                            "Not enough stock for medication " + medication.getMedicationCode());
                }

                insertConsultationMedication(connection, insertDetailSql, consultationId, detail);
                discountStock(connection, updateStockSql, medication.getId(), detail.getQuantity());

                totalCost += detail.getSubtotal();
            }

            updateConsultationTotalCost(connection, consultationId, totalCost);

            // Everything went fine, making all the inserts and updates permanent
            connection.commit();
            return consultationId;

        } catch (SQLException | PetCareException e) {
            // Something failed, undoing every partial change from this transaction
            rollbackQuietly(connection);
            if (e instanceof PetCareException) {
                throw (PetCareException) e;
            }
            throw new PetCareException("Error registering consultation", e);

        } finally {
            closeQuietly(connection);
        }
    }

    // Small helper just for the first insert, keeps save() easier to read
    private int insertConsultation(Connection connection, String sql, Consultation consultation) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, consultation.getOwner().getId());
            statement.setInt(2, consultation.getPet().getId());
            statement.setInt(3, consultation.getVeterinarian().getId());
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(consultation.getConsultationDate()));
            statement.setString(5, consultation.getReason());
            statement.setString(6, consultation.getDiagnosis());
            statement.setString(7, consultation.getTreatment());
            statement.setString(8, consultation.getStatus().name());
            // Starting at 0, updateConsultationTotalCost fixes this once we know the real total
            statement.setDouble(9, 0);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Consultation was inserted but no id was returned");
    }

    private void insertConsultationMedication(Connection connection, String sql, int consultationId,
                                                ConsultationMedication detail) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, consultationId);
            statement.setInt(2, detail.getMedication().getId());
            statement.setInt(3, detail.getQuantity());
            statement.setDouble(4, detail.getUnitPrice());
            statement.setDouble(5, detail.getSubtotal());
            statement.executeUpdate();
        }
    }

    private void discountStock(Connection connection, String sql, int medicationId, int quantity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, medicationId);
            statement.executeUpdate();
        }
    }

    private void updateConsultationTotalCost(Connection connection, int consultationId, double totalCost) throws SQLException {
        String sql = "UPDATE consulta SET costo_total = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, totalCost);
            statement.setInt(2, consultationId);
            statement.executeUpdate();
        }
    }

    @Override
    public void updateStatus(int consultationId, QueryStatus newStatus) throws PetCareException {
        // This is a single statement, so a plain try-with-resources is enough, no manual transaction needed
        String sql = "UPDATE consulta SET estado = ? WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newStatus.name());
            statement.setInt(2, consultationId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new PetCareException("Error updating consultation status", e);
        }
    }

    @Override
    public void finalizeConsultation(int consultationId, String diagnosis, String treatment) throws PetCareException {
        Connection connection = null;

        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            // Recalculating the total from the detail rows, this is the source of truth, not the model
            double totalCost = sumMedicationsCost(connection, consultationId);

            String sql = "UPDATE consulta SET estado = ?, diagnostico = ?, tratamiento = ?, costo_total = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, QueryStatus.COMPLETED.name());
                statement.setString(2, diagnosis);
                statement.setString(3, treatment);
                statement.setDouble(4, totalCost);
                statement.setInt(5, consultationId);
                statement.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw new PetCareException("Error finalizing consultation", e);

        } finally {
            closeQuietly(connection);
        }
    }

    private double sumMedicationsCost(Connection connection, int consultationId) throws SQLException {
        String sql = "SELECT SUM(subtotal) AS total FROM consulta_medicamento WHERE consulta_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, consultationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("total");
                }
            }
        }
        return 0;
    }

    @Override
    public Consultation findById(int id) throws PetCareException {
        String sql = "SELECT * FROM consulta WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Consultation consultation = mapRow(resultSet);
                    consultation.setMedicationsUsed(loadMedicationsUsed(connection, id));
                    return consultation;
                }
                return null;
            }

        } catch (SQLException e) {
            throw new PetCareException("Error looking up consultation by id", e);
        }
    }

    @Override
    public List<Consultation> findByPetId(int petId) throws PetCareException {
        String sql = "SELECT * FROM consulta WHERE mascota_id = ? ORDER BY fecha_consulta DESC";
        List<Consultation> consultations = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, petId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Consultation consultation = mapRow(resultSet);
                    // Loading the medications for each consultation found in the history
                    consultation.setMedicationsUsed(loadMedicationsUsed(connection, consultation.getId()));
                    consultations.add(consultation);
                }
            }

        } catch (SQLException e) {
            throw new PetCareException("Error listing consultation history for pet", e);
        }

        return consultations;
    }

    // Builds the Consultation and its related Owner, Pet and User objects, but not the medications list
    private Consultation mapRow(ResultSet resultSet) throws SQLException, PetCareException {
        Consultation consultation = new Consultation();
        consultation.setId(resultSet.getInt("id"));
        consultation.setConsultationDate(resultSet.getTimestamp("fecha_consulta").toLocalDateTime());
        consultation.setReason(resultSet.getString("motivo"));
        consultation.setDiagnosis(resultSet.getString("diagnostico"));
        consultation.setTreatment(resultSet.getString("tratamiento"));
        consultation.setStatus(QueryStatus.valueOf(resultSet.getString("estado")));
        consultation.setTotalCost(resultSet.getDouble("costo_total"));
        consultation.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

        // Rebuilding the related objects through the other DAOs, same idea as PetDaoImpl does with Owner
        Owner owner = ownerDao.findById(resultSet.getInt("propietario_id"));
        Pet pet = petDao.findById(resultSet.getInt("mascota_id"));
        User veterinarian = userDao.findById(resultSet.getInt("veterinario_id"));

        consultation.setOwner(owner);
        consultation.setPet(pet);
        consultation.setVeterinarian(veterinarian);

        return consultation;
    }

    // Loads every row from consulta_medicamento for a given consultation, with the full Medication inside each one
    private List<ConsultationMedication> loadMedicationsUsed(Connection connection, int consultationId)
            throws SQLException, PetCareException {

        String sql = "SELECT * FROM consulta_medicamento WHERE consulta_id = ?";
        List<ConsultationMedication> details = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, consultationId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ConsultationMedication detail = new ConsultationMedication();
                    detail.setId(resultSet.getInt("id"));
                    detail.setQuantity(resultSet.getInt("cantidad"));
                    detail.setUnitPrice(resultSet.getDouble("precio_unitario"));
                    detail.setSubtotal(resultSet.getDouble("subtotal"));

                    // Fetching the full Medication, not just its id, so it prints and behaves nicely
                    Medication medication = medicationDao.findById(resultSet.getInt("medicamento_id"));
                    detail.setMedication(medication);

                    details.add(detail);
                }
            }
        }

        return details;
    }

    // Rolling back is best-effort: if the connection is already broken, there is nothing else we can do
    private void rollbackQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
                // Nothing useful to do here, the original error is what matters
            }
        }
    }

    // Restoring autoCommit and closing manually since we did not use try-with-resources for the connection
    private void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException ignored) {
                // Nothing useful to do here either
            }
        }
    }
}