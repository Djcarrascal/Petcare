package com.petcare.dao.impl;

import com.petcare.dao.OwnerDao;
import com.petcare.exception.PetCareException;
import com.petcare.model.Owner;
import com.petcare.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OwnerDaoImpl implements OwnerDao {

    @Override
    public int save(Owner owner) throws PetCareException {
        String sql = "INSERT INTO propietario (nombre, documento, telefono, email, direccion, is_activo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, owner.getName());
            statement.setString(2, owner.getDocument());
            statement.setString(3, owner.getPhone());
            statement.setString(4, owner.getEmail());
            statement.setString(5, owner.getAddress());
            statement.setBoolean(6, owner.isActive());

            statement.executeUpdate();

            // Same pattern as MedicationDao: read back the id MySQL generated
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new PetCareException("Owner was inserted but no id was returned");

        } catch (SQLException e) {
            throw new PetCareException("Error saving owner", e);
        }
    }

    @Override
    public void update(Owner owner) throws PetCareException {
        String sql = "UPDATE propietario SET nombre = ?, telefono = ?, email = ?, direccion = ?, is_activo = ? "
                + "WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Not updating "documento" here on purpose, this should stay fixed once created
            statement.setString(1, owner.getName());
            statement.setString(2, owner.getPhone());
            statement.setString(3, owner.getEmail());
            statement.setString(4, owner.getAddress());
            statement.setBoolean(5, owner.isActive());
            statement.setInt(6, owner.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new PetCareException("Error updating owner", e);
        }
    }

    @Override
    public Owner findByDocument(String document) throws PetCareException {
        String sql = "SELECT * FROM propietario WHERE documento = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, document);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new PetCareException("Error looking up owner by document", e);
        }
    }

    @Override
    public Owner findById(int id) throws PetCareException {
        String sql = "SELECT * FROM propietario WHERE id = ?";

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
            throw new PetCareException("Error looking up owner by id", e);
        }
    }

    @Override
    public List<Owner> findAll() throws PetCareException {
        String sql = "SELECT * FROM propietario";
        List<Owner> owners = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                owners.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new PetCareException("Error listing owners", e);
        }

        return owners;
    }

    // Same reusable mapping idea as in MedicationDaoImpl
    private Owner mapRow(ResultSet resultSet) throws SQLException {
        Owner owner = new Owner();
        owner.setId(resultSet.getInt("id"));
        owner.setName(resultSet.getString("nombre"));
        owner.setDocument(resultSet.getString("documento"));
        owner.setPhone(resultSet.getString("telefono"));
        owner.setEmail(resultSet.getString("email"));
        owner.setAddress(resultSet.getString("direccion"));
        owner.setActive(resultSet.getBoolean("is_activo"));
        owner.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return owner;
    }
}   