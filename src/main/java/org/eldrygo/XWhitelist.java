package org.eldrygo;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.eldrygo.MWhitelist.MWhitelist;
import org.eldrygo.placeholders.XWhitelistExpansion;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
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
    private boolean useMySQL;
    private File whitelistFile;
    private FileConfiguration whitelistConfig;
    private FileConfiguration messagesConfig;
    private String prefix;

    @Override
    public void onEnable() {
        this.config = getConfig();
        saveDefaultConfig();
        reloadConfig();
        this.useMySQL = config.getBoolean("mysql.enable", false);
        this.messagesConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
        log = this.getLogger();
        boolean firstRun = config.getBoolean("plugin.first_run", true);
        mWhitelist = new MWhitelist(this);
        loadPlaceholderAPI();
        loadMaintenanceWhitelist();
        reloadMessages();
        if (firstRun) {
            onFirstRun();
            config.set("plugin.first_run", false);
            saveConfig();
        } else {
            if (useMySQL) {
                startWithMySQL();
            } else {
                startOffline();
            }
        }

        if (useMySQL) {
            connectToDatabase();
            createTableIfNotExists();
        } else {
            loadWhitelistFile();
        }
        loadCommands();
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
        getCommand("xwhitelist").setTabCompleter(new XWhitelistTabCompleter(this));

        // Enable logs
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #a0ff72has been enabled! &fVersion: " + version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #fff18dThanks for use my plugin! - Drygo"));
    }
    public boolean isMySQLEnabled() {
        return useMySQL;
    }
    public void connectToDatabase() {
        host = config.getString("mysql.host");
        port = config.getInt("mysql.port");
        database = config.getString("mysql.database");
        username = config.getString("mysql.user");
        password = config.getString("mysql.password");
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
        getCommand("xwhitelist").setExecutor(new XWhitelistCommand(this, mWhitelist));
        if (getCommand("xwhitelist") == null) {
            getLogger().severe("❌ Error: xWhitelist command is no registered in plugin.yml");
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
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lx&r&lWhitelist&8] #ff7272has been disabled! &fVersion: " + version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lx&r&lWhitelist&8] #fff18dThanks for use my plugin! - Drygo"));
    }

    public Connection getConnection() {
        return connection;
    }
    public String getPrefix() {return prefix;}

    public void reloadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
            getLogger().info("✅ The messages.yml file did not exist, it has been created.");
        } else {
            getLogger().info("✅ The messages.yml file has been loaded successfully.");
        }
        prefix = ChatUtils.formatColor(messagesConfig.getString("prefix", "#ff0000&lx&r&lWhitelist &8»&r"));
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    public String getMessage(String path) {
        String message = messagesConfig.getString(path);
        if (messagesConfig.isList(path)) {
            List<String> lines = messagesConfig.getStringList(path);
            return ChatUtils.formatColor(String.join("\n", lines));
        } else {
            if (message == null || message.isEmpty()) {
                getLogger().warning("[WARNING] Message not found: " + path);
                return ChatUtils.formatColor("%prefix% #FF0000&l[ERROR] #FF3535Message not found: " + path).replace("%prefix%", getPrefix());
            }
            return ChatUtils.formatColor(message.replace("%prefix%", getPrefix()));
        }
    }
    public FileConfiguration getMessageConfig() {
        return messagesConfig;
    }
    public void reloadConfig(CommandSender sender) {
        reloadPluginConfig();
        FileConfiguration config = getConfig();
        loadMaintenanceWhitelist();
        reloadMessages();
        loadWhitelistFile();
        boolean newMySQLEnabled = getConfig().getBoolean("mysql.enable", false);

        if (useMySQL || newMySQLEnabled) {
            reloadDatabaseConnection(config);
        }


        sender.sendMessage(getMessage("commands.plugin.reload_success"));
    }
    public void reloadDatabaseConnection(FileConfiguration config) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

            String host = config.getString("mysql.host");
            int port = config.getInt("mysql.port");
            String database = config.getString("mysql.database");
            String user = config.getString("mysql.user");
            String password = config.getString("mysql.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, user, password);

            getLogger().info("Database connection reset.");

        } catch (SQLException e) {
            getLogger().severe("Error connecting to database: " + e.getMessage());
        }
    }
    private void onFirstRun() {
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+=================================================================+"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("&8                        [#ff0000&lx&r&lWhitelist&8]"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dHi, since this is the first time you've started the server, the plugin"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dstarted with the MySQL option disabled. If you want to use this feature,"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18d         you can enable the MySQL feature in config.yml."));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffff  You can find a guide for the plugin in the modrinth/spigot page."));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+=================================================================+"));
    }
    private void startWithMySQL() {
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+===================================================================+"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("&8                        [#ff0000&lx&r&lWhitelist&8]"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dHi, now you are using the whitelist with the MySQL feature, that means"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dthe whitelist is stored in a database, remember this whitelist and the"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18doffline whitelist are different, you can manage it with the commands."));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffffYou disable the MySQL feature in the config, on the file config.yml"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+===================================================================+"));
    }
    private void startOffline() {
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+=================================================================+"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("&8                        [#ff0000&lx&r&lWhitelist&8]"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dHi, now you are using the whitelist on the offline mode, that means"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dthe whitelist is stored in a local file (whitelist.yml), you can manage"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18d   the list in the file or with the commands that the plugin adds."));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffffYou enable the MySQL feature in the config, on the file config.yml"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+=================================================================+"));
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
    public void loadWhitelistFile() {
        whitelistFile = new File(getDataFolder(), "whitelist.yml");
        if (!whitelistFile.exists()) {
            saveResource("whitelist.yml", false);
            getLogger().info("✅ The whitelist.yml file did not exist, it has been created.");
        } else {
            getLogger().info("✅ The whitelist.yml file has been loaded successfully.");}
        whitelistConfig = YamlConfiguration.loadConfiguration(whitelistFile);
    }

    public FileConfiguration getWhitelistConfig() {
        return whitelistConfig;
    }

    public void saveWhitelistFile() {
        try {
            whitelistConfig.save(whitelistFile);
        } catch (IOException e) {
            getLogger().severe("Error on saving whitelist.yml: " + e.getMessage());
        }
    }
}