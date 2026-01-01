package dev.drygo.XWhitelist.Handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import dev.drygo.XWhitelist.Managers.ConfigManager;
import dev.drygo.XWhitelist.Managers.FileWhitelistManager;
import dev.drygo.XWhitelist.Managers.MWhitelistManager;
import dev.drygo.XWhitelist.Managers.MySQLWhitelistManager;
import dev.drygo.XWhitelist.Utils.ChatUtils;
import dev.drygo.XWhitelist.Utils.DBUtils;
import dev.drygo.XWhitelist.XWhitelist;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;

public class XWhitelistCommand implements CommandExecutor {
    private final XWhitelist plugin;

    public XWhitelistCommand(XWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatUtils.getMessage("commands.plugin.unknown_command"));
            return true;
        }
        Connection connection = XWhitelist.getConnection();

        if (args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("xwhitelist.whitelist.add") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.add.usage"));
                return true;
            }
            String playerName = args[1];
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                MySQLWhitelistManager.addPlayerToWhitelistMySQL(connection, playerName, sender);
            } else {
                FileWhitelistManager.addPlayerToWhitelistFile(playerName, sender);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("xwhitelist.whitelist.remove") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.remove.usage"));
                return true;
            }
            String playerName = args[1];
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                MySQLWhitelistManager.removePlayerFromWhitelistMySQL(connection, playerName, sender);
            } else {
                FileWhitelistManager.removePlayerFromWhitelistFile(playerName, sender);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            if (!sender.hasPermission("xwhitelist.whitelist.enable") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (!plugin.getConfig().getBoolean("enabled")) {
                plugin.getConfig().set("enabled", true);
                plugin.saveConfig();
                plugin.reloadConfig();
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.enable.success"));
            } else {
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.enable.already"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            if (!sender.hasPermission("xwhitelist.whitelist.disable") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("enabled")) {
                plugin.getConfig().set("enabled", false);
                plugin.saveConfig();
                plugin.reloadConfig();
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.disable.success"));
            } else {
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.disable.already"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("xwhitelist.whitelist.list") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                MySQLWhitelistManager.listWhitelistMySQL(connection,  sender);
            } else {
                FileWhitelistManager.listWhitelistFile(sender);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("cleanup")) {
            if (!sender.hasPermission("xwhitelist.whitelist.cleanup") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                MySQLWhitelistManager.cleanupWhitelistMySQL(connection,  sender);
            } else {
                FileWhitelistManager.cleanupWhitelistFile(sender);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("xwhitelist.plugin.reload") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            reloadConfig();
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("xwhitelist.plugin.help") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            helpWhitelist(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission("xwhitelist.plugin.info") && !sender.hasPermission("xwhitelist.admin")) {
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
            infoWhitelist(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("maintenance")) {
            if (args[1].equalsIgnoreCase("enable")) {
                if (sender.hasPermission("xwhitelist.maintenance.enable") || sender.hasPermission("xwhitelist.admin")) {
                    if (!MWhitelistManager.isMaintenanceWhitelistActive()) {
                        MWhitelistManager.toggleMaintenanceWhitelist();
                        ConfigManager.reloadMaintenanceWhitelist();
                        sender.sendMessage(ChatUtils.getMessage("commands.maintenance.enable.success"));
                    } else {
                        sender.sendMessage(ChatUtils.getMessage("commands.maintenance.enable.already"));
                    }
                } else {
                    sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                }
                return true;
            }

            if (args[1].equalsIgnoreCase("disable")) {
                if (sender.hasPermission("xwhitelist.maintenance.disable") || sender.hasPermission("xwhitelist.admin")) {
                    if (MWhitelistManager.isMaintenanceWhitelistActive()) {
                        MWhitelistManager.toggleMaintenanceWhitelist();
                        ConfigManager.reloadMaintenanceWhitelist();
                        sender.sendMessage(ChatUtils.getMessage("commands.maintenance.disable.success"));
                    } else {
                        sender.sendMessage(ChatUtils.getMessage("commands.maintenance.disable.already"));
                    }
                } else {
                    sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                }
                return true;
            }

            if (args[1].equalsIgnoreCase("add")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatUtils.getMessage("commands.maintenance.add.usage"));
                    return true;
                }
                if (sender.hasPermission("xwhitelist.maintenance.add") || sender.hasPermission("xwhitelist.admin")) {
                    String player = args[2];
                    List<String> whitelist = ConfigManager.getMaintenanceWhitelistConfig().getStringList("whitelist");

                    if (whitelist.contains(player)) {
                        sender.sendMessage(ChatUtils.getMessage("commands.maintenance.add.already").replace("%player%", player));
                        return true;
                    }

                    whitelist.add(player);
                    ConfigManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
                    ConfigManager.saveMaintenanceWhitelist();
                    ConfigManager.reloadMaintenanceWhitelist();
                    sender.sendMessage(ChatUtils.getMessage("commands.maintenance.add.success").replace("%player%", player));
                    return true;
                }
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatUtils.getMessage("commands.maintenance.remove.usage"));
                    return true;
                }
                if (sender.hasPermission("xwhitelist.maintenance.remove") || sender.hasPermission("xwhitelist.admin")) {
                    String player = args[2];
                    List<String> whitelist = ConfigManager.getMaintenanceWhitelistConfig().getStringList("whitelist");

                    if (!whitelist.contains(player)) {
                        sender.sendMessage(ChatUtils.getMessage("commands.maintenance.remove.already").replace("%player%", player));
                        return true;
                    }

                    whitelist.remove(player);
                    ConfigManager.getMaintenanceWhitelistConfig().set("whitelist", whitelist);
                    ConfigManager.saveMaintenanceWhitelist();
                    ConfigManager.reloadMaintenanceWhitelist();
                    sender.sendMessage(ChatUtils.getMessage("commands.maintenance.remove.success").replace("%player%", player));
                    return true;
                }
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("list")) {
                if (sender.hasPermission("xwhitelist.maintenance.list") || sender.hasPermission("xwhitelist.admin")) {
                    List<String> whitelist = ConfigManager.getMaintenanceWhitelistConfig().getStringList("whitelist");
                    if (whitelist.isEmpty()) {
                        sender.sendMessage(ChatUtils.getMessage("commands.maintenance.list.empty"));
                        return true;
                    }
                    sender.sendMessage(ChatUtils.getMessage("commands.maintenance.list.header"));
                    for (String player : whitelist) {
                        sender.sendMessage(ChatUtils.getMessage("commands.maintenance.list.row").replace("%player%", player));
                    }
                    return true;
                }
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }

            if (args[1].equalsIgnoreCase("cleanup")) {
                if (sender.hasPermission("xwhitelist.maintenance.cleanup") || sender.hasPermission("xwhitelist.admin")) {
                    ConfigManager.getMaintenanceWhitelistConfig().set("whitelist", null);
                    ConfigManager.saveMaintenanceWhitelist();
                    ConfigManager.reloadMaintenanceWhitelist();
                    sender.sendMessage(ChatUtils.getMessage("commands.maintenance.cleanup.success"));
                    return true;
                }
                sender.sendMessage(ChatUtils.getMessage("error.no_permission"));
                return true;
            }
        }

        sender.sendMessage(ChatUtils.getMessage("commands.plugin.unknown_command"));
        return true;
    }
    private void helpWhitelist(CommandSender sender) {
        List<String> helpMessages = ConfigManager.getMessageConfig().getStringList("help_message");

        if (helpMessages.isEmpty()) {
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
        String placeholderStatus = XWhitelist.isPlaceholderAPIEnabled() ? "#a0ff72✔" : "#ff7272✖";
        String mySQLStatus = plugin.getConfig().getBoolean("mysql.enable") ? "#a0ff72✔" : "#ff7272✖";
        sender.sendMessage(ChatUtils.formatColor("#666666+==================================================+"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&8                            #ff0000&lx&r&lWhitelist &8» &r&fInfo"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                           ᴍᴀᴅᴇ ʙʏ"));
        sender.sendMessage(ChatUtils.formatColor("&f                           xDrygo #707070» &7&o(@eldrygo)"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                  ʀᴜɴɴɪɴɢ ᴘʟᴜɢɪɴ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f                                    " + XWhitelist.version));
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
        ConfigManager.reloadPluginConfig();
        ConfigManager.loadMaintenanceWhitelist();
        ConfigManager.reloadMessages();
        ConfigManager.loadWhitelistFile();
        boolean newMySQLEnabled = plugin.getConfig().getBoolean("mysql.enable", false);
        if (XWhitelist.useMySQL || newMySQLEnabled) {
            DBUtils.reloadDatabaseConnection();
        }
    }
}