package org.eldrygo.XWhitelist.Handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Managers.FileWhitelistManager;
import org.eldrygo.XWhitelist.Managers.MWhitelistManager;
import org.eldrygo.XWhitelist.Managers.MySQLWhitelistManager;
import org.eldrygo.XWhitelist.Utils.ChatUtils;
import org.eldrygo.XWhitelist.Utils.DBUtils;
import org.eldrygo.XWhitelist.XWhitelist;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;

public class XWhitelistCommand implements CommandExecutor {
    private final XWhitelist plugin;
    private final MWhitelistManager mWhitelistManager;
    private final ConfigManager configManager;
    private final FileWhitelistManager fileWhitelistManager;
    private final MySQLWhitelistManager mySQLWhitelistManager;
    private final ChatUtils chatUtils;
    private final DBUtils dbUtils;

    public XWhitelistCommand(XWhitelist plugin, MWhitelistManager mWhitelistManager, ConfigManager configManager, FileWhitelistManager fileWhitelistManager, MySQLWhitelistManager mySQLWhitelistManager, ChatUtils chatUtils, DBUtils dbUtils) {
        this.plugin = plugin;
        this.mWhitelistManager = mWhitelistManager;
        this.configManager = configManager;
        this.fileWhitelistManager = fileWhitelistManager;
        this.mySQLWhitelistManager = mySQLWhitelistManager;
        this.chatUtils = chatUtils;
        this.dbUtils = dbUtils;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(chatUtils.getMessage("commands.plugin.unknown_command"));
            return true;
        }
        Connection connection = plugin.getConnection();

