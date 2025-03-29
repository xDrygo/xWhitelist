package org.eldrygo.Managers;

import org.bukkit.command.CommandSender;
import org.eldrygo.Utils.ChatUtils;

import java.sql.*;

public class MySQLWhitelistManager {
    private final ChatUtils chatUtils;

    public MySQLWhitelistManager(ChatUtils chatUtils) {
        this.chatUtils = chatUtils;
    }

    public void addPlayerToWhitelistMySQL(Connection connection, String playerName, CommandSender sender) {
        try {
            // Check if the player is already on the whitelist
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.already").replace("%player%", playerName));
                    return;
                }
            }

            String insertQuery = "INSERT INTO whitelist (username) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
            }

        } catch (SQLException e) {
            sender.sendMessage(chatUtils.getMessage("error.database_exception"));
        }
    }

    public void removePlayerFromWhitelistMySQL(Connection connection, String playerName, CommandSender sender) {
        try {
            // Check if player is in the whitelist
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
                    return;
                }
            }

            // If the player exists, remove it
            String deleteQuery = "DELETE FROM whitelist WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
            }

        } catch (SQLException e) {
            sender.sendMessage(chatUtils.getMessage("error.database_exception"));
        }
    }

    public void listWhitelistMySQL(Connection connection, CommandSender sender) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM whitelist")) {

            // Comprobar si la consulta está vacía
            boolean hasPlayers = false;
            String rowFormat = chatUtils.getMessage("commands.whitelist.list.row");
            while (rs.next()) {
                hasPlayers = true;
                String player = rs.getString("username");
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.header"));
                sender.sendMessage(rowFormat.replace("%player%", player));
            }

            if (!hasPlayers) {
                sender.sendMessage(chatUtils.getMessage("commands.whitelist.list.empty"));
            }

        } catch (SQLException e) {
            sender.sendMessage(chatUtils.getMessage("error.database_exception"));
        }
    }


    public void cleanupWhitelistMySQL(Connection connection, CommandSender sender) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM whitelist");
            sender.sendMessage(chatUtils.getMessage("commands.whitelist.cleanup.success"));
        } catch (SQLException e) {
            sender.sendMessage(chatUtils.getMessage("error.database_exception"));
        }
    }
}
