package org.eldrygo.XWhitelist.Utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bukkit.configuration.ConfigurationSection;
import org.eldrygo.XWhitelist.XWhitelist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private final XWhitelist plugin;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private String dbType;

    public DBUtils(XWhitelist plugin) {
        this.plugin = plugin;
    }

    public void connectToDatabase() {
        dbType = plugin.getConfig().getString("database.type", "mysql").toLowerCase();

        if (dbType.equals("mongodb")) {
            String connectionString = plugin.getConfig().getString("database.mongodb_url");
            if (connectionString == null || connectionString.isEmpty()) {
                plugin.log.severe("❌ No MongoDB URI found in config.");
                return;
            }
            connectMongoDB(connectionString);
            return;
        }

        if (!dbType.equals("mysql")) {
            plugin.log.severe("❌ Unsupported database type: " + dbType);
            return;
        }

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("database.credentials");
        if (section == null) {
            plugin.log.severe("❌ Missing 'database.credentials' section in config.");
            return;
        }

        String host = section.getString("host");
        int port = section.getInt("port");
        String database = section.getString("database");
        String username = section.getString("username");
        String password = section.getString("password");

        if (host == null || host.isEmpty() || database == null || database.isEmpty()
                || username == null || username.isEmpty() || password == null || password.isEmpty()) {
            plugin.log.severe("❌ Missing required credential fields in config.");
            return;
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
        connectMySQL(url, username, password);
    }

    private void connectMySQL(String url, String user, String pass) {
        try {
            plugin.log.info("🔌 Connecting to MySQL database...");
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, user, pass);
            plugin.setConnection(connection);
            plugin.log.info("✅ Connected to MySQL database.");
        } catch (SQLException | ClassNotFoundException e) {
            plugin.log.severe("❌ MySQL connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void connectMongoDB(String uri) {
        try {
            plugin.log.info("🔌 Connecting to MongoDB...");
            mongoClient = MongoClients.create(uri);

            String dbName = getDatabaseNameFromUri(uri);
            if (dbName == null || dbName.isEmpty()) {
                plugin.log.warning("⚠️ URI doesn't specify a database. Using fallback 'xwhitelist'.");
                dbName = "xwhitelist";
            }

            mongoDatabase = mongoClient.getDatabase(dbName);
            plugin.log.info("✅ Connected to MongoDB database.");
        } catch (Exception e) {
            plugin.log.severe("❌ MongoDB connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void createTableIfNotExists() {
        if (dbType.equals("mongodb")) {
            plugin.log.info("📁 MongoDB is schemaless — no need to create tables.");
            return;
        }

        Connection conn = plugin.getConnection();
        if (conn == null) {
            plugin.log.severe("❌ No active SQL connection. Cannot create table.");
            return;
        }

        String createSQL = "CREATE TABLE IF NOT EXISTS whitelist (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(16) NOT NULL UNIQUE, " +
                "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createSQL);
            plugin.log.info("✅ 'whitelist' table created or already exists.");
        } catch (SQLException e) {
            plugin.log.severe("❌ Error creating table: " + e.getMessage());
        }
    }

    public void unloadDatabase() {
        try {
            Connection connection = plugin.getConnection();
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.setConnection(null);
                plugin.log.info("📴 SQL connection closed.");
            }
        } catch (SQLException e) {
            plugin.log.severe("❌ Error closing SQL connection: " + e.getMessage());
        }

        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            mongoDatabase = null;
            plugin.log.info("📴 MongoDB connection closed.");
        }
    }

    public Object getDatabaseConnection() {
        return dbType.equals("mongodb") ? mongoDatabase : plugin.getConnection();
    }

    public void reloadDatabaseConnection() {
        try {
            plugin.log.info("🔄 Reloading database connection...");
            unloadDatabase();
            connectToDatabase();
        } catch (Exception e) {
            plugin.log.severe("❌ Error while reloading database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getDatabaseNameFromUri(String uri) {
        try {
            String[] parts = uri.split("\\?");
            String withoutParams = parts[0];
            String[] uriParts = withoutParams.split("/");
            return uriParts[uriParts.length - 1];
        } catch (Exception e) {
            plugin.log.warning("⚠️ Could not parse database name from URI. Using 'xwhitelist' as fallback.");
            return "xwhitelist";
        }
    }

    public String getDBType() {
        return dbType;
    }
}
