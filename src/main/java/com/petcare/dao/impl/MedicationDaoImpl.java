package com.petcare.dao.impl;

import com.petcare.dao.MedicationDao;
import com.petcare.exception.PetCareException;
import com.petcare.model.Medication;
import com.petcare.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicationDaoImpl implements MedicationDao {

    @Override
    public int save(Medication medication) throws PetCareException {
        // Using RETURN_GENERATED_KEYS so we can get the new id back after the insert
        String sql = "INSERT INTO medicamento "
                + "(codigo_medicamento, nombre, categoria, laboratorio, stock_total, stock_disponible, precio_unitario, is_activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Filling the placeholders in the same order as the columns above
            statement.setString(1, medication.getMedicationCode());
            statement.setString(2, medication.getName());
            statement.setString(3, medication.getCategory());
            statement.setString(4, medication.getLaboratory());
            statement.setInt(5, medication.getTotalStock());
            statement.setInt(6, medication.getAvailableStock());
            statement.setDouble(7, medication.getUnitPrice());
            statement.setBoolean(8, medication.isActive());

            statement.executeUpdate();

            // Grabbing the id MySQL just generated for this row
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new PetCareException("Medication was inserted but no id was returned");

        } catch (SQLException e) {
            // Wrapping the technical error into our own exception, keeping the original cause
            throw new PetCareException("Error saving medication", e);
        }
    }

    @Override
    public void update(Medication medication) throws PetCareException {
        String sql = "UPDATE medicamento SET nombre = ?, categoria = ?, laboratorio = ?, "
                + "stock_total = ?, stock_disponible = ?, precio_unitario = ?, is_activo = ? "
                + "WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, medication.getName());
            statement.setString(2, medication.getCategory());
            statement.setString(3, medication.getLaboratory());
            statement.setInt(4, medication.getTotalStock());
            statement.setInt(5, medication.getAvailableStock());
            statement.setDouble(6, medication.getUnitPrice());
            statement.setBoolean(7, medication.isActive());
            statement.setInt(8, medication.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new PetCareException("Error updating medication", e);
        }
    }

    @Override
    public Medication findByCode(String code) throws PetCareException {
        String sql = "SELECT * FROM medicamento WHERE codigo_medicamento = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, code);

            try (ResultSet resultSet = statement.executeQuery()) {
                // If nothing comes back, this medication code is free to use
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new PetCareException("Error looking up medication by code", e);
        }
    }

    @Override
    public Medication findById(int id) throws PetCareException {
        String sql = "SELECT * FROM medicamento WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new PetCareException("Error looking up medication by id", e);
        }
    }

    @Override
    public List<Medication> findAll() throws PetCareException {
        String sql = "SELECT * FROM medicamento";
        List<Medication> medications = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Looping through every row and turning it into a Medication object
            while (resultSet.next()) {
                medications.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new PetCareException("Error listing medications", e);
        }

        return medications;
    }

    @Override
    public List<Medication> findByCategoryOrLaboratory(String category, String laboratory) throws PetCareException {
        // Building the query dynamically depending on which filter was actually provided
        StringBuilder sql = new StringBuilder("SELECT * FROM medicamento WHERE 1 = 1");

        if (category != null && !category.isBlank()) {
            sql.append(" AND categoria = ?");
        }
        if (laboratory != null && !laboratory.isBlank()) {
            sql.append(" AND laboratorio = ?");
        }

        List<Medication> medications = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            // Keeping track of the placeholder position since it depends on which filters are active
            int index = 1;
            if (category != null && !category.isBlank()) {
                statement.setString(index++, category);
            }
            if (laboratory != null && !laboratory.isBlank()) {
                statement.setString(index++, laboratory);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    medications.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new PetCareException("Error filtering medications", e);
        }

        return medications;
    }

    @Override
    public List<Medication> findWithAvailableStock() throws PetCareException {
        String sql = "SELECT * FROM medicamento WHERE stock_disponible > 0";
        List<Medication> medications = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                medications.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new PetCareException("Error listing medications with stock", e);
        }

        return medications;
    }

    // Small helper to avoid repeating this mapping in every method above
    private Medication mapRow(ResultSet resultSet) throws SQLException {
        Medication medication = new Medication();
        medication.setId(resultSet.getInt("id"));
        medication.setMedicationCode(resultSet.getString("codigo_medicamento"));
        medication.setName(resultSet.getString("nombre"));
        medication.setCategory(resultSet.getString("categoria"));
        medication.setLaboratory(resultSet.getString("laboratorio"));
        medication.setTotalStock(resultSet.getInt("stock_total"));
        medication.setAvailableStock(resultSet.getInt("stock_disponible"));
        medication.setUnitPrice(resultSet.getDouble("precio_unitario"));
        medication.setActive(resultSet.getBoolean("is_activo"));
        medication.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return medication;
    }
}