package org.eldrygo;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlayerLoginListener implements Listener {
    private final XWhitelist plugin;

    public PlayerLoginListener(Plugin plugin) {
        this.plugin = (XWhitelist) plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();
        plugin.getLogger().info("Verifying the player: " + username);

        // Revisar primero la whitelist de mantenimiento
        if (plugin.getMaintenanceWhitelistConfig().getBoolean("enabled", false)) {
            if (!isPlayerInMWhitelist(username)) {
                List<String> kickMessages = plugin.getMessageConfig().getStringList("kick_messages.maintenance");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, formatMultiLineMessage(kickMessages, username));
                return;
            }
        } else {
            // Si la whitelist de mantenimiento NO está activada, verificar la whitelist normal
            if (plugin.getConfig().getBoolean("enabled", false)) {
                if (plugin.getConfig().getBoolean("mysql.enable")) {
                    Connection connection = plugin.getConnection();
                    if (!isPlayerWhitelistedMySQL(connection, username)) {
                        List<String> kickMessages = plugin.getMessageConfig().getStringList("kick_messages.whitelist");
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, formatMultiLineMessage(kickMessages, username));
                    }
                } else {
                    if (!isPlayerWhitelistedFile(username)) {
                        List<String> kickMessages = plugin.getMessageConfig().getStringList("kick_messages.whitelist");
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, formatMultiLineMessage(kickMessages, username));
                    }
                }
            }
            return;
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
        List<String> whitelist = plugin.getWhitelistConfig().getStringList("whitelist");
        return whitelist.contains(playerName);
    }

    // Verifica si el jugador está en la whitelist de mantenimiento
    private boolean isPlayerInMWhitelist(String playerName) {
        List<String> maintenanceWhitelist = plugin.getMaintenanceWhitelistConfig().getStringList("whitelist");
        return maintenanceWhitelist.contains(playerName);
    }
    private String formatMultiLineMessage(List<String> messages, String playerName) {
        String prefix = plugin.getMessageConfig().getString("prefix", "#ff0000&lx&r&lWhitelist &8»&r"); // Valor por defecto del prefix

        StringBuilder formattedMessage = new StringBuilder();
        for (String line : messages) {
            formattedMessage.append(ChatUtils.formatColor(
                    line.replace("%player%", playerName)
                            .replace("%prefix%", prefix)
            )).append("\n");
        }
        return formattedMessage.toString().trim();
    }
}
