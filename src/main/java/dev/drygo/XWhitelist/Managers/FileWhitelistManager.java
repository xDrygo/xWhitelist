package dev.drygo.XWhitelist.Managers;

import org.bukkit.command.CommandSender;
import dev.drygo.XWhitelist.Utils.ChatUtils;

import java.util.List;

public class FileWhitelistManager {

    public static void addPlayerToWhitelistFile(String playerName, CommandSender sender) {
        List<String> whitelist = ConfigManager.getWhitelistConfig().getStringList("whitelist");

        if (whitelist.contains(playerName)) {
            sender.sendMessage(ChatUtils.getMessage("commands.whitelist.add.already")
                    .replace("%player%", playerName));
            return;
        }

        whitelist.add(playerName);
        ConfigManager.getWhitelistConfig().set("whitelist", whitelist);
        ConfigManager.saveWhitelistFile();
        sender.sendMessage(ChatUtils.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
    }

    public static void removePlayerFromWhitelistFile(String playerName, CommandSender sender) {
        List<String> whitelist = ConfigManager.getWhitelistConfig().getStringList("whitelist");

        if (!whitelist.contains(playerName)) {
            sender.sendMessage(ChatUtils.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
            return;
        }

        whitelist.remove(playerName);
        ConfigManager.getWhitelistConfig().set("whitelist", whitelist);
        ConfigManager.saveWhitelistFile();
        sender.sendMessage(ChatUtils.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
    }
    public static void cleanupWhitelistFile(CommandSender sender) {
        ConfigManager.getWhitelistConfig().set("whitelist", null);
        ConfigManager.saveWhitelistFile();
        ConfigManager.loadWhitelistFile();
        sender.sendMessage(ChatUtils.getMessage("commands.whitelist.cleanup.success"));
    }
    public static void listWhitelistFile(CommandSender sender) {
        List<String> whitelist = ConfigManager.getWhitelistConfig().getStringList("whitelist");
        if (whitelist.isEmpty()) {
            sender.sendMessage(ChatUtils.getMessage("commands.whitelist.list.empty"));
        } else {
            sender.sendMessage(ChatUtils.getMessage("commands.whitelist.list.header"));
            for (String player : whitelist) {
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.list.row").replace("%player%", player));
            }
        }
    }
}
