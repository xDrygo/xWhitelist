package org.eldrygo.XWhitelist.Utils;

import org.bukkit.Bukkit;
import org.eldrygo.XWhitelist.XWhitelist;

public class LogsUtils {
    private final XWhitelist plugin;

    public LogsUtils(XWhitelist plugin) {
        this.plugin = plugin;
    }
    public void sendRunMessage() {
        if (plugin.getConfig().getBoolean("plugin.first_run", true)) {
            onFirstRun();
            plugin.getConfig().set("plugin.first_run", false);
            plugin.saveConfig();
        } else {
            if (plugin.useDatabase) {
                startWithMySQL();
            } else {
                startOffline();
            }
        }
    }
    public void sendStartupMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#ff177c&lx&r&lWhitelist #a0ff72plugin enabled!"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dVersion: #ffffff" + plugin.version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dDeveloped by: #ffffff" + String.join(", ", plugin.getDescription().getAuthors())));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
    }
    public void sendShutdownMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#ff177c&lx&r&lWhitelist #ff7272plugin disabled!"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dVersion: #ffffff" + plugin.version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dDeveloped by: #ffffff" + String.join(", ", plugin.getDescription().getAuthors())));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
    }
    public void onFirstRun() {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("                         #ff177c&lx&r&lWhitelist"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dHi, since this is the first time you've started the server, the plugin"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dstarted with the MySQL option disabled. If you want to use this feature,"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18d         you can enable the MySQL feature in config.yml."));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffff  You can find a guide for the plugin in the modrinth/spigot page."));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
    }
    public void startWithMySQL() {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("                         #ff177c&lx&r&lWhitelist"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dHi, now you are using the whitelist with the MySQL feature, that means"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dthe whitelist is stored in a database, remember this whitelist and the"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18doffline whitelist are different, you can manage it with the commands."));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffffYou disable the MySQL feature in the config, on the file config.yml"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
    }
    public void startOffline() {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("                         #ff177c&lx&r&lWhitelist"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dHi, now you are using the whitelist on the offline mode, that means"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dthe whitelist is stored in a local file (whitelist.yml), you can manage"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18d   the list in the file or with the commands that the plugin adds."));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#ffffffYou enable the MySQL feature in the config, on the file config.yml"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
    }
}
