package org.eldrygo.Listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.eldrygo.Managers.ConfigManager;
import org.eldrygo.Utils.ChatUtils;
import org.eldrygo.XWhitelist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlayerLoginListener implements Listener {
    private final XWhitelist plugin;
    private final ConfigManager configManager;
    private final ChatUtils chatUtils;

    public PlayerLoginListener(Plugin plugin, ConfigManager configManager, ChatUtils chatUtils) {
        this.plugin = (XWhitelist) plugin;
        this.configManager = configManager;
        this.chatUtils = chatUtils;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();
        plugin.getLogger().info("Verifying the player: " + username);

        List<String> kickMessages = null;

        // Revisar primero la whitelist de mantenimiento
        if (configManager.getMaintenanceWhitelistConfig().getBoolean("enabled", false)) {
            if (!isPlayerInMWhitelist(username)) {
                kickMessages = configManager.getMessageConfig().getStringList("kick_messages.maintenance");
            }
        } else {
            // Revisar la whitelist normal si la de mantenimiento no está activa
            if (plugin.getConfig().getBoolean("enabled", false)) {
                if (plugin.getConfig().getBoolean("mysql.enable")) {
                    Connection connection = plugin.getConnection();
                    if (!isPlayerWhitelistedMySQL(connection, username)) {
                        kickMessages = configManager.getMessageConfig().getStringList("kick_messages.whitelist");
                    }
                } else {
                    if (!isPlayerWhitelistedFile(username)) {
                        kickMessages = configManager.getMessageConfig().getStringList("kick_messages.whitelist");
                    }
                }
            }
        }

        if (kickMessages != null) {
            String fullMessage = chatUtils.formatMultiLineMessage(kickMessages, username);

            // Log reducido en consola (solo la primera línea)
            String logMessage = kickMessages.isEmpty() ? "No reason provided." : kickMessages.get(0);
            if (kickMessages.size() > 1) {
                logMessage += " [...]";
            }
            plugin.getLogger().info("Kicking " + username + ": " + logMessage);

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, fullMessage);
        }
    }

    // Verifica si el jugador está en la whitelist de MySQL
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

    // Verifica si el jugador está en la whitelist de `whitelist.yml`
    private boolean isPlayerWhitelistedFile(String playerName) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");
        return whitelist.contains(playerName);
    }

    // Verifica si el jugador está en la whitelist de mantenimiento
    private boolean isPlayerInMWhitelist(String playerName) {
        List<String> maintenanceWhitelist = configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
        return maintenanceWhitelist.contains(playerName);
    }
}
