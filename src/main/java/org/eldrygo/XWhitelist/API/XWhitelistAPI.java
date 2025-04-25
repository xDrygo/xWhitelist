package org.eldrygo.XWhitelist.API;

import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Managers.MWhitelistManager;
import org.eldrygo.XWhitelist.XWhitelist;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XWhitelistAPI {
    private static MWhitelistManager mWhitelistManager;
    private static Connection databaseConnection;
    private static XWhitelist plugin;
    private static ConfigManager configManager;
    private static YamlConfiguration whitelistFile;

    public XWhitelistAPI(XWhitelist plugin) {
        XWhitelistAPI.plugin = plugin;
        configManager = plugin.getConfigManager();
        mWhitelistManager = plugin.getMWhitelistManager();
        databaseConnection = plugin.getConnection();
        whitelistFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "whitelist.yml"));
    }

    public boolean isWhitelistActive() {
        return plugin.getConfig().getBoolean("enabled", false);
    }

    public static void toggleWhitelist() {
        boolean currentStatus = plugin.getConfig().getBoolean("enabled", false);
        plugin.getConfig().set("enabled", !currentStatus);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public static List<String> getFileWhitelist() {
        return whitelistFile.getStringList("whitelist");
    }

    public static boolean isPlayerInWhitelist(String playerName) {
        if (plugin.useMySQL) {
            try (PreparedStatement stmt = databaseConnection.prepareStatement("SELECT * FROM whitelist WHERE username = ?")) {
                stmt.setString(1, playerName);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return getFileWhitelist().contains(playerName);
        }
    }

    public static void addPlayerToWhitelist(String playerName) {
        if (plugin.useMySQL) {
            try {
                String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
                try (PreparedStatement checkStmt = databaseConnection.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, playerName);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        return;
                    }
                }
                String insertQuery = "INSERT INTO whitelist (username) VALUES (?)";
                try (PreparedStatement stmt = databaseConnection.prepareStatement(insertQuery)) {
                    stmt.setString(1, playerName);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");
            whitelist.add(playerName);
            configManager.getWhitelistConfig().set("whitelist", whitelist);
            configManager.saveWhitelistFile();
        }
    }

    public static void removePlayerFromWhitelist(String playerName) {
        if (plugin.useMySQL) {
            try {
                String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
                try (PreparedStatement checkStmt = databaseConnection.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, playerName);
                    ResultSet rs = checkStmt.executeQuery();
                }
                String deleteQuery = "DELETE FROM whitelist WHERE username = ?";
                try (PreparedStatement stmt = databaseConnection.prepareStatement(deleteQuery)) {
                    stmt.setString(1, playerName);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");
            whitelist.remove(playerName);
            configManager.getWhitelistConfig().set("whitelist", whitelist);
            configManager.saveWhitelistFile();
        }
    }

    public static List<String> listWhitelist() {
        if (plugin.useMySQL) {
            List<String> players = new ArrayList<>();
            try (Statement stmt = databaseConnection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT username FROM whitelist")) {
                while (rs.next()) {
                    players.add(rs.getString("username"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return players;
        } else {
            return configManager.getWhitelistConfig().getStringList("whitelist");
        }
    }

    public static void clearWhitelist() {
        if (plugin.useMySQL) {
            try (Statement stmt = databaseConnection.createStatement()) {
                stmt.executeUpdate("DELETE FROM whitelist");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            configManager.getWhitelistConfig().set("whitelist", null);
            configManager.saveWhitelistFile();
            configManager.loadWhitelistFile();
        }
    }

    // Métodos para la whitelist de mantenimiento
    public static boolean isMaintenanceWhitelistActive() {
        return mWhitelistManager.isMaintenanceWhitelistActive();
    }

    public static void toggleMaintenanceWhitelist() {
        mWhitelistManager.toggleMaintenanceWhitelist();
    }

    public static List<String> getMaintenanceWhitelist() {
        return mWhitelistManager.getMaintenanceWhitelist();
    }

    public static boolean isPlayerInMaintenanceWhitelist(String playerName) {
        return mWhitelistManager.isPlayerInMaintenanceWhitelist(playerName);
    }

    public static void addPlayerToMaintenanceWhitelist(String playerName) {
        List<String> whitelist = configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
        whitelist.add(playerName);
        configManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
        configManager.saveMaintenanceWhitelist();
        configManager.reloadMaintenanceWhitelist();
    }

    public static void removePlayerFromMaintenanceWhitelist(String playerName) {
        List<String> whitelist = configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
        whitelist.remove(playerName);
        configManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
        configManager.saveMaintenanceWhitelist();
        configManager.reloadMaintenanceWhitelist();
    }

    public static void cleanupMaintenanceWhitelist() {
        configManager.getMaintenanceWhitelistConfig().set("whitelist", null);
        configManager.saveMaintenanceWhitelist();
        configManager.reloadMaintenanceWhitelist();
    }

    public static List<String> listMaintenanceWhitelist() {
        return configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
    }
}
