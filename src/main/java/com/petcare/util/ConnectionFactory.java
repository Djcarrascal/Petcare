/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petcare.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConnectionFactory.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException("No se encontró el archivo db.properties en el classpath");
            }
            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la configuración de la base de datos", e);
        }
    }

    private ConnectionFactory() {
        // Clase de utilidad: no se debe instanciar
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPERTIES.getProperty("db.url");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}