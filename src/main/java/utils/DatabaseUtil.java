package utils;

import org.slf4j.Logger;
import java.sql.*;

public class DatabaseUtil {

    private static Connection connection;

    private static final Logger logger = LoggerUtil.getLogger(DatabaseUtil.class);

    public static void connect(String host, int port, String dbname, String user, String password) {
        try {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;

            connection = DriverManager.getConnection(url, user, password);
            logger.info("Connected to PostgreSQL!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    public static ResultSet executeQuery(String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            throw new RuntimeException("Query failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unused")
    public static int executeUpdate(String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            throw new RuntimeException("Update failed: " + e.getMessage(), e);
        }
    }

    public static void close() {
        try {
            if (connection != null) connection.close();
            logger.info("Database connection closed.");
        } catch (Exception ignored) {}
    }
}