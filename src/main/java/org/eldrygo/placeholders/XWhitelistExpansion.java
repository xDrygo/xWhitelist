package org.eldrygo.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.eldrygo.XWhitelist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class XWhitelistExpansion extends PlaceholderExpansion {

    private final XWhitelist plugin;

    public XWhitelistExpansion(XWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {return true;}

    @Override
    public String getIdentifier() { return "xwhitelist"; }

    @Override
    public String getAuthor() {
        return "Drygo";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        if (identifier.equals("maintenance_status")) {
            boolean isMaintenanceEnabled = plugin.getMaintenanceWhitelistConfig().getBoolean("enabled");
            return isMaintenanceEnabled ? "true" : "false";
        }

        if (identifier.equals("maintenance_iswhitelisted")) {
            if (player == null) return "false";
            return String.valueOf(plugin.getMaintenanceWhitelistConfig().getStringList("whitelist").contains(player.getName()));
        }

        if (identifier.equals("whitelist_iswhitelisted")) {
            if (player == null) return "false";
            return String.valueOf(isPlayerWhitelisted(plugin.getConnection(), player.getName()));
        }

        if (identifier.equals("whitelist_playerswhitelisted")) {
            return String.valueOf(getWhitelistedPlayersCount(plugin.getConnection()));
        }

        return null;
    }

    private boolean isPlayerWhitelisted(Connection connection, String playerName) {
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
