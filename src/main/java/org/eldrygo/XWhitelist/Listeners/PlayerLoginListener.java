package org.eldrygo.XWhitelist.Listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Managers.DBWhitelistManager;
import org.eldrygo.XWhitelist.Utils.ChatUtils;
import org.eldrygo.XWhitelist.XWhitelist;

import java.sql.Connection;
import java.util.List;

public class PlayerLoginListener implements Listener {
    private final XWhitelist plugin;
    private final ConfigManager configManager;
    private final ChatUtils chatUtils;
    private final DBWhitelistManager dbWhitelistManager;

    public PlayerLoginListener(Plugin plugin, ConfigManager configManager, ChatUtils chatUtils, DBWhitelistManager dbWhitelistManager) {
        this.plugin = (XWhitelist) plugin;
        this.configManager = configManager;
        this.chatUtils = chatUtils;
        this.dbWhitelistManager = dbWhitelistManager;
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
                if (plugin.getConfig().getBoolean("database.enable")) {
                    if (!dbWhitelistManager.isPlayerWhitelisted(username)) {
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
