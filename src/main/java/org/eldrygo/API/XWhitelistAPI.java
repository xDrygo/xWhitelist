package org.eldrygo.API;

import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.Managers.ConfigManager;
import org.eldrygo.Managers.MWhitelistManager;
import org.eldrygo.XWhitelist;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XWhitelistAPI {
    private final MWhitelistManager mWhitelistManager;
    private final Connection databaseConnection;
    private final XWhitelist plugin;
    private final ConfigManager configManager;
    private final YamlConfiguration whitelistFile;

    public XWhitelistAPI(XWhitelist plugin, Connection databaseConnection, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.mWhitelistManager = new MWhitelistManager(plugin);
        this.databaseConnection = databaseConnection;
        this.whitelistFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "whitelist.yml"));
    }

    public boolean isWhitelistActive() {
        return plugin.getConfig().getBoolean("enabled", false);
    }

    public void toggleWhitelist() {
        boolean currentStatus = plugin.getConfig().getBoolean("enabled", false);
        plugin.getConfig().set("enabled", !currentStatus);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public List<String> getFileWhitelist() {
        return whitelistFile.getStringList("whitelist");
    }

    public boolean isPlayerInWhitelist(String playerName) {
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

    public void addPlayerToWhitelist(String playerName) {
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

    public void removePlayerFromWhitelist(String playerName) {
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

    public List<String> listWhitelist() {
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

    public void clearWhitelist() {
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
    public boolean isMaintenanceWhitelistActive() {
        return mWhitelistManager.isMaintenanceWhitelistActive();
    }

    public void toggleMaintenanceWhitelist() {
        mWhitelistManager.toggleMaintenanceWhitelist();
    }

    public List<String> getMaintenanceWhitelist() {
        return mWhitelistManager.getMaintenanceWhitelist();
    }

    public boolean isPlayerInMaintenanceWhitelist(String playerName) {
        return mWhitelistManager.isPlayerInMaintenanceWhitelist(playerName);
    }

    public void addPlayerToMaintenanceWhitelist(String playerName) {
        List<String> whitelist = configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
        whitelist.add(playerName);
        configManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
        configManager.saveMaintenanceWhitelist();
        configManager.reloadMaintenanceWhitelist();
    }

    public void removePlayerFromMaintenanceWhitelist(String playerName) {
        List<String> whitelist = configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
        whitelist.remove(playerName);
        configManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
        configManager.saveMaintenanceWhitelist();
        configManager.reloadMaintenanceWhitelist();
    }

    public void cleanupMaintenanceWhitelist() {
        configManager.getMaintenanceWhitelistConfig().set("whitelist", null);
        configManager.saveMaintenanceWhitelist();
        configManager.reloadMaintenanceWhitelist();
    }

    public List<String> listMaintenanceWhitelist() {
        return configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
    }
}
