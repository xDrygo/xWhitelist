package org.eldrygo.XWhitelist.API;

import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Managers.DBWhitelistManager;
import org.eldrygo.XWhitelist.Managers.FileWhitelistManager;
import org.eldrygo.XWhitelist.Managers.MWhitelistManager;
import org.eldrygo.XWhitelist.XWhitelist;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XWhitelistAPI {
    private static MWhitelistManager mWhitelistManager;
    private static XWhitelist plugin;
    private static ConfigManager configManager;
    private static FileWhitelistManager fileWhitelistManager;
    private static DBWhitelistManager dbWhitelistManager;

    public static void init(XWhitelist plugin, ConfigManager configManager, MWhitelistManager mWhitelistManager, FileWhitelistManager fileWhitelistManager, DBWhitelistManager dbWhitelistManager) {
        XWhitelistAPI.mWhitelistManager = mWhitelistManager;
        XWhitelistAPI.plugin = plugin;
        XWhitelistAPI.configManager = configManager;
        XWhitelistAPI.fileWhitelistManager = fileWhitelistManager;
        XWhitelistAPI.dbWhitelistManager = dbWhitelistManager;
    }

    public static boolean isWhitelistActive() {
        return plugin.getConfig().getBoolean("enabled", false);
    }

    public static void toggleWhitelist() {
        boolean currentStatus = plugin.getConfig().getBoolean("enabled", false);
        plugin.getConfig().set("enabled", !currentStatus);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public static boolean isPlayerInWhitelist(String playerName) {
        if (plugin.useDatabase) {
            return dbWhitelistManager.isPlayerWhitelisted(playerName);
        } else {
            return fileWhitelistManager.isPlayerWhitelisted(playerName);
        }
    }

    public static void addPlayerToWhitelist(String playerName) {
        if (plugin.useDatabase) {
            dbWhitelistManager.addPlayer(playerName, null);
        } else {
            fileWhitelistManager.addPlayer(playerName, null);
        }
    }

    public static void removePlayerFromWhitelist(String playerName) {
        if (plugin.useDatabase) {
            dbWhitelistManager.removePlayer(playerName, null);
        } else {
            fileWhitelistManager.removePlayer(playerName, null);
        }
    }

    public static List<String> listWhitelist() {
        if (plugin.useDatabase) {
            return dbWhitelistManager.getWhitelistedPlayers();
        } else {
            return configManager.getWhitelistConfig().getStringList("whitelist");
        }
    }

    public static void clearWhitelist() {
        if (plugin.useDatabase) {
            dbWhitelistManager.cleanup(null);
        } else {
            fileWhitelistManager.cleanup(null);
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

    public static void setDbWhitelistManager(DBWhitelistManager dbWhitelistManager) {
        XWhitelistAPI.dbWhitelistManager = dbWhitelistManager;
    }
}
