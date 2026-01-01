package org.eldrygo.XWhitelist.Managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.XWhitelist.XWhitelist;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MWhitelistManager {

    private static XWhitelist plugin;
    private static File configFile;
    private static YamlConfiguration config;

    public static void init(XWhitelist plugin) {
        MWhitelistManager.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "maintenance_whitelist.yml");
    }

    public static void loadConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("maintenance_whitelist.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static boolean isMaintenanceWhitelistActive() {
        return config.getBoolean("enabled", false);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static void toggleMaintenanceWhitelist() {
        boolean currentStatus = config.getBoolean("enabled", false);
        config.set("enabled", !currentStatus);  // Cambiar el estado
        saveConfig();
        plugin.getLogger().info("🔄 Maintenance whitelist set to: " + !currentStatus);
    }

    public static List<String> getMaintenanceWhitelist() {
        return config.getStringList("whitelist");
    }

    public static boolean isPlayerInMaintenanceWhitelist(String playerName) {
        return getMaintenanceWhitelist().contains(playerName);
    }

    public static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Error saving the config: " + e.getMessage());
        }
    }
}