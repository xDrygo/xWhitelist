package dev.drygo.XWhitelist.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import dev.drygo.XWhitelist.Extensions.XWhitelistExpansion;
import dev.drygo.XWhitelist.Handlers.XWhitelistCommand;
import dev.drygo.XWhitelist.Handlers.XWhitelistTabCompleter;
import dev.drygo.XWhitelist.Listeners.PlayerLoginListener;
import dev.drygo.XWhitelist.Managers.ConfigManager;
import dev.drygo.XWhitelist.Managers.MWhitelistManager;
import dev.drygo.XWhitelist.XWhitelist;

import java.io.File;
import java.util.Objects;

public class LoadUtils {
    private static XWhitelist plugin;

    public static void init(XWhitelist plugin) {
        LoadUtils.plugin = plugin;
    }

    public static void loadFeatures() {
        loadPlaceholderAPI();
        loadCommands();
        loadConfigFiles();
        loadPlaceholderAPI();
        loadWhitelistManager();
        loadListeners();
    }
    private static void loadWhitelistManager() {
        if (XWhitelist.useMySQL) {
            DBUtils.connectToDatabase();
            DBUtils.createTableIfNotExists();
        }
    }
    private static void loadPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XWhitelistExpansion(plugin).register();
            plugin.getLogger().info("✅ PlaceholderAPI detected. Placeholders will work.");
            XWhitelist.workingPlaceholderAPI = true;
        } else {
            plugin.getLogger().warning("⚠ PlaceholderAPI not detected. Placeholders will not work.");
        }
    }
    public static void loadCommands() {
        Objects.requireNonNull(plugin.getCommand("xwhitelist")).setExecutor(new XWhitelistCommand(plugin));
        Objects.requireNonNull(plugin.getCommand("xwhitelist")).setTabCompleter(new XWhitelistTabCompleter(plugin));
        if (plugin.getCommand("xwhitelist") == null) {
            plugin.getLogger().severe("❌ Error: xWhitelist command is no registered in plugin.yml");
        }
    }
    public static void loadListeners() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerLoginListener(plugin), plugin);
    }
    public static void loadConfigFiles() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        MWhitelistManager.loadConfig();
        ConfigManager.loadWhitelistFile();
        ConfigManager.setMessagesConfig(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")));
        ConfigManager.loadMaintenanceWhitelist();
        ConfigManager.reloadMessages();
    }
}
