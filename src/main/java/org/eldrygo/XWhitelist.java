package org.eldrygo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.eldrygo.MWhitelist.MWhitelist;
import org.eldrygo.MWhitelist.MWhitelistCommand;

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

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        log = this.getLogger();

        saveDefaultConfig();
        loadMaintenanceWhitelist();
        this.config = getConfig();
        mWhitelist = new MWhitelist(this);


        // Cargar los valores de la configuración principal
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

        // Registrar eventos
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);

        // Registrar el comando
        loadCommands();

        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #a0ff72se ha encendido! &fVersion: " + version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #fff18dGracias por usar mi plugin! - Drygo"));
    }

    public void connectToDatabase() {
        try {
            log.info("Intentando conectar a la base de datos...");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            log.info("✅ Conectado a la base de datos MySQL.");
        } catch (SQLException e) {
            log.severe("❌ Error conectando a MySQL: " + e.getMessage());
        }
    }
    private void loadCommands() {
        getCommand("xwhitelist").setExecutor(new XWhitelistCommand(this));
        if (getCommand("xwhitelist") == null) {
            getLogger().severe("❌ Error: El comando xwhitelist no está registrado en plugin.yml");
        }
        getCommand("mwhitelist").setExecutor(new MWhitelistCommand(this, mWhitelist));
        if (getCommand("mwhitelist") == null) {
            getLogger().severe("❌ Error: El comando mwhitelist no está registrado en plugin.yml");
        }
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS whitelist (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(16) NOT NULL UNIQUE, " +
                    "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");
            log.info("✅ Tabla 'whitelist' verificada en la base de datos.");
        } catch (SQLException e) {
            log.severe("❌ Error al crear la tabla en MySQL: " + e.getMessage());
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
                log.info("📴 Desconectado de la base de datos.");
            }
        } catch (SQLException e) {
            log.severe("❌ Error cerrando conexión MySQL: " + e.getMessage());
        }
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #ff7272se ha apagado! &fVersion: " + version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&8[#ff0000&lX&r&lWhitelist&8] #fff18dGracias por usar mi plugin! - Drygo"));
    }

    public Connection getConnection() {
        return connection;
    }

    public void reloadMessages() {
        this.config = getConfig();
    }

    private void onFirstRun() {
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+======================================================+"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("&8                   [#ff0000&lX&r&lWhitelist&8]"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dHola! Al ser este el primer inicio de XWhitelist, el plugin"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dse ha desactivado al no tener una base de datos configurada."));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffffFavor de configurar la base de datos en el archivo config.yml"));
        getServer().getConsoleSender().sendMessage(ChatUtils.formatColor("#666666+======================================================+"));
    }

    // Cargar y manejar el archivo maintenance-whitelist.yml
    public void loadMaintenanceWhitelist() {
        maintenanceWhitelistFile = new File(getDataFolder(), "maintenance_whitelist.yml");

        if (!maintenanceWhitelistFile.exists()) {
            saveResource("maintenance_whitelist.yml", false);
            getLogger().info("✅ El archivo maintenance_whitelist.yml no existía, se ha creado.");
        } else {
            getLogger().info("✅ El archivo maintenance_whitelist.yml se ha cargado correctamente.");
        }

        maintenanceWhitelistConfig = YamlConfiguration.loadConfiguration(maintenanceWhitelistFile);
    }

    // Método para obtener la configuración de la whitelist de mantenimiento
    public FileConfiguration getMaintenanceWhitelistConfig() {
        return maintenanceWhitelistConfig;
    }

    // Guardar cambios en el archivo maintenance-whitelist.yml
    public void saveMaintenanceWhitelist() {
        try {
            maintenanceWhitelistConfig.save(maintenanceWhitelistFile);
            getLogger().info("✅ maintenance_whitelist.yml guardado correctamente.");
        } catch (IOException e) {
            getLogger().severe("❌ No se pudo guardar maintenance_whitelist.yml: " + e.getMessage());
        }
    }

    // Recargar la configuración, incluida la whitelist de mantenimiento
    public void reloadPluginConfig() {
        // Recargar la configuración principal
        super.reloadConfig();
        this.config = getConfig();

        // Recargar la whitelist de mantenimiento
        loadMaintenanceWhitelist();

        getLogger().info("✅ La configuración ha sido recargada.");
    }
}