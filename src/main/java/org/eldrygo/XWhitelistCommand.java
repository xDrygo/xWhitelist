package org.eldrygo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

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
            sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.unknown_command")));
            return true;
        }
        Connection connection = plugin.getConnection();

        if (args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("xwhitelist.whitelist.add") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.player_argument_needed")));
                return true;
            }
            String playerName = args[1];
            addPlayerToWhitelist(connection, playerName, sender);
            return true;
        }

        // Command: /xwhitelist remove
        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("xwhitelist.whitelist.remove") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.player_argument_needed")));
                return true;
            }
            String playerName = args[1];
            removePlayerFromWhitelist(connection, playerName, sender);
            return true;
        }

        // Command: /xwhitelist list
        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("xwhitelist.whitelist.list") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            listWhitelist(connection, sender);
            return true;
        }

        // Command: /xwhitelist cleanup
        if (args[0].equalsIgnoreCase("cleanup")) {
            if (!sender.hasPermission("xwhitelist.whitelist.cleanup") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            cleanupWhitelist(connection, sender);
            return true;
        }

        // Command: /xwhitelist reload
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("xwhitelist.plugin.reload") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            reloadConfig(sender);
            return true;

        // Command: /xwhitelist help
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("xwhitelist.plugin.help") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            helpWhitelist(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission("xwhitelist.plugin.info") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            infoWhitelist(sender);
            return true;
        }

        sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.unknown_command")));
        return true;
    }

    private void addPlayerToWhitelist(Connection connection, String playerName, CommandSender sender) {
        try {
            // Check if the player is already on the whitelist
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.already_whitelisted").replace("%player%", playerName)));
                    return;
                }
            }

            String insertQuery = "INSERT INTO whitelist (username) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                sender.sendMessage(ChatUtils.formatColor(plugin.getConfig().getString("messages.player_added").replace("%player%", playerName)));
            }

        } catch (SQLException e) {
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
        plugin.reloadPluginConfig();
        FileConfiguration config = plugin.getConfig();

        plugin.loadMaintenanceWhitelist();

        reloadDatabaseConnection(config);

        plugin.reloadMessages();

        sender.sendMessage(ChatUtils.formatColor(config.getString("messages.config_reloaded")));
    }

    private void reloadDatabaseConnection(FileConfiguration config) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

            String host = config.getString("mysql.host");
            int port = config.getInt("mysql.port");
            String database = config.getString("mysql.database");
            String user = config.getString("mysql.user");
            String password = config.getString("mysql.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, user, password);

            plugin.getLogger().info("Database connection reset.");

        } catch (SQLException e) {
            plugin.getLogger().severe("Error connecting to database: " + e.getMessage());
        }
    }

    private void helpWhitelist(CommandSender sender) {
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&8                            #ff0000&lX&r&lWhitelist &8- &r&fHelp"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                    ᴘʟᴜɢɪɴ ᴄᴏᴍᴍᴀɴᴅꜱ"));
        sender.sendMessage(ChatUtils.formatColor("&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʜᴇʟᴘ #707070- #ccccccShows this help message"));
        sender.sendMessage(ChatUtils.formatColor("&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʀᴇʟᴏᴀᴅ #707070- #ccccccReloads the plugin configuration"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                 ᴡʜɪᴛᴇʟɪꜱᴛ ᴄᴏᴍᴍᴀɴᴅꜱ"));
        sender.sendMessage(ChatUtils.formatColor("&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴀᴅᴅ #707070- #ccccccAdd a player to the whitelist"));
        sender.sendMessage(ChatUtils.formatColor("&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʀᴇᴍᴏᴠᴇ #707070- #ccccccREMOVE a player from the whitelist"));
        sender.sendMessage(ChatUtils.formatColor("&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʟɪꜱᴛ #707070- #ccccccDisplays the player is the whitelist"));
        sender.sendMessage(ChatUtils.formatColor("&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴄʟᴇᴀɴᴜᴘ #707070- #ccccccRemoves all players from the whitelist"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l          ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ᴡʜɪᴛᴇʟɪꜱᴛ ᴄᴏᴍᴍᴀɴᴅꜱ"));
        sender.sendMessage(ChatUtils.formatColor("&f  /ᴍᴡʜɪᴛᴇʟɪꜱᴛ ᴇɴᴀʙʟᴇ #707070- #ccccccEnables the maintenance whitelist"));
        sender.sendMessage(ChatUtils.formatColor("&f  /ᴍᴡʜɪᴛᴇʟɪꜱᴛ ᴅɪꜱᴀʙʟᴇ #707070- #ccccccDisables the maintenance whitelist"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
    }
    private void infoWhitelist(CommandSender sender) {
        String placeholderStatus = plugin.isPlaceholderAPIEnabled() ? "#a0ff72✔" : "#ff7272✖";
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&8                            #ff0000&lX&r&lWhitelist &8- &r&fInfo"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                           ᴍᴀᴅᴇ ʙʏ"));
        sender.sendMessage(ChatUtils.formatColor("&f                           xDrygo #707070» &7&o(@eldrygo)"));;
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                  ʀᴜɴɴɪɴɢ ᴘʟᴜɢɪɴ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f                                    " + plugin.version));;
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                  ᴇxᴛᴇɴꜱɪᴏɴꜱ ʀᴇɢɪꜱᴛᴇʀᴇᴅ"));
        sender.sendMessage(ChatUtils.formatColor("&f                           ᴘʟᴀᴄᴇʜᴏʟᴅᴇʀᴀᴘɪ #707070» #FFFAAB" + placeholderStatus));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                      ᴠᴇʀꜱɪᴏɴ ᴄʜᴀɴɢᴇꜱ"));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABCode Optimization and bug fixes."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070702. #FFFAABChanged messages from Spanish to English."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070703. #FFFAABRemoved debug logs."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070704. #FFFAABAdded PlaceholderAPI variables."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070705. #FFFAABPermissions reformed."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070706. #FFFAABAdded command /xwhitelist info."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070707. #FFFAABReformed /xwhitelist help message."));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l               ᴅʀʏɢᴏ'ꜱ ɴᴏᴛᴇ ᴏꜰ ᴛʜᴇ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       Hi, I added a guide in the README.md file that you"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       can find in the plugin's github repository, in case"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       you have any questions you can ask me on my X."));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
    }
}
