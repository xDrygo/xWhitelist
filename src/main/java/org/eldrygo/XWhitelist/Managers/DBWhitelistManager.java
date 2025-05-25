package org.eldrygo.XWhitelist.Managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.eldrygo.XWhitelist.Utils.ChatUtils;
import org.eldrygo.XWhitelist.Utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBWhitelistManager {
    private final ChatUtils chatUtils;
    private final DBUtils dbUtils;

    public DBWhitelistManager(ChatUtils chatUtils, DBUtils dbUtils) {
        this.chatUtils = chatUtils;
        this.dbUtils = dbUtils;
    }

    public void addPlayer(String playerName, CommandSender sender) {
        Object db = dbUtils.getDatabaseConnection();
        String type = dbUtils.getDBType();

        if (type.equalsIgnoreCase("mongodb")) {
            MongoDatabase mongoDB = (MongoDatabase) db;
            MongoCollection<Document> collection = mongoDB.getCollection("whitelist");
            Document existing = collection.find(new Document("username", playerName)).first();
            if (existing != null) {
                if (sender != null) {
                    sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.already").replace("%player%", playerName));
                }
                return;
            }

            Document doc = new Document("username", playerName)
                    .append("added_at", System.currentTimeMillis());
            collection.insertOne(doc);
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
            }
            return;
        }

        // SQL
        Connection connection = (Connection) db;
        try {
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    if (sender != null) {
                        sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.already").replace("%player%", playerName));
                    }
                    return;
                }
            }

            String insertQuery = "INSERT INTO whitelist (username) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                if (sender != null) {
                    sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
                }
            }

        } catch (SQLException e) {
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("error.database_exception"));
            }
        }
    }

    public void removePlayer(String playerName, CommandSender sender) {
        Object db = dbUtils.getDatabaseConnection();
        String type = dbUtils.getDBType();
        if (type.equalsIgnoreCase("mongodb")) {
            MongoDatabase mongoDB = (MongoDatabase) db;
            MongoCollection<Document> collection = mongoDB.getCollection("whitelist");
            Document existing = collection.find(new Document("username", playerName)).first();
            if (existing == null) {
                if (sender != null) {
                    sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
                }
                return;
            }

            collection.deleteOne(new Document("username", playerName));
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
            }
            return;
        }

        // SQL
        Connection connection = (Connection) db;
        try {
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    if (sender != null) {
                        sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
                    }
                    return;
                }
            }

            String deleteQuery = "DELETE FROM whitelist WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                if (sender != null) {
                    sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
                }
            }

        } catch (SQLException e) {
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("error.database_exception"));
            }
        }
    }

    public void listPlayers(CommandSender sender) {
        if (sender == null) return; // si es null, no hacer nada
        Object db = dbUtils.getDatabaseConnection();
        String type = dbUtils.getDBType();

        sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.header"));

        if (type.equalsIgnoreCase("mongodb")) {
            MongoDatabase mongoDB = (MongoDatabase) db;
            MongoCollection<Document> collection = mongoDB.getCollection("whitelist");

            boolean hasPlayers = false;
            for (Document doc : collection.find()) {
                hasPlayers = true;
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.row").replace("%player%", doc.getString("username")));
            }

            if (!hasPlayers) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.empty"));
            }
            return;
        }

        // SQL
        Connection connection = (Connection) db;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM whitelist")) {

            boolean hasPlayers = false;
            while (rs.next()) {
                hasPlayers = true;
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.row").replace("%player%", rs.getString("username")));
            }

            if (!hasPlayers) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.empty"));
            }

        } catch (SQLException e) {
            sender.sendMessage(chatUtils.getMessage("error.database_exception"));
        }
    }

    public void cleanup(CommandSender sender) {
        Object db = dbUtils.getDatabaseConnection();
        String type = dbUtils.getDBType();
        if (type.equalsIgnoreCase("mongodb")) {
            MongoDatabase mongoDB = (MongoDatabase) db;
            MongoCollection<Document> collection = mongoDB.getCollection("whitelist");
            collection.deleteMany(new Document());
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.cleanup.success"));
            }
            return;
        }

        // SQL
        Connection connection = (Connection) db;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM whitelist");
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.cleanup.success"));
            }
        } catch (SQLException e) {
            if (sender != null) {
                sender.sendMessage(chatUtils.getMessage("error.database_exception"));
            }
        }
    }

    public boolean isPlayerWhitelisted(String playerName) {
        Object db = dbUtils.getDatabaseConnection();
        String type = dbUtils.getDBType();

        if (type.equalsIgnoreCase("mongodb")) {
            MongoDatabase mongoDB = (MongoDatabase) db;
            MongoCollection<Document> collection = mongoDB.getCollection("whitelist");
            Document existing = collection.find(new Document("username", playerName)).first();
            return existing != null;
        }

        // SQL
        Connection connection = (Connection) db;
        try {
            String query = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, playerName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // Opcional: puedes manejar la excepción o registrar el error
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getWhitelistedPlayers() {
        List<String> players = new ArrayList<>();
        Object db = dbUtils.getDatabaseConnection();
        String type = dbUtils.getDBType();

        if (type.equalsIgnoreCase("mongodb")) {
            MongoDatabase mongoDB = (MongoDatabase) db;
            MongoCollection<Document> collection = mongoDB.getCollection("whitelist");

            for (Document doc : collection.find()) {
                String username = doc.getString("username");
                if (username != null) {
                    players.add(username);
                }
            }
            return players;
        }

        // SQL
        Connection connection = (Connection) db;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM whitelist")) {

            while (rs.next()) {
                String username = rs.getString("username");
                if (username != null) {
                    players.add(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }
}
