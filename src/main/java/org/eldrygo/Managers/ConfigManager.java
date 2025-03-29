package org.eldrygo.Managers;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.Utils.ChatUtils;
import org.eldrygo.Utils.DBUtils;
import org.eldrygo.XWhitelist;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final XWhitelist plugin;
    public File maintenanceWhitelistFile;
    public FileConfiguration maintenanceWhitelistConfig;
    public File whitelistFile;
    private FileConfiguration whitelistConfig;
    public FileConfiguration messagesConfig;
    public String prefix;
    private final ConfigManager configManager;
    private final ChatUtils chatUtils;

    public ConfigManager(XWhitelist plugin, ConfigManager configManager, ChatUtils chatUtils) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.chatUtils = chatUtils;
    }

    public void loadMaintenanceWhitelist() {
        File maintenanceWhitelistFile = new File(plugin.getDataFolder(), "maintenance_whitelist.yml");

        if (!maintenanceWhitelistFile.exists()) {
            plugin.saveResource("maintenance_whitelist.yml", false);
            plugin.getLogger().info("✅ The maintenance_whitelist.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The maintenance_whitelist.yml file has been loaded successfully.");
        }

        maintenanceWhitelistConfig = YamlConfiguration.loadConfiguration(maintenanceWhitelistFile);
    }
    public void reloadMaintenanceWhitelist() {
        maintenanceWhitelistConfig = YamlConfiguration.loadConfiguration(maintenanceWhitelistFile);
        plugin.getLogger().info("🔄 maintenance-whitelist.yml was reloaded successfully.");
    }

    // Method to obtain the maintenance whitelist configuration
    public FileConfiguration getMaintenanceWhitelistConfig() {
        return maintenanceWhitelistConfig;
    }

    // Save changes to the maintenance-whitelist.yml file
    public void saveMaintenanceWhitelist() {
        try {
            maintenanceWhitelistConfig.save(maintenanceWhitelistFile);
            plugin.getLogger().info("✅ maintenance_whitelist.yml saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Failed to save maintenance_whitelist.yml: " + e.getMessage());
        }
    }

    // Reload configuration, including maintenance whitelist
    public void loadWhitelistFile() {
        File whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml");
        if (!whitelistFile.exists()) {
            plugin.saveResource("whitelist.yml", false);
            plugin.getLogger().info("✅ The whitelist.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The whitelist.yml file has been loaded successfully.");}
        whitelistConfig = YamlConfiguration.loadConfiguration(whitelistFile);
    }

    public FileConfiguration getWhitelistConfig() {
        return whitelistConfig;
    }

    public void saveWhitelistFile() {
        try {
            whitelistConfig.save(whitelistFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error on saving whitelist.yml: " + e.getMessage());
        }
    }
    public void reloadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
            plugin.getLogger().info("✅ The messages.yml file did not exist, it has been created.");
        } else {
            plugin.getLogger().info("✅ The messages.yml file has been loaded successfully.");
        }
        prefix = ChatUtils.formatColor(messagesConfig.getString("prefix", "#ff177c&lx&r&lWhitelist &cDefault Prefix &8»&r"));
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadPluginConfig() {
        // Reload main configuration
        plugin.reloadConfig();
        plugin.config = plugin.getConfig();

        // Reload the maintenance whitelist configuration
        configManager.loadMaintenanceWhitelist();

        plugin.getLogger().info("✅ The configuration has been reloaded.");
    }
    public FileConfiguration getMessageConfig() {
        return messagesConfig;
    }
    public void reloadConfig(CommandSender sender) {
        FileConfiguration config = plugin.getConfig();
        reloadPluginConfig();
        loadMaintenanceWhitelist();
        reloadMessages();
        loadWhitelistFile();
        boolean newMySQLEnabled = plugin.getConfig().getBoolean("mysql.enable", false);
        if (plugin.useMySQL || newMySQLEnabled) {
            DBUtils.reloadDatabaseConnection(config);
        }
        sender.sendMessage(chatUtils.getMessage("commands.plugin.reload_success"));
    }
}
