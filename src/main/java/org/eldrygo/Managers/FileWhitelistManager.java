package org.eldrygo.Managers;

import org.bukkit.command.CommandSender;
import org.eldrygo.Utils.ChatUtils;

import java.util.List;

public class FileWhitelistManager {

    private final ConfigManager configManager;
    private final ChatUtils chatUtils;

    public FileWhitelistManager(ConfigManager configManager, ChatUtils chatUtils) {
        this.configManager = configManager;
        this.chatUtils = chatUtils;
    }

    public void addPlayerToWhitelistFile(String playerName, CommandSender sender) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");

        if (whitelist.contains(playerName)) {
            sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.already")
                    .replace("%player%", playerName));
            return;
        }

        whitelist.add(playerName);
        configManager.getWhitelistConfig().set("whitelist", whitelist);
        configManager.saveWhitelistFile();
        sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
    }

    public void removePlayerFromWhitelistFile(String playerName, CommandSender sender) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");

        if (!whitelist.contains(playerName)) {
            sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
            return;
        }

        whitelist.remove(playerName);
        configManager.getWhitelistConfig().set("whitelist", whitelist);
        configManager.saveWhitelistFile();
        sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
    }
    public void cleanupWhitelistFile(CommandSender sender) {
        configManager.getWhitelistConfig().set("whitelist", null);
        configManager.saveWhitelistFile();
        configManager.loadWhitelistFile();
        sender.sendMessage(chatUtils.getMessage("commands.whitelist.cleanup.success"));
    }
    public void listWhitelistFile(CommandSender sender) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");
        // Comprobar si la lista de whitelist está vacía
        if (whitelist.isEmpty()) {
            sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.empty"));
        } else {
            sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.header"));
            for (String player : whitelist) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.row").replace("%player%", player));
            }
        }
    }
}
