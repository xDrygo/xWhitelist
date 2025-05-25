package org.eldrygo.XWhitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eldrygo.XWhitelist.API.XWhitelistAPI;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Managers.DBWhitelistManager;
import org.eldrygo.XWhitelist.Managers.FileWhitelistManager;
import org.eldrygo.XWhitelist.Managers.MWhitelistManager;
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
    public boolean useDatabase;
    private LogsUtils logsUtils;
    private ConfigManager configManager;
    private DBUtils dbUtils;
    private MWhitelistManager mWhitelistManager;

    @Override
    public void onEnable() {
        log = this.getLogger();
        version = getDescription().getVersion();
        config = getConfig();
        useDatabase = config.getBoolean("database.enable", false);
        this.configManager = new ConfigManager(this);
        ChatUtils chatUtils = new ChatUtils(this, configManager);
        this.mWhitelistManager = new MWhitelistManager(this);
        FileWhitelistManager fileWhitelistManager = new FileWhitelistManager(configManager, chatUtils);
        this.dbUtils = new DBUtils(this);
        DBWhitelistManager dbWhitelistManager = new DBWhitelistManager(chatUtils, dbUtils);
        this.logsUtils = new LogsUtils(this);
        XWhitelistAPI.init(this, configManager, mWhitelistManager, fileWhitelistManager, dbWhitelistManager);
        LoadUtils loadUtils = new LoadUtils(configManager, this, mWhitelistManager, dbUtils, fileWhitelistManager, dbWhitelistManager, chatUtils);

        loadUtils.loadFeatures();
        logsUtils.sendRunMessage();
        logsUtils.sendStartupMessage();
    }

    @Override
    public void onDisable() {
        if (dbUtils != null) {
            dbUtils.unloadDatabase();
        }
        if (logsUtils != null) {
            logsUtils.sendShutdownMessage();
        }
    }
    public boolean isDataBaseEnabled() { return useDatabase; }
    public boolean isPlaceholderAPIEnabled() { return workingPlaceholderAPI; }
    public Connection getConnection() { return connection; }
    public String getPrefix() {return configManager.getPrefix();}
    public void setConnection(Connection newConnection) { this.connection = newConnection; }
    public ConfigManager getConfigManager() { return configManager; }
    public MWhitelistManager getMWhitelistManager() { return mWhitelistManager; }
}
