package org.eldrygo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;

public class XWhitelistCommand implements CommandExecutor {
    private final XWhitelist plugin;
    private Connection connection;
    public XWhitelistCommand(XWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_unknown_command")));
            return true; // Muestra la ayuda si no se pasa ningún argumento
        }
        Connection connection = plugin.getConnection();

        if (args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("xwhitelist.add") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_player_argument_needed")));
                return true;
            }
            String playerName = args[1];
            addPlayerToWhitelist(connection, playerName, sender);
            return true;
        }

        // Comando /xwhitelist remove
        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("xwhitelist.remove") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_player_argument_needed")));
                return true;
            }
            String playerName = args[1];
            removePlayerFromWhitelist(connection, playerName, sender);
            return true;
        }

        // Comando /xwhitelist list
        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("xwhitelist.list") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            listWhitelist(connection, sender);
            return true;
        }

        // Comando /xwhitelist cleanup
        if (args[0].equalsIgnoreCase("cleanup")) {
            if (!sender.hasPermission("xwhitelist.cleanup") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            cleanupWhitelist(connection, sender);
            return true;
        }

        // Comando /xwhitelist reload
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("xwhitelist.reload") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            reloadConfig(sender);
            return true;

            // Comando /xwhitelist help
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            helpWhitelist(sender);
            return true;
        }

        sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_unknown_command")));
        return true;
    }

    private void addPlayerToWhitelist(Connection connection, String playerName, CommandSender sender) {
        try {
            // Verificar si el jugador ya está en la whitelist
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Si el jugador ya está en la whitelist
                    sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_already_whitelisted").replace("%player%", playerName)));
                    return; // Salir de la función
                }
            }

            // Si no existe, agregarlo
            String insertQuery = "INSERT INTO whitelist (username) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.player_added").replace("%player%", playerName)));
            }

        } catch (SQLException e) {
            // En caso de error con la base de datos
            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_database")));
        }
    }

    private void removePlayerFromWhitelist(Connection connection, String playerName, CommandSender sender) {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM whitelist WHERE username = ?")) {
            stmt.setString(1, playerName);
            stmt.executeUpdate();
            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.player_removed").replace("%player%", playerName)));
        } catch (SQLException e) {
            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_database")));
        }
    }

    private void listWhitelist(Connection connection, CommandSender sender) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM whitelist")) {

            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.whitelist_header")));
            while (rs.next()) {
                sender.sendMessage(ChatUtils.formatColor(rs.getString("username")));
            }
        } catch (SQLException e) {
            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_database")));
        }
    }

    private void cleanupWhitelist(Connection connection, CommandSender sender) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM whitelist");
            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.whitelist_cleaned")));
        } catch (SQLException e) {
            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.error_database")));
        }
    }
    private void reloadConfig(CommandSender sender) {
        // Recargar la configuración principal
        plugin.reloadPluginConfig();
        FileConfiguration config = plugin.getConfig();

        // Recargar el archivo maintenance-whitelist.yml
        plugin.loadMaintenanceWhitelist();

        // Restablecer la conexión con la base de datos
        reloadDatabaseConnection(config);

        // Recargar los mensajes
        plugin.reloadMessages();

        sender.sendMessage(ChatUtils.formatColor(config.getString("messages.config_reloaded")));
    }

    private void reloadDatabaseConnection(FileConfiguration config) {
        try {
            // Cerrar la conexión anterior si existe
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

            // Establecer nueva conexión con la base de datos
            String host = config.getString("mysql.host");
            int port = config.getInt("mysql.port");
            String database = config.getString("mysql.database");
            String user = config.getString("mysql.user");
            String password = config.getString("mysql.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, user, password);

            plugin.getLogger().info("Conexión a la base de datos restablecida.");

        } catch (SQLException e) {
            plugin.getLogger().severe("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void helpWhitelist(CommandSender sender) {
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&8                                [#ff0000&lX&r&lWhitelist&8]"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l         ʟɪꜱᴛᴀ ᴅᴇ ᴄᴏᴍᴀɴᴅᴏꜱ ᴅᴇ ᴀᴅᴍɪɴɪꜱᴛʀᴀᴄɪóɴ:"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#ff7272/xᴡʜɪᴛᴇʟɪꜱᴛ &fᴀᴅᴅ <jugador> #707070- #ccccccAñade un jugador a la whitelist"));
        sender.sendMessage(ChatUtils.formatColor("#ff7272/xᴡʜɪᴛᴇʟɪꜱᴛ &fʀᴇᴍᴏᴠᴇ <jugador> #707070- #ccccccElimina un jugador de la whitelist"));
        sender.sendMessage(ChatUtils.formatColor("#ff7272/xᴡʜɪᴛᴇʟɪꜱᴛ &fʟɪꜱᴛ #707070- #ccccccMuestra la lista de jugadores en la whitelist"));
        sender.sendMessage(ChatUtils.formatColor("#ff7272/xᴡʜɪᴛᴇʟɪꜱᴛ &fᴄʟᴇᴀɴᴜᴘ #707070- #ccccccElimina a todos los jugadores de la whitelist"));
        sender.sendMessage(ChatUtils.formatColor("#ff7272/xᴡʜɪᴛᴇʟɪꜱᴛ &fʀᴇʟᴏᴀᴅ #707070- #ccccccRecarga la configuración del plugin"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#ff9cd2/ᴍᴡʜɪᴛᴇʟɪꜱᴛ &fᴇɴᴀʙʟᴇ #707070- #ccccccActivar la MWhitelist"));
        sender.sendMessage(ChatUtils.formatColor("#ff9cd2/ᴍᴡʜɪᴛᴇʟɪꜱᴛ &fᴅɪꜱᴀʙʟᴇ #707070- #ccccccDesctivar la MWhitelist"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&f/xᴡʜɪᴛᴇʟɪꜱᴛ ʜᴇʟᴘ #707070- #ccccccMuestra la lista de comandos."));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
    }
}
