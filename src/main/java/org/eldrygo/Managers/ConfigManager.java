package org.eldrygo.Managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.Utils.ChatUtils;
import org.eldrygo.XWhitelist;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final XWhitelist plugin;
    private File maintenanceWhitelistFile;
    private FileConfiguration maintenanceWhitelistConfig;
    private File whitelistFile;
    private FileConfiguration whitelistConfig;
    private FileConfiguration messagesConfig;
    private String prefix;

    public ConfigManager(XWhitelist plugin) {
        this.plugin = plugin;
    }

    public void loadMaintenanceWhitelist() {
        maintenanceWhitelistFile = new File(plugin.getDataFolder(), "maintenance_whitelist.yml"); // Usar la variable de instancia

        if (!maintenanceWhitelistFile.exists()) {
            plugin.saveResource("maintenance_whitelist.yml", false);
            plugin.getLogger().info("✅ The maintenance_whitelist.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The maintenance_whitelist.yml file has been loaded successfully.");
        }

        setMaintenanceWhitelistConfig(YamlConfiguration.loadConfiguration(maintenanceWhitelistFile));
    }

    public void reloadMaintenanceWhitelist() {
        setMaintenanceWhitelistConfig(YamlConfiguration.loadConfiguration(getMaintenanceWhitelistFile()));
        plugin.getLogger().info("🔄 maintenance-whitelist.yml was reloaded successfully.");
    }

    public void saveMaintenanceWhitelist() {
        try {
            getMaintenanceWhitelistConfig().save(getMaintenanceWhitelistFile());
            plugin.getLogger().info("✅ maintenance_whitelist.yml saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Failed to save maintenance_whitelist.yml: " + e.getMessage());
        }
    }
    public void loadWhitelistFile() {
        whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml"); // Usar la variable de instancia

        if (!whitelistFile.exists()) {
            plugin.saveResource("whitelist.yml", false);
            plugin.getLogger().info("✅ The whitelist.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The whitelist.yml file has been loaded successfully.");
        }

        whitelistConfig = YamlConfiguration.loadConfiguration(whitelistFile);
    }

    public void saveWhitelistFile() {
        try {
            getWhitelistConfig().save(getWhitelistFile());
            plugin.getLogger().info("✅ whitelist.yml saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Error saving whitelist.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getWhitelistConfig() {
        return whitelistConfig;
    }
    public FileConfiguration getMaintenanceWhitelistConfig() {
        return maintenanceWhitelistConfig;
    }

    public void reloadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
            plugin.getLogger().info("✅ The messages.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The messages.yml file has been loaded successfully.");
        }
        setPrefix(ChatUtils.formatColor(getMessagesConfig().getString("prefix", "#ff177c&lx&r&lWhitelist &cDefault Prefix &8»&r")));
        setMessagesConfig(YamlConfiguration.loadConfiguration(messagesFile));
    }

    public void reloadPluginConfig() {
        // Reload main configuration
        plugin.reloadConfig();
        plugin.config = plugin.getConfig();

        // Reload the maintenance whitelist configuration
        loadMaintenanceWhitelist();

        plugin.getLogger().info("✅ The configuration has been reloaded.");
    }
    public FileConfiguration getMessageConfig() {
        return getMessagesConfig();
    }

    public File getWhitelistFile() {
        return whitelistFile;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void setMessagesConfig(FileConfiguration messagesConfig) {
        this.messagesConfig = messagesConfig;
    }

    public void setMaintenanceWhitelistConfig(FileConfiguration maintenanceWhitelistConfig) {
        this.maintenanceWhitelistConfig = maintenanceWhitelistConfig;
    }

    public File getMaintenanceWhitelistFile() {
        return maintenanceWhitelistFile;
    }

    public void setMaintenanceWhitelistFile(File maintenanceWhitelistFile) {
        this.maintenanceWhitelistFile = maintenanceWhitelistFile;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
