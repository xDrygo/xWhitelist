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
        plugin.getLogger().info("[DEBUG] Comando ejecutado por: " + sender.getName());

        if (args.length < 1) {
            plugin.getLogger().info("[DEBUG] No se proporcionaron argumentos.");
            sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.unknown_command")));
            return true;
        }

        if (!sender.hasPermission("xwhitelist.maintenance")) {
            plugin.getLogger().info("[DEBUG] El usuario " + sender.getName() + " no tiene permisos.");
            sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.no_permission")));
            return true;
        }

        String action = args[0].toLowerCase();
        plugin.getLogger().info("[DEBUG] Acción recibida: " + action);

        switch (action) {
            case "enable":
                if (!mWhitelist.isMaintenanceWhitelistActive()) {
                    plugin.getLogger().info("[DEBUG] Activando MWhitelist...");
                    mWhitelist.toggleMaintenanceWhitelist();
                    sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.enable")));
                } else {
                    plugin.getLogger().info("[DEBUG] La MWhitelist ya estaba activada.");
                    sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.already_enabled")));
                }
                return true;

            case "disable":
                if (mWhitelist.isMaintenanceWhitelistActive()) {
                    plugin.getLogger().info("[DEBUG] Desactivando MWhitelist...");
                    mWhitelist.toggleMaintenanceWhitelist();
                    sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.disable")));
                } else {
                    plugin.getLogger().info("[DEBUG] La MWhitelist ya estaba desactivada.");
                    sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.already_disabled")));
                }
                return true;

            default:
                plugin.getLogger().info("[DEBUG] Comando desconocido: " + action);
                sender.sendMessage(ChatUtils.formatColor(plugin.getMaintenanceWhitelistConfig().getString("messages.unknown_command")));
                return true;
        }
    }
}