package org.eldrygo.XWhitelist.Managers;

import org.bukkit.command.CommandSender;
import org.eldrygo.XWhitelist.Utils.ChatUtils;

import java.util.List;

public class FileWhitelistManager {

    private final ConfigManager configManager;
    private final ChatUtils chatUtils;

    public FileWhitelistManager(ConfigManager configManager, ChatUtils chatUtils) {
        this.configManager = configManager;
        this.chatUtils = chatUtils;
    }

    public boolean isPlayerWhitelisted(String playerName) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");

        return whitelist.contains(playerName);
    }

    public void addPlayer(String playerName, CommandSender sender) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");

        if (whitelist.contains(playerName)) {
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.already")
                        .replace("%player%", playerName));
            }
            return;
        }

        whitelist.add(playerName);
        configManager.getWhitelistConfig().set("whitelist", whitelist);
        configManager.saveWhitelistFile();
        if (sender != null) {
            sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
        }
    }

    public void removePlayer(String playerName, CommandSender sender) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");

        if (!whitelist.contains(playerName)) {
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
            }
            return;
        }

        whitelist.remove(playerName);
        configManager.getWhitelistConfig().set("whitelist", whitelist);
        configManager.saveWhitelistFile();
        if (sender != null) {
            sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
        }
    }

    public void cleanup(CommandSender sender) {
        configManager.getWhitelistConfig().set("whitelist", null);
        configManager.saveWhitelistFile();
        configManager.loadWhitelistFile();
        if (sender != null) {
            sender.sendMessage(chatUtils.getMessage("commands.whitelist.cleanup.success"));
        }
    }

    public void listWhitelist(CommandSender sender) {
        List<String> whitelist = configManager.getWhitelistConfig().getStringList("whitelist");
        if (sender == null) return;

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
