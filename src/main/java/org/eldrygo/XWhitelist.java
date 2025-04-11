package org.eldrygo;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
//import org.eldrygo.API.XWhitelistAPI;
import org.eldrygo.Managers.ConfigManager;
import org.eldrygo.Managers.FileWhitelistManager;
import org.eldrygo.Managers.MWhitelistManager;
import org.eldrygo.Managers.MySQLWhitelistManager;
import org.eldrygo.Utils.ChatUtils;
import org.eldrygo.Utils.DBUtils;
import org.eldrygo.Utils.LoadUtils;
import org.eldrygo.Utils.LogsUtils;

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

    @Override
    public void onEnable() {
        log = this.getLogger();
        version = getDescription().getVersion();
        config = getConfig();
        useMySQL = config.getBoolean("mysql.enable", false);
        this.configManager = new ConfigManager(this);
        ChatUtils chatUtils = new ChatUtils(this, configManager);
        MWhitelistManager mWhitelistManager = new MWhitelistManager(this);
        FileWhitelistManager fileWhitelistManager = new FileWhitelistManager(configManager, chatUtils);
        MySQLWhitelistManager mySQLWhitelistManager = new MySQLWhitelistManager(chatUtils);
        this.logsUtils = new LogsUtils(this);
        this.dBUtils = new DBUtils(this);
        LoadUtils loadUtils = new LoadUtils(configManager, this, mWhitelistManager, dBUtils, fileWhitelistManager, mySQLWhitelistManager, chatUtils);
        //XWhitelistAPI xWhitelistAPI = new XWhitelistAPI(this, connection, configManager);

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
}
