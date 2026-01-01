package org.eldrygo.XWhitelist.Utils;

import org.eldrygo.XWhitelist.XWhitelist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private static XWhitelist plugin;

    public static void init(XWhitelist plugin) {
        DBUtils.plugin = plugin;
    }

    public static boolean connectToDatabase() {
        String host = plugin.getConfig().getString("mysql.host");
        int port = plugin.getConfig().getInt("mysql.port");
        String database = plugin.getConfig().getString("mysql.database");
        String username = plugin.getConfig().getString("mysql.user");
        String password = plugin.getConfig().getString("mysql.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        try {
            plugin.getLogger().info("🔌 Attempting to connect to the database...");
            Connection connection = DriverManager.getConnection(url, username, password);
            XWhitelist.setConnection(connection);
            plugin.getLogger().info("✅ Successfully connected to the MySQL database.");
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("❌ MySQL connection error: " + e.getMessage());
            XWhitelist.setConnection(null);
            return false;
        }
    }

    public static void createTableIfNotExists() {
        Connection connection = XWhitelist.getConnection();

        if (connection == null) {
            plugin.getLogger().severe("❌ Cannot create tables: database connection is null.");
            return;
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS whitelist (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(16) NOT NULL UNIQUE,
                added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """);
            plugin.getLogger().info("✅ 'whitelist' table verified or created successfully.");
        } catch (SQLException e) {
            plugin.getLogger().severe("❌ Error creating 'whitelist' table: " + e.getMessage());
        }
    }

    public static void reloadDatabaseConnection() {
        unloadDatabase();
        connectToDatabase();
    }

    public static void unloadDatabase() {
        try {
            Connection connection = XWhitelist.getConnection();
            if (connection != null && !connection.isClosed()) {
                connection.close();
                XWhitelist.setConnection(null);
                plugin.getLogger().info("📴 Database connection closed successfully.");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("❌ Error closing the database connection: " + e.getMessage());
        }
    }
}
