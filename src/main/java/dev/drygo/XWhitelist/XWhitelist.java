package dev.drygo.XWhitelist;

import org.bukkit.plugin.java.JavaPlugin;
import dev.drygo.XWhitelist.API.XWhitelistAPI;
import dev.drygo.XWhitelist.Managers.ConfigManager;
import dev.drygo.XWhitelist.Managers.MWhitelistManager;
import dev.drygo.XWhitelist.Utils.ChatUtils;
import dev.drygo.XWhitelist.Utils.DBUtils;
import dev.drygo.XWhitelist.Utils.LoadUtils;
import dev.drygo.XWhitelist.Utils.LogsUtils;

import java.sql.Connection;

public class XWhitelist extends JavaPlugin {

    public static Connection connection;
    public static String prefix;
    public static String version;
    public static boolean workingPlaceholderAPI = false;
    public static boolean useMySQL;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();
        DBUtils.init(this);
        boolean tempUseMySQL = getConfig().getBoolean("mysql.enable", false);
        if (tempUseMySQL) {
            if (DBUtils.connectToDatabase()) {
                useMySQL = true;
            } else {
                useMySQL = false;
                DBUtils.unloadDatabase();
                getLogger().severe("Database connection failed. Using file whitelist.");
                getConfig().set("mysql.enable", false);
                saveConfig();
            }
        }
        ConfigManager.init(this);
        ChatUtils.init(this);
        MWhitelistManager.init(this);
        LogsUtils.init(this);
        LoadUtils.init(this);
        XWhitelistAPI.init(this);

        LoadUtils.loadFeatures();
        LogsUtils.sendRunMessage();
        LogsUtils.sendStartupMessage();
    }

    @Override
    public void onDisable() {
        DBUtils.unloadDatabase();
        LogsUtils.sendShutdownMessage();
    }
    public static boolean isMySQLEnabled() {
        return useMySQL;
    }
    public static boolean isPlaceholderAPIEnabled() {
        return workingPlaceholderAPI;
    }
    public static Connection getConnection() {
        return connection;
    }
    public static String getPrefix() {
        return prefix;
    }
    public static void setConnection(Connection newConnection) {
        connection = newConnection;
    }
}
