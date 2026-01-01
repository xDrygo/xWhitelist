package org.eldrygo.XWhitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
//import org.eldrygo.XWhitelist.XWhitelistAPI;
import org.eldrygo.XWhitelist.API.XWhitelistAPI;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Managers.FileWhitelistManager;
import org.eldrygo.XWhitelist.Managers.MWhitelistManager;
import org.eldrygo.XWhitelist.Managers.MySQLWhitelistManager;
import org.eldrygo.XWhitelist.Utils.ChatUtils;
import org.eldrygo.XWhitelist.Utils.DBUtils;
import org.eldrygo.XWhitelist.Utils.LoadUtils;
import org.eldrygo.XWhitelist.Utils.LogsUtils;

import java.sql.Connection;
import java.util.logging.Logger;

public class XWhitelist extends JavaPlugin {

    public Connection connection;
    public String version;
    public Logger log;
    public FileConfiguration config;
    public boolean workingPlaceholderAPI = false;
    public boolean useMySQL;
    private LogsUtils logsUtils;
    private ConfigManager configManager;
    private DBUtils dBUtils;
    private MWhitelistManager mWhitelistManager;

    @Override
    public void onEnable() {
        log = this.getLogger();
        version = getDescription().getVersion();
        config = getConfig();
        boolean tempUseMySQL = config.getBoolean("mysql.enable", false);
        if (tempUseMySQL) {
            if (dBUtils.connectToDatabase()) {
                useMySQL = true;
            } else {
                useMySQL = false;
                dBUtils.unloadDatabase();
                getLogger().severe("Database connection failed. Using file whitelist.");
                config.set("mysql.enable", false);
                saveConfig();
            }
        }
        this.configManager = new ConfigManager(this);
        ChatUtils chatUtils = new ChatUtils(this, configManager);
        this.mWhitelistManager = new MWhitelistManager(this);
        FileWhitelistManager fileWhitelistManager = new FileWhitelistManager(configManager, chatUtils);
        MySQLWhitelistManager mySQLWhitelistManager = new MySQLWhitelistManager(chatUtils);
        this.logsUtils = new LogsUtils(this);
        this.dBUtils = new DBUtils(this);
        LoadUtils loadUtils = new LoadUtils(configManager, this, mWhitelistManager, dBUtils, fileWhitelistManager, mySQLWhitelistManager, chatUtils);
        XWhitelistAPI xWhitelistAPI = new XWhitelistAPI(this);

        loadUtils.loadFeatures();
        logsUtils.sendRunMessage();
        logsUtils.sendStartupMessage();
    }

    @Override
    public void onDisable() {
        dBUtils.unloadDatabase();
        logsUtils.sendShutdownMessage();
    }
    public boolean isMySQLEnabled() { return useMySQL; }
    public boolean isPlaceholderAPIEnabled() { return workingPlaceholderAPI; }
    public Connection getConnection() { return connection; }
    public String getPrefix() {return configManager.getPrefix();}
    public void setConnection(Connection newConnection) { this.connection = newConnection; }
    public ConfigManager getConfigManager() { return configManager; }
    public MWhitelistManager getMWhitelistManager() { return mWhitelistManager; }
}
