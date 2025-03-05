package org.eldrygo;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class PlayerLoginListener implements Listener {
    private final XWhitelist plugin;  // Asegúrate de tener tu instancia del plugin aquí

    public PlayerLoginListener(Plugin plugin) {
        this.plugin = (XWhitelist) plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();
        plugin.getLogger().info("Verificando el jugador: " + username); // Mensaje de depuración

        // Conexión a la base de datos
        Connection connection = plugin.getConnection();

        plugin.getLogger().info("Comprobando si la MWhitelist esta activada. (" + plugin.getMaintenanceWhitelistConfig().getBoolean("enabled") + ")"); // Mensaje de depuración
        // Verificar si la MWhitelist está activada
        if (plugin.getMaintenanceWhitelistConfig().getBoolean("enabled")) {
            plugin.getLogger().info("La MWhitelist está activada, comprobando si " + username + " esta en la whitelist de mantenimiento."); // Mensaje de depuración
            // Revisar si el jugador está en la whitelist de mantenimiento
            if (isPlayerInMWhitelist(username)) {
                plugin.getLogger().info("El jugador " + username + " está en la whitelist de mantenimiento."); // Mensaje de depuración
                return;  // Permite que el jugador ingrese
            } else {
                plugin.getLogger().info("El jugador " + username + " no está en la whitelist de mantenimiento."); // Mensaje de depuración
                String kickMessage = plugin.getMaintenanceWhitelistConfig().getString("messages.player_not_whitelisted", "&cNo estás en la whitelist de mantenimiento.")
                        .replace("%player%", username);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatUtils.formatColor(kickMessage));
                return;  // Rechaza el jugador si no está en ninguna whitelist
            }
        } else {
            plugin.getLogger().info("La MWhitelist está desactivada, comprobando si " + username + " esta en la whitelist.");
            // Si la MWhitelist no está activada, verificar si el jugador está en la whitelist normal
            if (isPlayerWhitelisted(connection, username)) {
                plugin.getLogger().info("El jugador " + username + " está en la whitelist normal."); // Mensaje de depuración
                return;  // Permite que el jugador ingrese
            } else {
                // Si el jugador no está en ninguna whitelist, lo rechazamos
                String kickMessage = plugin.getConfig().getString("messages.player_not_whitelisted", "&cNo estás en la whitelist.")
                        .replace("%player%", username);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatUtils.formatColor(kickMessage));
                return;  // Rechaza el jugador si no está en ninguna whitelist
            }
        }

    }

    // Método para verificar si el jugador está en la whitelist normal
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

    // Método para verificar si el jugador está en la whitelist de mantenimiento
    private boolean isPlayerInMWhitelist(String playerName) {
        // Leer la lista de jugadores en el archivo maintenance-whitelist.yml
        List<String> maintenanceWhitelist = plugin.getMaintenanceWhitelistConfig().getStringList("whitelist");
        return maintenanceWhitelist.contains(playerName);
    }
}
