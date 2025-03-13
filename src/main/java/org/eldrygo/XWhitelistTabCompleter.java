package org.eldrygo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.eldrygo.MWhitelist.MWhitelist;

import java.util.ArrayList;
import java.util.List;

public class XWhitelistTabCompleter implements TabCompleter {

    private XWhitelist plugin;

    public XWhitelistTabCompleter(XWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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


    // Obtiene una lista de todos los jugadores conectados
    private List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }

    // Obtiene los jugadores que están en la whitelist (dependiendo de si MySQL está habilitado o no)
    private List<String> getWhitelistPlayers() {
        List<String> whitelistPlayers = new ArrayList<>();

        // Si MySQL está habilitado, obtenemos los jugadores de la base de datos
        if (plugin.isMySQLEnabled()) {
            whitelistPlayers = getWhitelistPlayersFromDatabase();
        } else {
            // Si no está habilitado, obtenemos los jugadores desde el archivo "whitelist.yml"
            whitelistPlayers = plugin.getWhitelistConfig().getStringList("whitelist");
        }

        return whitelistPlayers;
    }

    // Obtiene los jugadores de la whitelist desde la base de datos MySQL
    private List<String> getWhitelistPlayersFromDatabase() {
        List<String> whitelistPlayers = new ArrayList<>();
        try {
            // Aquí haces la consulta SQL para obtener los jugadores de la whitelist
            // Asegúrate de que el nombre de la tabla y la columna coincidan con tu base de datos
            String query = "SELECT player_name FROM whitelist";
            var statement = plugin.getConnection().createStatement();
            var resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                whitelistPlayers.add(resultSet.getString("player_name"));
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed on get players on database: " + e.getMessage());
        }
        return whitelistPlayers;
    }
    private List<String> getMaintenanceWhitelistPlayers() {
        return MWhitelist.getConfig().getStringList("whitelist");
    }
}
