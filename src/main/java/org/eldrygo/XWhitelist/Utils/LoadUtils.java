package org.eldrygo.XWhitelist.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.XWhitelist.Extensions.XWhitelistExpansion;
import org.eldrygo.XWhitelist.Handlers.XWhitelistCommand;
import org.eldrygo.XWhitelist.Handlers.XWhitelistTabCompleter;
import org.eldrygo.XWhitelist.Listeners.PlayerLoginListener;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Managers.FileWhitelistManager;
import org.eldrygo.XWhitelist.Managers.MWhitelistManager;
import org.eldrygo.XWhitelist.Managers.MySQLWhitelistManager;
import org.eldrygo.XWhitelist.XWhitelist;

import java.io.File;
import java.util.Objects;

public class LoadUtils {
    private final ConfigManager configManager;
    private final XWhitelist plugin;
    private final MWhitelistManager mWhitelistManager;
    private final DBUtils dBUtils;
    private final FileWhitelistManager fileWhitelistManager;
    private final MySQLWhitelistManager mySQLWhitelistManager;
    private final ChatUtils chatUtils;

    public LoadUtils(ConfigManager configManager, XWhitelist plugin, MWhitelistManager mWhitelistManager, DBUtils dBUtils, FileWhitelistManager fileWhitelistManager, MySQLWhitelistManager mySQLWhitelistManager, ChatUtils chatUtils) {
        this.configManager = configManager;
        this.plugin = plugin;
        this.mWhitelistManager = mWhitelistManager;
        this.dBUtils = dBUtils;
        this.fileWhitelistManager = fileWhitelistManager;
        this.mySQLWhitelistManager = mySQLWhitelistManager;
        this.chatUtils = chatUtils;
    }

    public void loadFeatures() {
        loadPlaceholderAPI();
        loadCommands();
        loadConfigFiles();
        loadPlaceholderAPI();
        loadWhitelistManager();
        loadListeners();
    }
    private void loadWhitelistManager() {
        if (plugin.useMySQL) {
            dBUtils.connectToDatabase();
            dBUtils.createTableIfNotExists();
        }
    }
    private void loadPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XWhitelistExpansion(plugin, configManager).register();
            plugin.log.info("✅ PlaceholderAPI detected. Placeholders will work.");
            plugin.workingPlaceholderAPI = true;
        } else {
            plugin.log.warning("⚠ PlaceholderAPI not detected. Placeholders will not work.");
        }
    }
    public void loadCommands() {
        Objects.requireNonNull(plugin.getCommand("xwhitelist")).setExecutor(new XWhitelistCommand(plugin, mWhitelistManager, configManager, fileWhitelistManager, mySQLWhitelistManager, chatUtils, dBUtils));
        Objects.requireNonNull(plugin.getCommand("xwhitelist")).setTabCompleter(new XWhitelistTabCompleter(plugin, configManager, mWhitelistManager));
        if (plugin.getCommand("xwhitelist") == null) {
            plugin.getLogger().severe("❌ Error: xWhitelist command is no registered in plugin.yml");
        }
    }
    public void loadListeners() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerLoginListener(plugin, configManager, chatUtils), plugin);
    }
    public void loadConfigFiles() {
        plugin.config = plugin.getConfig();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        mWhitelistManager.loadConfig();
        configManager.loadWhitelistFile();
        configManager.setMessagesConfig(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")));
        configManager.loadMaintenanceWhitelist();
        configManager.reloadMessages();
    }
}
