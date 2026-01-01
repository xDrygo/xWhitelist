package dev.drygo.XWhitelist.Handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import dev.drygo.XWhitelist.Managers.ConfigManager;
import dev.drygo.XWhitelist.Managers.MWhitelistManager;
import dev.drygo.XWhitelist.XWhitelist;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class XWhitelistTabCompleter implements TabCompleter {

    private final XWhitelist plugin;

    public XWhitelistTabCompleter(XWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("xwhitelist")) {

            if (args.length == 1) {
                List<String> options = List.of("enable", "disable", "add", "remove", "list", "cleanup", "reload", "help", "info", "maintenance");
                StringUtil.copyPartialMatches(args[0], options, suggestions);
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add")) {
                    List<String> playerNames = getAllPlayerNames();
                    playerNames.removeAll(getWhitelistPlayers());
                    StringUtil.copyPartialMatches(args[1], playerNames, suggestions);
                }
                else if (args[0].equalsIgnoreCase("remove")) {
                    StringUtil.copyPartialMatches(args[1], getWhitelistPlayers(), suggestions);
                }
                else if (args[0].equalsIgnoreCase("maintenance")) {
                    List<String> options = List.of("enable", "disable", "add", "remove", "list", "cleanup");
                    StringUtil.copyPartialMatches(args[1], options, suggestions);
                }
            }
            else if (args.length == 3 && args[0].equalsIgnoreCase("maintenance")) {
                if (args[1].equalsIgnoreCase("add")) {
                    List<String> playerNames = getAllPlayerNames();
                    playerNames.removeAll(getMaintenanceWhitelistPlayers());
                    StringUtil.copyPartialMatches(args[2], playerNames, suggestions);
                }
                else if (args[1].equalsIgnoreCase("remove")) {
                    StringUtil.copyPartialMatches(args[2], getMaintenanceWhitelistPlayers(), suggestions);
                }
            }
        }

        return suggestions;
    }


    private List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }

    private List<String> getWhitelistPlayers() {
        List<String> whitelistPlayers;
        if (XWhitelist.isMySQLEnabled()) {
            whitelistPlayers = getWhitelistPlayersFromDatabase();
        } else {
            whitelistPlayers = ConfigManager.getWhitelistConfig().getStringList("whitelist");
        }

        return whitelistPlayers;
    }

    private List<String> getWhitelistPlayersFromDatabase() {
        List<String> whitelistPlayers = new ArrayList<>();
        try {
            String query = "SELECT username FROM whitelist";
            var statement = XWhitelist.getConnection().createStatement();
            var resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                whitelistPlayers.add(resultSet.getString("username"));
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed on get players on database: " + e.getMessage());
        }
        return whitelistPlayers;
    }
    private List<String> getMaintenanceWhitelistPlayers() {
        return MWhitelistManager.getConfig().getStringList("whitelist");
    }
}