        if (args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("xwhitelist.whitelist.add") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.usage"));
                return true;
            }
            String playerName = args[1];
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                mySQLWhitelistManager.addPlayerToWhitelistMySQL(connection, playerName, sender);
            } else {
                fileWhitelistManager.addPlayerToWhitelistFile(playerName, sender);
            }
            return true;
        }

        // Command: /xwhitelist remove
        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("xwhitelist.whitelist.remove") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.usage"));
                return true;
            }
            String playerName = args[1];
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                mySQLWhitelistManager.removePlayerFromWhitelistMySQL(connection, playerName, sender);
            } else {
                fileWhitelistManager.removePlayerFromWhitelistFile(playerName, sender);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            if (!sender.hasPermission("xwhitelist.whitelist.enable") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (!plugin.getConfig().getBoolean("enabled")) {
                plugin.getConfig().set("enabled", true);
                plugin.saveConfig();
                plugin.reloadConfig();
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.enable.success"));
            } else {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.enable.already"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            if (!sender.hasPermission("xwhitelist.whitelist.disable") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("enabled")) {
                plugin.getConfig().set("enabled", false);
                plugin.saveConfig();
                plugin.reloadConfig();
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.disable.success"));
            } else {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.disable.already"));
            }
            return true;
        }

        // Command: /xwhitelist list
        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("xwhitelist.whitelist.list") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                mySQLWhitelistManager.listWhitelistMySQL(connection,  sender);
            } else {
                fileWhitelistManager.listWhitelistFile(sender);
            }
            return true;
        }

        // Command: /xwhitelist cleanup
        if (args[0].equalsIgnoreCase("cleanup")) {
            if (!sender.hasPermission("xwhitelist.whitelist.cleanup") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                mySQLWhitelistManager.cleanupWhitelistMySQL(connection,  sender);
            } else {
                fileWhitelistManager.cleanupWhitelistFile(sender);
            }
            return true;
        }

        // Command: /xwhitelist reload
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("xwhitelist.plugin.reload") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            reloadConfig();
            return true;

        // Command: /xwhitelist help
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("xwhitelist.plugin.help") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            helpWhitelist(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission("xwhitelist.plugin.info") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
            infoWhitelist(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("maintenance")) {
            if (args[1].equalsIgnoreCase("enable")) {
                if (sender.hasPermission("xwhitelist.maintenance.enable") || sender.hasPermission("xwhitelist.admin")) {
                    if (!mWhitelistManager.isMaintenanceWhitelistActive()) {
                        mWhitelistManager.toggleMaintenanceWhitelist();
                        configManager.reloadMaintenanceWhitelist();
                        sender.sendMessage(chatUtils.getMessage("commands.maintenance.enable.success"));
                    } else {
                        sender.sendMessage(chatUtils.getMessage("commands.maintenance.enable.already"));
                    }
                } else {
                    sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                }
                return true;
            }

            if (args[1].equalsIgnoreCase("disable")) {
                if (sender.hasPermission("xwhitelist.maintenance.disable") || sender.hasPermission("xwhitelist.admin")) {
                    if (mWhitelistManager.isMaintenanceWhitelistActive()) {
                        mWhitelistManager.toggleMaintenanceWhitelist();
                        configManager.reloadMaintenanceWhitelist();
                        sender.sendMessage(chatUtils.getMessage("commands.maintenance.disable.success"));
                    } else {
                        sender.sendMessage(chatUtils.getMessage("commands.maintenance.disable.already"));
                    }
                } else {
                    sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                }
                return true;
            }

            if (args[1].equalsIgnoreCase("add")) {
                if (args.length < 3) {
                    sender.sendMessage(chatUtils.getMessage("commands.maintenance.add.usage"));
                    return true;
                }
                if (sender.hasPermission("xwhitelist.maintenance.add") || sender.hasPermission("xwhitelist.admin")) {
                    String player = args[2];
                    List<String> whitelist = configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");

                    if (whitelist.contains(player)) {
                        sender.sendMessage(chatUtils.getMessage("commands.maintenance.add.already").replace("%player%", player));
                        return true;
                    }

                    whitelist.add(player);
                    configManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
                    configManager.saveMaintenanceWhitelist();
                    configManager.reloadMaintenanceWhitelist();
                    sender.sendMessage(chatUtils.getMessage("commands.maintenance.add.success").replace("%player%", player));
                    return true;
                }
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    sender.sendMessage(chatUtils.getMessage("commands.maintenance.remove.usage"));
                    return true;
                }
                if (sender.hasPermission("xwhitelist.maintenance.remove") || sender.hasPermission("xwhitelist.admin")) {
                    String player = args[2];
                    List<String> whitelist = configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");

                    if (!whitelist.contains(player)) {
                        sender.sendMessage(chatUtils.getMessage("commands.maintenance.remove.already").replace("%player%", player));
                        return true;
                    }

                    whitelist.remove(player);
                    configManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
                    configManager.saveMaintenanceWhitelist();
                    configManager.reloadMaintenanceWhitelist();
                    sender.sendMessage(chatUtils.getMessage("commands.maintenance.remove.success").replace("%player%", player));
                    return true;
                }
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("list")) {
                if (sender.hasPermission("xwhitelist.maintenance.list") || sender.hasPermission("xwhitelist.admin")) {
                    List<String> whitelist = configManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
                    if (whitelist.isEmpty()) {
                        sender.sendMessage(chatUtils.getMessage("commands.maintenance.list.empty"));
                        return true;
                    }
                    sender.sendMessage(chatUtils.getMessage("commands.maintenance.list.header"));
                    for (String player : whitelist) {
                        sender.sendMessage(chatUtils.getMessage("commands.maintenance.list.row").replace("%player%", player));
                    }
                    return true;
                }
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("cleanup")) {
                if (sender.hasPermission("xwhitelist.maintenance.cleanup") || sender.hasPermission("xwhitelist.admin")) {
                    configManager.getMaintenanceWhitelistConfig().set("whitelist", null);
                    configManager.saveMaintenanceWhitelist();
                    configManager.reloadMaintenanceWhitelist();
                    sender.sendMessage(chatUtils.getMessage("commands.maintenance.cleanup.success"));
                    return true;
                }
                sender.sendMessage(chatUtils.getMessage("error.no_permission"));
                return true;
            }
        }

        sender.sendMessage(chatUtils.getMessage("commands.plugin.unknown_command"));
        return true;
    }
    private void helpWhitelist(CommandSender sender) {
        List<String> helpMessages = configManager.getMessageConfig().getStringList("help_message");

        if (helpMessages.isEmpty()) {
            // Mensaje por defecto si no hay configuración en el archivo
            helpMessages = List.of(
                    " ",
                    " ","                            #ff0000&lX&r&lWhitelist &8» &r&fHelp"
                    ,
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
        sender.sendMessage(ChatUtils.formatColor("&f                           xDrygo #707070» &7&o(@eldrygo)"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                  ʀᴜɴɴɪɴɢ ᴘʟᴜɢɪɴ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f                                    " + plugin.version));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                      ꜰᴇᴀᴛᴜʀᴇꜱ ᴇɴᴀʙʟᴇᴅ"));
        sender.sendMessage(ChatUtils.formatColor("&f                           ᴘʟᴀᴄᴇʜᴏʟᴅᴇʀᴀᴘɪ #707070» #FFFAAB" + placeholderStatus));
        sender.sendMessage(ChatUtils.formatColor("&f                           ᴍʏꜱqʟ ᴡʜɪᴛᴇʟɪꜱᴛ #707070» #FFFAAB" + mySQLStatus));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l               ᴅʀʏɢᴏ'ꜱ ɴᴏᴛᴇ ᴏꜰ ᴛʜᴇ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       Hi, this was a great update I want to implement "));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       to the plugin, I need to reform the plugin and"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       added new functions for customization. Enjoy it!"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
    }
    public void reloadConfig() {
        configManager.reloadPluginConfig();
        configManager.loadMaintenanceWhitelist();
        configManager.reloadMessages();
        configManager.loadWhitelistFile();
        boolean newMySQLEnabled = plugin.getConfig().getBoolean("mysql.enable", false);
        if (plugin.useMySQL || newMySQLEnabled) {
            dbUtils.reloadDatabaseConnection();
        }
    }
}