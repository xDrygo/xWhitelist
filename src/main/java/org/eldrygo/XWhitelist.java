package org.eldrygo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.eldrygo.MWhitelist.MWhitelist;
import org.eldrygo.MWhitelist.MWhitelistCommand;
import org.eldrygo.placeholders.XWhitelistExpansion;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

public class XWhitelist extends JavaPlugin implements Listener {

    private Connection connection;
    public String version = getDescription().getVersion();
    private String host, database, username, password;
    private int port;
    private Logger log;
    private FileConfiguration config;
    private File maintenanceWhitelistFile;
    private FileConfiguration maintenanceWhitelistConfig;
    private MWhitelist mWhitelist;
    private boolean workingPlaceholderAPI = false;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        log = this.getLogger();

        saveDefaultConfig();
        loadMaintenanceWhitelist();
        this.config = getConfig();
        mWhitelist = new MWhitelist(this);


        host = config.getString("mysql.host");
        port = config.getInt("mysql.port");
        database = config.getString("mysql.database");
        username = config.getString("mysql.user");
        password = config.getString("mysql.password");

        boolean firstRun = config.getBoolean("plugin.first_run", true);
        if (firstRun) {
            onFirstRun();

            config.set("plugin.first_run", false);
            saveConfig();
        }

        connectToDatabase();
        createTableIfNotExists();
        loadPlaceholderAPI();

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);

        // Register commands
        loadCommands();

        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #a0ff72has been enabled! &fVersion: " + version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #fff18dThanks for use my plugin! - Drygo"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+====================================================================+"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("&8                             [#ff0000&lX&r&lWhitelist&8]"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18d                Thank you for using XWhitelist plugin!"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18d   You can find all the commands and permission from the plugin in"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dthe README.md of the github repository, you can find it in config.yml"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffff  If you have any questions or suggestions, please contact me on X!"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+====================================================================+"));
    }

    public void connectToDatabase() {
        try {
            log.info("Trying to connect with database...");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            log.info("✅ Connected with MySQL database.");
        } catch (SQLException e) {
            log.severe("❌ MySQL connection error: " + e.getMessage());
        }
    }
    private void loadPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XWhitelistExpansion(this).register();
            log.info("✅ PlaceholderAPI detected. Placeholders will work.");
            workingPlaceholderAPI = true;
        } else {
            log.warning("⚠ PlaceholderAPI not detected. Placeholders will not work.");
        }}
    public boolean isPlaceholderAPIEnabled() {
        return workingPlaceholderAPI;
    }
    private void loadCommands() {
        getCommand("xwhitelist").setExecutor(new XWhitelistCommand(this));
        if (getCommand("xwhitelist") == null) {
            getLogger().severe("❌ Error: xWhitelist command is no registered in plugin.yml");
        }
        getCommand("mwhitelist").setExecutor(new MWhitelistCommand(this, mWhitelist));
        if (getCommand("mwhitelist") == null) {
            getLogger().severe("❌ Error: mWhitelist command is no registered in plugin.yml");
        }
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS whitelist (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(16) NOT NULL UNIQUE, " +
                    "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");
            log.info("✅ 'whitelist' table verified in the database.");
        } catch (SQLException e) {
            log.severe("❌ Error creating table in MySQL: " + e.getMessage());
        }
    }
    public MWhitelist getMWhitelist() {
        return mWhitelist;
    }

    @Override
    public void onDisable() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("📴 Disconnected from database.");
            }
        } catch (SQLException e) {
            log.severe("❌ Error closing MySQL connection: " + e.getMessage());
        }
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #ff7272has been disabled! &fVersion: " + version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #fff18dThanks for use my plugin! - Drygo"));
    }

    public Connection getConnection() {
        return connection;
    }

    public void reloadMessages() {
        this.config = getConfig();
    }

    private void onFirstRun() {
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+==============================================================+"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("&8                      [#ff0000&lX&r&lWhitelist&8]"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dHello! Since this is the first time I've started XWhitelist, the"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dplugin has been disabled due to not having a database configured."));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffffPlease configure the database in the config.yml file and restart."));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+==============================================================+"));
    }

    // Load and manage the maintenance-whitelist.yml file
    public void loadMaintenanceWhitelist() {
        maintenanceWhitelistFile = new File(getDataFolder(), "maintenance_whitelist.yml");

        if (!maintenanceWhitelistFile.exists()) {
            saveResource("maintenance_whitelist.yml", false);
            getLogger().info("✅ The maintenance_whitelist.yml file did not exist, it has been created.");
        } else {
            getLogger().info("✅ The maintenance_whitelist.yml file has been loaded successfully.");
        }

        maintenanceWhitelistConfig = YamlConfiguration.loadConfiguration(maintenanceWhitelistFile);
    }
    public void reloadMaintenanceWhitelist() {
        maintenanceWhitelistConfig = YamlConfiguration.loadConfiguration(maintenanceWhitelistFile);
        getLogger().info("🔄 maintenance-whitelist.yml was reloaded successfully.");
    }

    // Method to obtain the maintenance whitelist configuration
    public FileConfiguration getMaintenanceWhitelistConfig() {
        return maintenanceWhitelistConfig;
    }

    // Save changes to the maintenance-whitelist.yml file
    public void saveMaintenanceWhitelist() {
        try {
            maintenanceWhitelistConfig.save(maintenanceWhitelistFile);
            getLogger().info("✅ maintenance_whitelist.yml saved successfully.");
        } catch (IOException e) {
            getLogger().severe("❌ Failed to save maintenance_whitelist.yml: " + e.getMessage());
        }
    }

    // Reload configuration, including maintenance whitelist
    public void reloadPluginConfig() {
        // Reload main configuration
        super.reloadConfig();
        this.config = getConfig();

        // Reload the maintenance whitelist configuration
        loadMaintenanceWhitelist();

        getLogger().info("✅ The configuration has been reloaded.");
    }
}