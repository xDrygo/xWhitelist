package org.eldrygo.XWhitelist.Utils;

import org.bukkit.ChatColor;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.XWhitelist;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static XWhitelist plugin;

    public static void init(XWhitelist plugin) {
        ChatUtils.plugin = plugin;
    }

    public static String formatColor(String message) {
        message = replaceHexColors(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String replaceHexColors(String message) {
        Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            StringBuilder color = new StringBuilder("&x");
            for (char c : hexColor.toCharArray()) {
                color.append("&").append(c);
            }
            matcher.appendReplacement(buffer, color.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    public static String getMessage(String path) {
        String message = ConfigManager.getMessagesConfig().getString(path);
        if (ConfigManager.getMessagesConfig().isList(path)) {
            List<String> lines = ConfigManager.getMessagesConfig().getStringList(path);
            return ChatUtils.formatColor(String.join("\n", lines));
        } else {
            if (message == null || message.isEmpty()) {
                plugin.getLogger().warning("[WARNING] Message not found: " + path);
                return ChatUtils.formatColor("%prefix% #FF0000&l[ERROR] #FF3535Message not found: " + path).replace("%prefix%", XWhitelist.getPrefix());
            }
            return ChatUtils.formatColor(message.replace("%prefix%", XWhitelist.getPrefix()));
        }
    }
    public static String formatMultiLineMessage(List<String> messages, String playerName) {
        String prefix = ConfigManager.getMessageConfig().getString("prefix", "#ff177c&lx&r&lWhitelist &8»&r"); // Valor por defecto del prefix

        StringBuilder formattedMessage = new StringBuilder();
        for (String line : messages) {
            formattedMessage.append(ChatUtils.formatColor(
                    line.replace("%player%", playerName)
                            .replace("%prefix%", prefix)
            )).append("\n");
        }
        return formattedMessage.toString().trim();
    }
}