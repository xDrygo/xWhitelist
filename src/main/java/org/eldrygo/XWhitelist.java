package org.eldrygo;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.eldrygo.API.XWhitelistAPI;
import org.eldrygo.Managers.ConfigManager;
import org.eldrygo.Utils.ChatUtils;
import org.eldrygo.Utils.DBUtils;
import org.eldrygo.Utils.LoadUtils;
import org.eldrygo.Utils.LogsUtils;

import java.sql.*;
import java.util.logging.Logger;

public class XWhitelist extends JavaPlugin implements Listener {

    public Connection connection;
    public String version = getDescription().getVersion();
    public Logger log;
    public FileConfiguration config;
    public boolean workingPlaceholderAPI = false;
    public boolean useMySQL;
    private LogsUtils logsUtils;
    private ConfigManager configManager;
    private final LoadUtils loadUtils;
    private final DBUtils dBUtils;
    private XWhitelistAPI xWhitelistAPI;
    private final ChatUtils chatUtils;

    public XWhitelist(LoadUtils loadUtils, DBUtils dBUtils, XWhitelistAPI xWhitelistAPI, ChatUtils chatUtils) {
        this.loadUtils = loadUtils;
        this.dBUtils = dBUtils;
        this.xWhitelistAPI = xWhitelistAPI;
        this.chatUtils = chatUtils;
    }
    public boolean isMySQLEnabled() {
        return useMySQL;
    }
    public boolean isPlaceholderAPIEnabled() {
        return workingPlaceholderAPI;
    }
    public Connection getConnection() {
        return connection;
    }
    public String getPrefix() {return configManager.prefix;}

    @Override
    public void onEnable() {
        log = this.getLogger();
        this.useMySQL = config.getBoolean("mysql.enable", false);
        this.configManager = new ConfigManager(this, configManager, chatUtils);
        this.logsUtils = new LogsUtils(this);
        this.xWhitelistAPI = new XWhitelistAPI(this, connection, configManager);
        loadUtils.loadFeatures();
        logsUtils.sendRunMessage();
        logsUtils.sendStartupMessage();
    }

    @Override
    public void onDisable() {
        dBUtils.unloadDatabase();
        logsUtils.sendShutdownMessage();
    }
}