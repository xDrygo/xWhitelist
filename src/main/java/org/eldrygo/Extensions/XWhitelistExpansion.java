package org.eldrygo.Extensions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eldrygo.Managers.ConfigManager;
import org.eldrygo.XWhitelist;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class XWhitelistExpansion extends PlaceholderExpansion {

    private final XWhitelist plugin;
    private final ConfigManager configManager;

    public XWhitelistExpansion(XWhitelist plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {return true;}

    @Override
    public @NotNull String getIdentifier() { return "xwhitelist"; }

    @Override
    public @NotNull String getAuthor() {
        return "Drygo";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        if (identifier.equals("maintenance_enabled")) {
            boolean isMaintenanceEnabled = configManager.getMaintenanceWhitelistConfig().getBoolean("enabled");
            return isMaintenanceEnabled ? "true" : "false";
        }

        if (identifier.equals("whitelist_enabled")) {
            boolean isWhitelistEnabled = plugin.getConfig().getBoolean("enabled", false);
            return isWhitelistEnabled ? "true" : "false";
        }

        if (identifier.equals("mysql_enabled")) {
            boolean isMySQLEnabled = plugin.getConfig().getBoolean("mysql.enable");
            return isMySQLEnabled ? "true" : "false";
        }

        if (identifier.equals("maintenance_iswhitelisted")) {
            if (player == null) return "false";
            return String.valueOf(configManager.getMaintenanceWhitelistConfig().getStringList("whitelist").contains(player.getName()));
        }

        if (identifier.equals("whitelist_iswhitelisted")) {
            if (player == null) return "false";
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                return String.valueOf(isPlayerWhitelistedMySQL(plugin.getConnection(), player.getName()));
            } else {
                return String.valueOf(isPlayerWhitelistedFile(player.getName()));
            }
        }

        if (identifier.equals("whitelist_playerswhitelisted")) {
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                return String.valueOf(getWhitelistedPlayersCount(plugin.getConnection()));
            } else {
                File whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml");
                if (!whitelistFile.exists()) return "0";

                YamlConfiguration whitelistConfig = YamlConfiguration.loadConfiguration(whitelistFile);
                List<String> whitelistedPlayers = whitelistConfig.getStringList("whitelist");

                return String.valueOf(whitelistedPlayers.size());
            }
        }

        if (identifier.equals("maintenance_playerswhitelisted")) {
            File mWhitelistFile = new File(plugin.getDataFolder(), "maintenance_whitelist.yml");
            if (!mWhitelistFile.exists()) return "0";

            YamlConfiguration whitelistConfig = YamlConfiguration.loadConfiguration(mWhitelistFile);
            List<String> whitelistedMPlayers = whitelistConfig.getStringList("whitelist");

            return String.valueOf(whitelistedMPlayers.size());
        }
        return null;
    }

    private boolean isPlayerWhitelistedMySQL(Connection connection, String playerName) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM whitelist WHERE username = ?")) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("❌ Error checking whitelist: " + e.getMessage());
        }
        return false;
    }
    private boolean isPlayerWhitelistedFile(String playerName) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");
        return whitelist.contains(playerName);
    }

    private int getWhitelistedPlayersCount(Connection connection) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM whitelist")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("❌ Error counting players in the whitelist: " + e.getMessage());
        }
        return 0;
    }
}
