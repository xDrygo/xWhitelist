package org.eldrygo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.eldrygo.MWhitelist.MWhitelist;

import java.sql.*;
import java.util.List;

public class XWhitelistCommand implements CommandExecutor {
    private final XWhitelist plugin;
    private final MWhitelist mWhitelist;
    private Connection connection;
    public XWhitelistCommand(XWhitelist plugin, MWhitelist mWhitelist) {
        this.plugin = plugin;
        this.mWhitelist = mWhitelist;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessage("commands.plugin.unknown_command"));
            return true;
        }
        Connection connection = plugin.getConnection();

        if (args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("xwhitelist.whitelist.add") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(plugin.getMessage("commands.whitelist.add.usage"));
                return true;
            }
            String playerName = args[1];
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                addPlayerToWhitelistMySQL(connection, playerName, sender);
            } else {
                addPlayerToWhitelistFile(playerName, sender);
            }
            return true;
        }

        // Command: /xwhitelist remove
        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("xwhitelist.whitelist.remove") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(plugin.getMessage("commands.whitelist.remove.usage"));
                return true;
            }
            String playerName = args[1];
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                removePlayerFromWhitelistMySQL(connection, playerName, sender);
            } else {
                removePlayerFromWhitelistFile(playerName, sender);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            if (!sender.hasPermission("xwhitelist.whitelist.enable") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            if (!plugin.getConfig().getBoolean("enabled")) {
                plugin.getConfig().set("enabled", true);
                plugin.saveConfig();
                plugin.reloadConfig();
                sender.sendMessage(plugin.getMessage("commands.whitelist.enable.success"));
            } else {
                sender.sendMessage(plugin.getMessage("commands.whitelist.enable.already"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            if (!sender.hasPermission("xwhitelist.whitelist.disable") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("enabled")) {
                plugin.getConfig().set("enabled", false);
                plugin.saveConfig();
                plugin.reloadConfig();
                sender.sendMessage(plugin.getMessage("commands.whitelist.disable.success"));
            } else {
                sender.sendMessage(plugin.getMessage("commands.whitelist.disable.already"));
            }
            return true;
        }

        // Command: /xwhitelist list
        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("xwhitelist.whitelist.list") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                listWhitelistMySQL(connection,  sender);
            } else {
                listWhitelistFile(sender);
            }
            return true;
        }

        // Command: /xwhitelist cleanup
        if (args[0].equalsIgnoreCase("cleanup")) {
            if (!sender.hasPermission("xwhitelist.whitelist.cleanup") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                cleanupWhitelistMySQL(connection,  sender);
            } else {
                cleanupWhitelistFile(sender);
            }
            return true;
        }

        // Command: /xwhitelist reload
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("xwhitelist.plugin.reload") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            plugin.reloadConfig(sender);
            return true;

        // Command: /xwhitelist help
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("xwhitelist.plugin.help") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            helpWhitelist(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission("xwhitelist.plugin.info") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
            infoWhitelist(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("maintenance")) {
            if (args[1].equalsIgnoreCase("enable")) {
                if (sender.hasPermission("xwhitelist.maintenance.enable") || sender.hasPermission("xwhitelist.admin")) {
                    if (!mWhitelist.isMaintenanceWhitelistActive()) {
                        mWhitelist.toggleMaintenanceWhitelist();
                        plugin.reloadMaintenanceWhitelist();
                        sender.sendMessage(plugin.getMessage("commands.maintenance.enable.success"));
                    } else {
                        sender.sendMessage(plugin.getMessage("commands.maintenance.enable.already"));
                    }
                    return true;
                } else {
                    sender.sendMessage(plugin.getMessage("error.no_permission"));
                    return true;
                }
            }

            if (args[1].equalsIgnoreCase("disable")) {
                if (sender.hasPermission("xwhitelist.maintenance.disable") || sender.hasPermission("xwhitelist.admin")) {
                    if (mWhitelist.isMaintenanceWhitelistActive()) {
                        mWhitelist.toggleMaintenanceWhitelist();
                        plugin.reloadMaintenanceWhitelist();
                        sender.sendMessage(plugin.getMessage("commands.maintenance.disable.success"));
                    } else {
                        sender.sendMessage(plugin.getMessage("commands.maintenance.disable.already"));
                    }
                    return true;
                } else {
                    sender.sendMessage(plugin.getMessage("error.no_permission"));
                    return true;
                }
            }

            if (args[1].equalsIgnoreCase("add")) {
                if (args.length < 3) {
                    sender.sendMessage(plugin.getMessage("commands.maintenance.add.usage"));
                    return true;
                }
                if (sender.hasPermission("xwhitelist.maintenance.add") || sender.hasPermission("xwhitelist.admin")) {
                    String player = args[2];
                    List<String> whitelist = plugin.getMaintenanceWhitelistConfig().getStringList("whitelist");

                    if (whitelist.contains(player)) {
                        sender.sendMessage(plugin.getMessage("commands.maintenance.add.already").replace("%player%", player));
                        return true;
                    }

                    whitelist.add(player);
                    plugin.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
                    plugin.saveMaintenanceWhitelist();
                    plugin.reloadMaintenanceWhitelist();
                    sender.sendMessage(plugin.getMessage("commands.maintenance.add.success").replace("%player%", player));
                    return true;
                }
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    sender.sendMessage(plugin.getMessage("commands.maintenance.remove.usage"));
                    return true;
                }
                if (sender.hasPermission("xwhitelist.maintenance.remove") || sender.hasPermission("xwhitelist.admin")) {
                    String player = args[2];
                    List<String> whitelist = plugin.getMaintenanceWhitelistConfig().getStringList("whitelist");

                    if (!whitelist.contains(player)) {
                        sender.sendMessage(plugin.getMessage("commands.maintenance.remove.already").replace("%player%", player));
                        return true;
                    }

                    whitelist.remove(player);
                    plugin.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
                    plugin.saveMaintenanceWhitelist();
                    plugin.reloadMaintenanceWhitelist();
                    sender.sendMessage(plugin.getMessage("commands.maintenance.remove.success").replace("%player%", player));
                    return true;
                }
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("list")) {
                if (sender.hasPermission("xwhitelist.maintenance.list") || sender.hasPermission("xwhitelist.admin")) {
                    List<String> whitelist = plugin.getMaintenanceWhitelistConfig().getStringList("whitelist");
                    if (whitelist.isEmpty()) {
                        sender.sendMessage(plugin.getMessage("commands.maintenance.list.empty"));
                        return true;
                    }
                    sender.sendMessage(plugin.getMessage("commands.maintenance.list.header"));
                    for (String player : whitelist) {
                        sender.sendMessage(plugin.getMessage("commands.maintenance.list.row").replace("%player%", player));
                    }
                    return true;
                }
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("cleanup")) {
                if (sender.hasPermission("xwhitelist.maintenance.cleanup") || sender.hasPermission("xwhitelist.admin")) {
                    plugin.getMaintenanceWhitelistConfig().set("whitelist", null);
                    plugin.saveMaintenanceWhitelist();
                    plugin.reloadMaintenanceWhitelist();
                    sender.sendMessage(plugin.getMessage("commands.maintenance.cleanup.success"));
                    return true;
                }
                sender.sendMessage(plugin.getMessage("error.no_permission"));
                return true;
            }
        }

        sender.sendMessage(plugin.getMessage("commands.plugin.unknown_command"));
        return true;
    }

    private void addPlayerToWhitelistMySQL(Connection connection, String playerName, CommandSender sender) {
        try {
            // Check if the player is already on the whitelist
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    sender.sendMessage(plugin.getMessage("commands.whitelist.add.already").replace("%player%", playerName));
                    return;
                }
            }

            String insertQuery = "INSERT INTO whitelist (username) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                sender.sendMessage(plugin.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
            }

        } catch (SQLException e) {
            sender.sendMessage(plugin.getMessage("error.database_exception"));
        }
    }

    private void removePlayerFromWhitelistMySQL(Connection connection, String playerName, CommandSender sender) {
        try {
            // Check if player is in the whitelist
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    sender.sendMessage(plugin.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
                    return;
                }
            }

            // If the player exists, remove it
            String deleteQuery = "DELETE FROM whitelist WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                sender.sendMessage(plugin.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
            }

        } catch (SQLException e) {
            sender.sendMessage(plugin.getMessage("error.database_exception"));
        }
    }
    private void addPlayerToWhitelistFile(String playerName, CommandSender sender) {
        List<String> whitelist = plugin.getWhitelistConfig().getStringList("whitelist");

        if (whitelist.contains(playerName)) {
            sender.sendMessage(plugin.getMessage("commands.whitelist.add.already")
                    .replace("%player%", playerName));
            return;
        }

        whitelist.add(playerName);
        plugin.getWhitelistConfig().set("whitelist", whitelist);
        plugin.saveWhitelistFile();
        sender.sendMessage(plugin.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
    }

    private void removePlayerFromWhitelistFile(String playerName, CommandSender sender) {
        List<String> whitelist = plugin.getWhitelistConfig().getStringList("whitelist");

        if (!whitelist.contains(playerName)) {
            sender.sendMessage(plugin.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
            return;
        }

        whitelist.remove(playerName);
        plugin.getWhitelistConfig().set("whitelist", whitelist);
        plugin.saveWhitelistFile();
        sender.sendMessage(plugin.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
    }

    private void listWhitelistMySQL(Connection connection, CommandSender sender) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM whitelist")) {

            // Comprobar si la consulta está vacía
            boolean hasPlayers = false;
            String rowFormat = plugin.getMessage("commands.whitelist.list.row");
            while (rs.next()) {
                hasPlayers = true;
                String player = rs.getString("username");
                sender.sendMessage(plugin.getMessage("commands.whitelist.list.header"));
                sender.sendMessage(rowFormat.replace("%player%", player));
            }

            if (!hasPlayers) {
                sender.sendMessage(plugin.getMessage("commands.whitelist.list.empty"));
            }

        } catch (SQLException e) {
            sender.sendMessage(plugin.getMessage("error.database_exception"));
        }
    }
    private void listWhitelistFile(CommandSender sender) {
        List<String> whitelist = plugin.getWhitelistConfig().getStringList("whitelist");
        // Comprobar si la lista de whitelist está vacía
        if (whitelist.isEmpty()) {
            sender.sendMessage(plugin.getMessage("commands.whitelist.list.empty"));
        } else {
            sender.sendMessage(plugin.getMessage("commands.whitelist.list.header"));
            for (String player : whitelist) {
                sender.sendMessage(plugin.getMessage("commands.whitelist.list.row").replace("%player%", player));
            }
        }
    }

    private void cleanupWhitelistMySQL(Connection connection, CommandSender sender) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM whitelist");
            sender.sendMessage(plugin.getMessage("commands.whitelist.cleanup.success"));
        } catch (SQLException e) {
            sender.sendMessage(plugin.getMessage("error.database_exception"));
        }
    }
    private void cleanupWhitelistFile(CommandSender sender) {
        plugin.getWhitelistConfig().set("whitelist", null);
        plugin.saveWhitelistFile();
        plugin.loadWhitelistFile();
        sender.sendMessage(plugin.getMessage("commands.whitelist.cleanup.success"));
    }

    private void helpWhitelist(CommandSender sender) {
        List<String> helpMessages = plugin.getMessageConfig().getStringList("help_message");

        if (helpMessages.isEmpty()) {
            // Mensaje por defecto si no hay configuración en el archivo
            helpMessages = List.of(
                    " ",
                    " ",
                    "                            #ff0000&lX&r&lWhitelist &8» &r&fHelp",
                    " ",
                    "#fff18d&l                    ᴘʟᴜɢɪɴ ᴄᴏᴍᴍᴀɴᴅꜱ",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʜᴇʟᴘ #707070» #ccccccShows this help message",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʀᴇʟᴏᴀᴅ #707070» #ccccccReloads the plugin configuration",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ɪɴꜰᴏ #707070- #ccccccDisplays information about the plugin.",
                    " ",
                    "                     #fff18d&lᴡʜɪᴛᴇʟɪꜱᴛ ᴄᴏᴍᴍᴀɴᴅꜱ",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴇɴᴀʙʟᴇ #707070» #ccccccEnables the whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴅɪꜱᴀʙʟᴇ #707070» #ccccccDisables the whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴀᴅᴅ #707070» #ccccccAdd a player to the whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʀᴇᴍᴏᴠᴇ #707070» #ccccccRemove a player from the whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ʟɪꜱᴛ #707070» #ccccccDisplays the players in the whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴄʟᴇᴀɴᴜᴘ #707070» #ccccccRemoves all players from the whitelist",
                    " ",
                    "                 #fff18d&lᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ᴡʜɪᴛᴇʟɪꜱᴛ ᴄᴏᴍᴍᴀɴᴅꜱ",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ᴇɴᴀʙʟᴇ #707070» #ccccccEnables the maintenance whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ᴅɪꜱᴀʙʟᴇ #707070» #ccccccDisables the maintenance whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ᴀᴅᴅ #707070» #ccccccAdd a player to the maintenance whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ʀᴇᴍᴏᴠᴇ #707070» #ccccccRemove a player from the maintenance whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ʟɪꜱᴛ #707070» #ccccccDisplays the players in the maintenance whitelist",
                    "&f  /xᴡʜɪᴛᴇʟɪꜱᴛ ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ᴄʟᴇᴀɴᴜᴘ #707070» #ccccccRemoves all players from the maintenance whitelist",
                    " ",
                    " "
            );
        }

        for (String line : helpMessages) {
            sender.sendMessage(ChatUtils.formatColor(line));
        }
    }
    private void infoWhitelist(CommandSender sender) {
        String placeholderStatus = plugin.isPlaceholderAPIEnabled() ? "#a0ff72✔" : "#ff7272✖";
        String mySQLStatus = plugin.getConfig().getBoolean("mysql.enable") ? "#a0ff72✔" : "#ff7272✖";
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&8                            #ff0000&lx&r&lWhitelist &8» &r&fInfo"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                           ᴍᴀᴅᴇ ʙʏ"));
        sender.sendMessage(ChatUtils.formatColor("&f                           xDrygo #707070» &7&o(@eldrygo)"));;
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                  ʀᴜɴɴɪɴɢ ᴘʟᴜɢɪɴ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f                                    " + plugin.version));;
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                      ꜰᴇᴀᴛᴜʀᴇꜱ ᴇɴᴀʙʟᴇᴅ"));
        sender.sendMessage(ChatUtils.formatColor("&f                           ᴘʟᴀᴄᴇʜᴏʟᴅᴇʀᴀᴘɪ #707070» #FFFAAB" + placeholderStatus));
        sender.sendMessage(ChatUtils.formatColor("&f                           ᴍʏꜱqʟ ᴡʜɪᴛᴇʟɪꜱᴛ #707070» #FFFAAB" + mySQLStatus));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                      ᴠᴇʀꜱɪᴏɴ ᴄʜᴀɴɢᴇꜱ"));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABAdded multi line kick messages."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABAdded offline mode whitelist."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABAdded enable or disable whitelist."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABAdded management commands for maintenance whitelist."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABMoved command /mwhitelist to /xwhitelist maintenance."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABAdded %prefix% for messages."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABAdded new placeholders."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABAdded tab completion on commands."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABReformed custom message system."));
        sender.sendMessage(ChatUtils.formatColor("&f      #7070701. #FFFAABNew Spigot and Modrinth page!"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l               ᴅʀʏɢᴏ'ꜱ ɴᴏᴛᴇ ᴏꜰ ᴛʜᴇ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       Hi, this was a great update I want to implement "));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       to the plugin, I need to reform the plugin and"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       added new functions for customization. Enjoy it!"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
    }
}