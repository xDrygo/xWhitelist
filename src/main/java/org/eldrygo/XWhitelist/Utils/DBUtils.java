package org.eldrygo.XWhitelist.Utils;

import org.eldrygo.XWhitelist.XWhitelist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private final XWhitelist plugin;

    public DBUtils(XWhitelist plugin) {
        this.plugin = plugin;
    }

    public void connectToDatabase() {
        String host = plugin.getConfig().getString("mysql.host");
        int port = plugin.getConfig().getInt("mysql.port");
        String database = plugin.getConfig().getString("mysql.database");
        String username = plugin.getConfig().getString("mysql.user");
        String password = plugin.getConfig().getString("mysql.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";

        try {
            plugin.log.info("🔌 Attempting to connect to the database...");
            Connection connection = DriverManager.getConnection(url, username, password);
            plugin.setConnection(connection);
            plugin.log.info("✅ Successfully connected to the MySQL database.");
        } catch (SQLException e) {
            plugin.log.severe("❌ MySQL connection error: " + e.getMessage());
        }
    }

    public void createTableIfNotExists() {
        try (Statement stmt = plugin.getConnection().createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS whitelist (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(16) NOT NULL UNIQUE, " +
                    "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");
            plugin.log.info("✅ 'whitelist' table verified or created successfully.");
        } catch (SQLException e) {
            plugin.log.severe("❌ Error creating 'whitelist' table: " + e.getMessage());
        }
    }

    public void reloadDatabaseConnection() {
        unloadDatabase();
        connectToDatabase();
    }

    public void unloadDatabase() {
        try {
            Connection connection = plugin.getConnection();
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.setConnection(null);
                plugin.log.info("📴 Database connection closed successfully.");
            }
        } catch (SQLException e) {
            plugin.log.severe("❌ Error closing the database connection: " + e.getMessage());
        }
    }
}
