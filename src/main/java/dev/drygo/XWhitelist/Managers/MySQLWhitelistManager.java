package dev.drygo.XWhitelist.Managers;

import org.bukkit.command.CommandSender;
import dev.drygo.XWhitelist.Utils.ChatUtils;

import java.sql.*;

public class MySQLWhitelistManager {
    public static void addPlayerToWhitelistMySQL(Connection connection, String playerName, CommandSender sender) {
        try {
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    sender.sendMessage(ChatUtils.getMessage("commands.whitelist.add.already").replace("%player%", playerName));
                    return;
                }
            }

            String insertQuery = "INSERT INTO whitelist (username) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.add.success").replace("%player%", playerName));
            }

        } catch (SQLException e) {
            sender.sendMessage(ChatUtils.getMessage("error.database_exception"));
        }
    }

    public static void removePlayerFromWhitelistMySQL(Connection connection, String playerName, CommandSender sender) {
        try {
            String checkQuery = "SELECT COUNT(*) FROM whitelist WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, playerName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    sender.sendMessage(ChatUtils.getMessage("commands.whitelist.remove.already").replace("%player%", playerName));
                    return;
                }
            }

            String deleteQuery = "DELETE FROM whitelist WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                stmt.setString(1, playerName);
                stmt.executeUpdate();
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.remove.success").replace("%player%", playerName));
            }

        } catch (SQLException e) {
            sender.sendMessage(ChatUtils.getMessage("error.database_exception"));
        }
    }

    public static void listWhitelistMySQL(Connection connection, CommandSender sender) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM whitelist")) {

            sender.sendMessage(ChatUtils.getMessage("commands.whitelist.list.header"));
            boolean hasPlayers = false;
            String rowFormat = ChatUtils.getMessage("commands.whitelist.list.row");
            while (rs.next()) {
                hasPlayers = true;
                String player = rs.getString("username");
                sender.sendMessage(rowFormat.replace("%player%", player));
            }

            if (!hasPlayers) {
                sender.sendMessage(ChatUtils.getMessage("commands.whitelist.list.empty"));
            }

        } catch (SQLException e) {
            sender.sendMessage(ChatUtils.getMessage("error.database_exception"));
        }
    }


    public static void cleanupWhitelistMySQL(Connection connection, CommandSender sender) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM whitelist");
            sender.sendMessage(ChatUtils.getMessage("commands.whitelist.cleanup.success"));
        } catch (SQLException e) {
            sender.sendMessage(ChatUtils.getMessage("error.database_exception"));
        }
    }
}
