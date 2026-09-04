package com.petcare.dao.impl;

import com.petcare.dao.PetDao;
import com.petcare.exception.PetCareException;
import com.petcare.model.Owner;
import com.petcare.model.Pet;
import com.petcare.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PetDaoImpl implements PetDao {

    // Reusing OwnerDaoImpl instead of duplicating the owner mapping logic here
    private final OwnerDaoImpl ownerDao = new OwnerDaoImpl();

    @Override
    public int save(Pet pet) throws PetCareException {
        String sql = "INSERT INTO mascota (historia_clinica, nombre, especie, raza, edad, propietario_id, is_activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, pet.getMedicalRecordNumber());
            statement.setString(2, pet.getName());
            statement.setString(3, pet.getSpecies());
            statement.setString(4, pet.getBreed());
            statement.setInt(5, pet.getAge());
            // We only need the owner's id here, the full object is only needed when reading back
            statement.setInt(6, pet.getOwner().getId());
            statement.setBoolean(7, pet.isActive());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new PetCareException("Pet was inserted but no id was returned");

        } catch (SQLException e) {
            throw new PetCareException("Error saving pet", e);
        }
    }

    @Override
    public void update(Pet pet) throws PetCareException {
        String sql = "UPDATE mascota SET nombre = ?, especie = ?, raza = ?, edad = ?, is_activo = ? "
                + "WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Not updating "historia_clinica" or "propietario_id" here on purpose,
            // reassigning a pet to another owner is a different, more sensitive operation
            statement.setString(1, pet.getName());
            statement.setString(2, pet.getSpecies());
            statement.setString(3, pet.getBreed());
            statement.setInt(4, pet.getAge());
            statement.setBoolean(5, pet.isActive());
            statement.setInt(6, pet.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new PetCareException("Error updating pet", e);
        }
    }

    @Override
    public Pet findByMedicalRecordNumber(String medicalRecordNumber) throws PetCareException {
        String sql = "SELECT * FROM mascota WHERE historia_clinica = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, medicalRecordNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new PetCareException("Error looking up pet by medical record number", e);
        }
    }

    @Override
    public Pet findById(int id) throws PetCareException {
        String sql = "SELECT * FROM mascota WHERE id = ?";

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
            throw new PetCareException("Error looking up pet by id", e);
        }
    }

    @Override
    public List<Pet> findByOwnerId(int ownerId) throws PetCareException {
        String sql = "SELECT * FROM mascota WHERE propietario_id = ?";
        List<Pet> pets = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ownerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pets.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new PetCareException("Error listing pets by owner", e);
        }

        return pets;
    }

    // Maps a row and also rebuilds the full Owner object, not just its id
    private Pet mapRow(ResultSet resultSet) throws SQLException, PetCareException {
        Pet pet = new Pet();
        pet.setId(resultSet.getInt("id"));
        pet.setMedicalRecordNumber(resultSet.getString("historia_clinica"));
        pet.setName(resultSet.getString("nombre"));
        pet.setSpecies(resultSet.getString("especie"));
        pet.setBreed(resultSet.getString("raza"));
        pet.setAge(resultSet.getInt("edad"));
        pet.setActive(resultSet.getBoolean("is_activo"));
        pet.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

        // This is the extra step Medication and Owner didn't need: fetching the related object
        Owner owner = ownerDao.findById(resultSet.getInt("propietario_id"));
        pet.setOwner(owner);

        return pet;
    }
}