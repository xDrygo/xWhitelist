package org.eldrygo.Managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MWhitelistManager {

    private final JavaPlugin plugin;
    private final File configFile;
    private static YamlConfiguration config;

    public MWhitelistManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "maintenance_whitelist.yml");
        loadConfig();
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
    public static YamlConfiguration getConfig() {
        return config;
    }
    public void toggleMaintenanceWhitelist() {
        boolean currentStatus = config.getBoolean("enabled", false);
        config.set("enabled", !currentStatus);
        saveConfig();
        loadConfig();
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
            plugin.getLogger().severe("Error on saving the config: " + e.getMessage());
        }
    }
}