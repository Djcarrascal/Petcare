package com.petcare.dao.impl;

import com.petcare.dao.UserDao;
import com.petcare.enums.RoleUser;
import com.petcare.enums.StateUser;
import com.petcare.exception.PetCareException;
import com.petcare.model.User;
import com.petcare.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public int save(User user) throws PetCareException {
        String sql = "INSERT INTO usuario (username, password, nombre, rol, estado) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getName());
            // Enums get stored as their name(), e.g. RolUsuario.ADMIN becomes "ADMIN"
            statement.setString(4, user.getRole().name());
            statement.setString(5, user.getStatus().name());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new PetCareException("User was inserted but no id was returned");

        } catch (SQLException e) {
            throw new PetCareException("Error saving user", e);
        }
    }

    @Override
    public void update(User user) throws PetCareException {
        String sql = "UPDATE usuario SET nombre = ?, rol = ?, estado = ? WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Not touching username or password here, that would deserve its own dedicated method
            statement.setString(1, user.getName());
            statement.setString(2, user.getRole().name());
            statement.setString(3, user.getStatus().name());
            statement.setInt(4, user.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new PetCareException("Error updating user", e);
        }
    }

    @Override
    public void delete(int id) throws PetCareException {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new PetCareException("Error deleting user", e);
        }
    }

    @Override
    public User findByUsername(String username) throws PetCareException {
        String sql = "SELECT * FROM usuario WHERE username = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new PetCareException("Error looking up user by username", e);
        }
    }

    @Override
    public User findById(int id) throws PetCareException {
        String sql = "SELECT * FROM usuario WHERE id = ?";

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
            throw new PetCareException("Error looking up user by id", e);
        }
    }

    @Override
    public List<User> findAll() throws PetCareException {
        String sql = "SELECT * FROM usuario";
        List<User> users = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new PetCareException("Error listing users", e);
        }

        return users;
    }

    // Converts the text columns back into their matching enums
    private User mapRow(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setName(resultSet.getString("nombre"));
        // valueOf turns the stored text ("ADMIN") back into the enum constant
        user.setRole(RoleUser.valueOf(resultSet.getString("rol")));
        user.setStatus(StateUser.valueOf(resultSet.getString("estado")));
        user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}