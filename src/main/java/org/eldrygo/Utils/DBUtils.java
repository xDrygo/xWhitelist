package org.eldrygo.Utils;

import org.eldrygo.XWhitelist;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private static XWhitelist plugin;

    public DBUtils(XWhitelist plugin) {
        DBUtils.plugin = plugin;
    }

    public void createTableIfNotExists() {
        try (Statement stmt = plugin.getConnection().createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS whitelist (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(16) NOT NULL UNIQUE, " +
                    "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");
            plugin.log.info("✅ 'whitelist' table verified in the database.");
        } catch (SQLException e) {
            plugin.log.severe("❌ Error creating table in MySQL: " + e.getMessage());
        }
    }
    public static void connectToDatabase() {
        String host = plugin.getConfig().getString("mysql.host");
        int port = plugin.getConfig().getInt("mysql.port");
        String database = plugin.getConfig().getString("mysql.database");
        String username = plugin.getConfig().getString("mysql.user");
        String password = plugin.getConfig().getString("mysql.password");
        try {
            plugin.log.info("Trying to connect with database...");
            plugin.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            plugin.log.info("✅ Connected with MySQL database.");
        } catch (SQLException e) {
            plugin.log.severe("❌ MySQL connection error: " + e.getMessage());
        }
    }
    public static void reloadDatabaseConnection() {
        try {
            if (plugin.getConnection() != null && !plugin.getConnection().isClosed()) {
                plugin.getConnection().close();
            }

            String host = plugin.getConfig().getString("mysql.host");
            int port = plugin.getConfig().getInt("mysql.port");
            String database = plugin.getConfig().getString("mysql.database");
            String user = plugin.getConfig().getString("mysql.user");
            String password = plugin.getConfig().getString("mysql.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            plugin.connection = DriverManager.getConnection(url, user, password);

            plugin.getLogger().info("Database connection reset.");

        } catch (SQLException e) {
            plugin.getLogger().severe("Error connecting to database: " + e.getMessage());
        }
    }
    public void unloadDatabase() {
        try {
            if (plugin.getConnection() != null && !plugin.getConnection().isClosed()) {
                plugin.getConnection().close();
                plugin.log.info("📴 Disconnected from database.");
            }
        } catch (SQLException e) {
            plugin.log.severe("❌ Error closing MySQL connection: " + e.getMessage());
        }
    }
}
