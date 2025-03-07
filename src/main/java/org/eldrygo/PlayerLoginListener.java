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

        Connection connection = plugin.getConnection();

        plugin.getLogger().info("Verifying if MWhitelist is active.");
        if (plugin.getMaintenanceWhitelistConfig().getBoolean("enabled", false)) {
            plugin.getLogger().info("MWhitelist is active. Verifying if player is in maintenance whitelist.");
            if (!isPlayerInMWhitelist(username)){
                plugin.getLogger().info("Player is not in maintenance whitelist. Kicking player.");
                String kickMessage = plugin.getMaintenanceWhitelistConfig().getString("messages.player_not_whitelisted", "&cYou are not in the maintenance whitelist.")
                        .replace("%player%", username);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatUtils.formatColor(kickMessage));
            }
            plugin.getLogger().info("Player is in maintenance whitelist. Skipping whitelist check.");
        } else {
            plugin.getLogger().info("MWhitelist is not active. Checking if player is in whitelist.");
            if (!isPlayerWhitelisted(connection, username)){
                plugin.getLogger().info("Player is not in whitelist. Kicking player.");
                String kickMessage = plugin.getConfig().getString("messages.player_not_whitelisted", "&cYou are not in the whitelist.")
                        .replace("%player%", username);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatUtils.formatColor(kickMessage));
            }
        }
    }

    private boolean isPlayerWhitelisted(Connection connection, String playerName) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM whitelist WHERE username = ?")) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isPlayerInMWhitelist(String playerName) {
        List<String> maintenanceWhitelist = plugin.getMaintenanceWhitelistConfig().getStringList("whitelist");
        return maintenanceWhitelist.contains(playerName);
    }
}
