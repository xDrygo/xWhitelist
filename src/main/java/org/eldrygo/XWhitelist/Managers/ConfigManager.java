package org.eldrygo.XWhitelist.Managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.XWhitelist.Utils.ChatUtils;
import org.eldrygo.XWhitelist.XWhitelist;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private static XWhitelist plugin;
    private static File maintenanceWhitelistFile;
    private static FileConfiguration maintenanceWhitelistConfig;
    private static File whitelistFile;
    private static FileConfiguration whitelistConfig;
    private static FileConfiguration messagesConfig;

    public static void init(XWhitelist plugin) {
        ConfigManager.plugin = plugin;
    }

    public static void loadMaintenanceWhitelist() {
        maintenanceWhitelistFile = new File(plugin.getDataFolder(), "maintenance_whitelist.yml");

        if (!maintenanceWhitelistFile.exists()) {
            plugin.saveResource("maintenance_whitelist.yml", false);
            plugin.getLogger().info("✅ The maintenance_whitelist.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The maintenance_whitelist.yml file has been loaded successfully.");
        }

        setMaintenanceWhitelistConfig(YamlConfiguration.loadConfiguration(maintenanceWhitelistFile));
    }

    public static void reloadMaintenanceWhitelist() {
        setMaintenanceWhitelistConfig(YamlConfiguration.loadConfiguration(getMaintenanceWhitelistFile()));
        plugin.getLogger().info("🔄 maintenance-whitelist.yml was reloaded successfully.");
    }

    public static void saveMaintenanceWhitelist() {
        try {
            getMaintenanceWhitelistConfig().save(getMaintenanceWhitelistFile());
            plugin.getLogger().info("✅ maintenance_whitelist.yml saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Failed to save maintenance_whitelist.yml: " + e.getMessage());
        }
    }
    public static void loadWhitelistFile() {
        whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml"); // Usar la variable de instancia

        if (!whitelistFile.exists()) {
            plugin.saveResource("whitelist.yml", false);
            plugin.getLogger().info("✅ The whitelist.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The whitelist.yml file has been loaded successfully.");
        }

        whitelistConfig = YamlConfiguration.loadConfiguration(whitelistFile);
    }

    public static void saveWhitelistFile() {
        try {
            getWhitelistConfig().save(getWhitelistFile());
            plugin.getLogger().info("✅ whitelist.yml saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Error saving whitelist.yml: " + e.getMessage());
        }
    }

    public static FileConfiguration getWhitelistConfig() {
        return whitelistConfig;
    }
    public static FileConfiguration getMaintenanceWhitelistConfig() {
        return maintenanceWhitelistConfig;
    }

    public static void reloadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
            plugin.getLogger().info("✅ The messages.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The messages.yml file has been loaded successfully.");
        }
        XWhitelist.prefix = ChatUtils.formatColor(getMessagesConfig().getString("prefix", "#ff177c&lx&r&lWhitelist &cDefault Prefix &8»&r"));
        setMessagesConfig(YamlConfiguration.loadConfiguration(messagesFile));
    }

    public static void reloadPluginConfig() {
        plugin.reloadConfig();
        loadMaintenanceWhitelist();
        plugin.getLogger().info("✅ The configuration has been reloaded.");
    }
    public static FileConfiguration getMessageConfig() {
        return getMessagesConfig();
    }
    public static File getWhitelistFile() {
        return whitelistFile;
    }
    public static FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }
    public static void setMessagesConfig(FileConfiguration messagesConfig) {
        ConfigManager.messagesConfig = messagesConfig;
    }
    public static void setMaintenanceWhitelistConfig(FileConfiguration maintenanceWhitelistConfig) {
        ConfigManager.maintenanceWhitelistConfig = maintenanceWhitelistConfig;
    }
    public static File getMaintenanceWhitelistFile() {
        return maintenanceWhitelistFile;
    }
}
