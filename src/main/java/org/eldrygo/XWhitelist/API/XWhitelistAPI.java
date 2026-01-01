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
    private static XWhitelist plugin;
    private static YamlConfiguration whitelistFile;

    public static void init(XWhitelist plugin) {
        XWhitelistAPI.plugin = plugin;
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
        if (XWhitelist.useMySQL) {
            try (PreparedStatement stmt = XWhitelist.getConnection().prepareStatement("SELECT * FROM whitelist WHERE username = ?")) {
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
        if (XWhitelist.useMySQL) {
            try {
                String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
                try (PreparedStatement checkStmt = XWhitelist.getConnection().prepareStatement(checkQuery)) {
                    checkStmt.setString(1, playerName);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        return;
                    }
                }
                String insertQuery = "INSERT INTO whitelist (username) VALUES (?)";
                try (PreparedStatement stmt = XWhitelist.getConnection().prepareStatement(insertQuery)) {
                    stmt.setString(1, playerName);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            List<String> whitelist = ConfigManager.getWhitelistConfig().getStringList("whitelist");
            whitelist.add(playerName);
            ConfigManager.getWhitelistConfig().set("whitelist", whitelist);
            ConfigManager.saveWhitelistFile();
        }
    }

    public static void removePlayerFromWhitelist(String playerName) {
        if (XWhitelist.useMySQL) {
            try {
                String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
                try (PreparedStatement checkStmt = XWhitelist.getConnection().prepareStatement(checkQuery)) {
                    checkStmt.setString(1, playerName);
                    ResultSet rs = checkStmt.executeQuery();
                }
                String deleteQuery = "DELETE FROM whitelist WHERE username = ?";
                try (PreparedStatement stmt = XWhitelist.getConnection().prepareStatement(deleteQuery)) {
                    stmt.setString(1, playerName);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            List<String> whitelist = ConfigManager.getWhitelistConfig().getStringList("whitelist");
            whitelist.remove(playerName);
            ConfigManager.getWhitelistConfig().set("whitelist", whitelist);
            ConfigManager.saveWhitelistFile();
        }
    }

    public static List<String> listWhitelist() {
        if (XWhitelist.useMySQL) {
            List<String> players = new ArrayList<>();
            try (Statement stmt = XWhitelist.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT username FROM whitelist")) {
                while (rs.next()) {
                    players.add(rs.getString("username"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return players;
        } else {
            return ConfigManager.getWhitelistConfig().getStringList("whitelist");
        }
    }

    public static void clearWhitelist() {
        if (XWhitelist.useMySQL) {
            try (Statement stmt = XWhitelist.getConnection().createStatement()) {
                stmt.executeUpdate("DELETE FROM whitelist");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            ConfigManager.getWhitelistConfig().set("whitelist", null);
            ConfigManager.saveWhitelistFile();
            ConfigManager.loadWhitelistFile();
        }
    }

    public static boolean isMaintenanceWhitelistActive() {
        return MWhitelistManager.isMaintenanceWhitelistActive();
    }
    public static void toggleMaintenanceWhitelist() {
        MWhitelistManager.toggleMaintenanceWhitelist();
    }
    public static List<String> getMaintenanceWhitelist() {
        return MWhitelistManager.getMaintenanceWhitelist();
    }
    public static boolean isPlayerInMaintenanceWhitelist(String playerName) {
        return MWhitelistManager.isPlayerInMaintenanceWhitelist(playerName);
    }

    public static void addPlayerToMaintenanceWhitelist(String playerName) {
        List<String> whitelist = ConfigManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
        whitelist.add(playerName);
        ConfigManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
        ConfigManager.saveMaintenanceWhitelist();
        ConfigManager.reloadMaintenanceWhitelist();
    }

    public static void removePlayerFromMaintenanceWhitelist(String playerName) {
        List<String> whitelist = ConfigManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
        whitelist.remove(playerName);
        ConfigManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
        ConfigManager.saveMaintenanceWhitelist();
        ConfigManager.reloadMaintenanceWhitelist();
    }

    public static void cleanupMaintenanceWhitelist() {
        ConfigManager.getMaintenanceWhitelistConfig().set("whitelist", null);
        ConfigManager.saveMaintenanceWhitelist();
        ConfigManager.reloadMaintenanceWhitelist();
    }

    public static List<String> listMaintenanceWhitelist() {
        return ConfigManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
    }
}
