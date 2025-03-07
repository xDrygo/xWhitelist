package org.eldrygo.MWhitelist;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.eldrygo.ChatUtils;
import org.eldrygo.XWhitelist;

public class MWhitelistCommand implements CommandExecutor {

    private final XWhitelist plugin;
    private final MWhitelist mWhitelist;

    public MWhitelistCommand(XWhitelist plugin, MWhitelist mWhitelist) {
        this.plugin = plugin;
        this.mWhitelist = mWhitelist;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.unknown_command")));
            return true;
        }

        String action = args[0].toLowerCase();

        if (args[0].equalsIgnoreCase("enable")) {
            if (sender.hasPermission("xwhitelist.maintenance.enable") || sender.hasPermission("xwhitelist.admin")) {
                if (!mWhitelist.isMaintenanceWhitelistActive()) {
                    mWhitelist.toggleMaintenanceWhitelist();
                    plugin.reloadMaintenanceWhitelist();
                    sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.enable")));
                } else {
                    sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.already_enabled")));
                }
                return true;
            } else {
                sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.no_permission")));
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("disable")) {
            if (sender.hasPermission("xwhitelist.maintenance.disable") || sender.hasPermission("xwhitelist.admin")) {
                if (mWhitelist.isMaintenanceWhitelistActive()) {
                    mWhitelist.toggleMaintenanceWhitelist();
                    plugin.reloadMaintenanceWhitelist();
                    sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.disable")));
                } else {
                    sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.already_disabled")));
                }
                return true;
            }
        }
        sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.unknown_command")));
        return true;
    }
}