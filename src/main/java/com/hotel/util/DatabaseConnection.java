package com.hotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    public static boolean connect() {
        try {
            String url = "jdbc:postgresql://localhost:5432/hotel_db";
            connection = DriverManager.getConnection(url, "hotel_user", "hotel_password");
            return true;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    public static Connection getConnection() {
        return connection;
    }
    
    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
