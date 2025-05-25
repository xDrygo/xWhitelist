package org.eldrygo.XWhitelist.Handlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.eldrygo.XWhitelist.Managers.ConfigManager;
import org.eldrygo.XWhitelist.Managers.MWhitelistManager;
import org.eldrygo.XWhitelist.Utils.DBUtils;
import org.eldrygo.XWhitelist.XWhitelist;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class XWhitelistTabCompleter implements TabCompleter {

    private final XWhitelist plugin;
    private final ConfigManager configManager;
    private final MWhitelistManager mWhitelistManager;
    private final DBUtils dbUtils;

    public XWhitelistTabCompleter(XWhitelist plugin, ConfigManager configManager, MWhitelistManager mWhitelistManager, DBUtils dbUtils) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.mWhitelistManager = mWhitelistManager;
        this.dbUtils = dbUtils;
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
        List<String> whitelistPlayers;

        // Si MySQL está habilitado, obtenemos los jugadores de la base de datos
        if (plugin.isDataBaseEnabled()) {
            whitelistPlayers = getWhitelistPlayersFromDatabase();
        } else {
            // Si no está habilitado, obtenemos los jugadores desde el archivo "whitelist.yml"
            whitelistPlayers = configManager.getWhitelistConfig().getStringList("whitelist");
        }

        return whitelistPlayers;
    }

    // Obtiene los jugadores de la whitelist desde la base de datos MySQL
    private List<String> getWhitelistPlayersFromDatabase() {
        String type = plugin.getConfig().getString("database.type", "mysql").toLowerCase();
        Object db = dbUtils.getDatabaseConnection();

        List<String> whitelistPlayers = new ArrayList<>();
        try {
            if (type.equalsIgnoreCase("mongodb")) {
                MongoDatabase mongoDB = (MongoDatabase) db;
                MongoCollection<Document> collection = mongoDB.getCollection("whitelist");

                for (Document doc : collection.find()) {
                    String username = doc.getString("username");
                    if (username != null) {
                        whitelistPlayers.add(username);
                    }
                }
            } else {
                Connection connection = (Connection) db;
                String query = "SELECT username FROM whitelist";
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(query)) {

                    while (resultSet.next()) {
                        whitelistPlayers.add(resultSet.getString("username"));
                    }
                }
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to get players from database: " + e.getMessage());
        }

        return whitelistPlayers;
    }
    private List<String> getMaintenanceWhitelistPlayers() {
        return mWhitelistManager.getConfig().getStringList("whitelist");
    }
}
