package org.eldrygo.XWhitelist.Managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.XWhitelist.XWhitelist;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MWhitelistManager {

    private final XWhitelist plugin;
    private final File configFile;
    private YamlConfiguration config; // No es estática

    public MWhitelistManager(XWhitelist plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "maintenance_whitelist.yml");
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("maintenance_whitelist.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public boolean isMaintenanceWhitelistActive() {
        return config.getBoolean("enabled", false);
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void toggleMaintenanceWhitelist() {
        boolean currentStatus = config.getBoolean("enabled", false);
        config.set("enabled", !currentStatus);  // Cambiar el estado
        saveConfig();  // Solo guardamos sin recargar la configuración
        plugin.getLogger().info("🔄 Maintenance whitelist set to: " + !currentStatus);
    }

    public List<String> getMaintenanceWhitelist() {
        return config.getStringList("whitelist");
    }

    public boolean isPlayerInMaintenanceWhitelist(String playerName) {
        return getMaintenanceWhitelist().contains(playerName);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Error saving the config: " + e.getMessage());
        }
    }
}