package org.eldrygo.MWhitelist;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MWhitelist {

    private JavaPlugin plugin;
    private File configFile;
    private YamlConfiguration config;

    public MWhitelist(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "maintenance_whitelist.yml");
        loadConfig();
    }

    // Cargar la configuración desde el archivo maintenance-whitelist.yml
    public void loadConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("maintenance_whitelist.yml", false); // Si el archivo no existe, crear el archivo
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    // Obtener el estado de la whitelist de mantenimiento (activada o desactivada)
    public boolean isMaintenanceWhitelistActive() {
        return config.getBoolean("enabled", false);
    }

    // Cambiar el estado de la whitelist de mantenimiento (activar o desactivar)
    public void toggleMaintenanceWhitelist() {
        boolean currentState = isMaintenanceWhitelistActive();
        config.set("enabled", !currentState); // Cambiar el valor a lo contrario
        saveConfig();
    }

    // Obtener la lista de jugadores en la whitelist de mantenimiento
    public List<String> getMaintenanceWhitelist() {
        return config.getStringList("whitelist");
    }

    // Comprobar si el jugador está en la whitelist de mantenimiento
    public boolean isPlayerInMaintenanceWhitelist(String playerName) {
        return getMaintenanceWhitelist().contains(playerName);
    }

    // Guardar los cambios en el archivo maintenance-whitelist.yml
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error al guardar la configuración: " + e.getMessage());
        }
    }

    // Obtener un mensaje del archivo de configuración
    public String getMessage(String messageKey) {
        return config.getString("messages." + messageKey, "Mensaje no encontrado.");
    }
}