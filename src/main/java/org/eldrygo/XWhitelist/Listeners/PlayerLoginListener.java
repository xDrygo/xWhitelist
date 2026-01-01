package org.eldrygo.XWhitelist.Listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Utils.ChatUtils;
import org.eldrygo.XWhitelist.XWhitelist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlayerLoginListener implements Listener {
    private final XWhitelist plugin;

    public PlayerLoginListener(XWhitelist plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();
        plugin.getLogger().info("Verifying the player: " + username);

        List<String> kickMessages = null;

        if (ConfigManager.getMaintenanceWhitelistConfig().getBoolean("enabled", false)) {
            if (!isPlayerInMWhitelist(username)) {
                kickMessages = ConfigManager.getMessageConfig().getStringList("kick_messages.maintenance");
            }
        } else {
            if (plugin.getConfig().getBoolean("enabled", false)) {
                if (plugin.getConfig().getBoolean("mysql.enable")) {
                    Connection connection = XWhitelist.getConnection();
                    if (!isPlayerWhitelistedMySQL(connection, username)) {
                        kickMessages = ConfigManager.getMessageConfig().getStringList("kick_messages.whitelist");
                    }
                } else {
                    if (!isPlayerWhitelistedFile(username)) {
                        kickMessages = ConfigManager.getMessageConfig().getStringList("kick_messages.whitelist");
                    }
                }
            }
        }

        if (kickMessages != null) {
            String fullMessage = ChatUtils.formatMultiLineMessage(kickMessages, username);

            String logMessage = kickMessages.isEmpty() ? "No reason provided." : kickMessages.getFirst();
            if (kickMessages.size() > 1) {
                logMessage += " [...]";
            }
            plugin.getLogger().info("Kicking " + username + ": " + logMessage);

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, fullMessage);
        }
    }

    private boolean isPlayerWhitelistedMySQL(Connection connection, String playerName) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM whitelist WHERE username = ?")) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isPlayerWhitelistedFile(String playerName) {
        List<String> whitelist = ConfigManager.getWhitelistConfig().getStringList("whitelist");
        return whitelist.contains(playerName);
    }

    private boolean isPlayerInMWhitelist(String playerName) {
        List<String> maintenanceWhitelist = ConfigManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
        return maintenanceWhitelist.contains(playerName);
    }
}
