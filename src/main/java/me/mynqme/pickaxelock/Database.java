package me.mynqme.pickaxelock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import java.sql.*;

public class Database {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            String dbUrl = "jdbc:mysql://" + Config.databaseHost + "/" + Config.databaseName + "?autoReconnect=true&useUnicode=yes";
            if (Config.debug) {
                PickaxeLock.console("&7 \u2022&f Using Database URL: " + dbUrl);
                PickaxeLock.console("&7 \u2022&f Using Database User: " + Config.databaseUser);
                PickaxeLock.console("&7 \u2022&f Using Database Password: " + Config.databasePassword);
            }
            Database.connection = DriverManager.getConnection(dbUrl, Config.databaseUser, Config.databasePassword);
            if (Config.debug) {
                PickaxeLock.console("&7 \u2022&f Connected to the MySQL database.");
            }
        } catch (SQLException e) {
            PickaxeLock.console("&7 \u2022&c Failed to connect to the MySQL database.");
            PickaxeLock.console("&7 \u2022&c Error: " + e.getMessage());
        }
        return Database.connection;
    }

    public static void init() throws SQLException {
        Database.connection = getConnection();
        setup();
    }

    public static void setup() throws SQLException {
        Statement statement = Database.connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS pickaxelock (id int NOT NULL AUTO_INCREMENT, uuid VARCHAR(36), slot INT, PRIMARY KEY (id))");
    }

    public static void close() throws SQLException {
        if (Database.connection != null) {
            Database.connection.close();
        }
    }

    public static void createPlayer(UUID uuid) throws SQLException {
        // Do a SELECT * FROM minelevels WHERE uuid = ? to check if the player exists
        PreparedStatement statement = Database.connection.prepareStatement("SELECT * FROM `pickaxelock` WHERE uuid = ?");
        statement.setString(1, uuid.toString());
        ResultSet results = statement.executeQuery();

        // check if results are present or not
        if (!results.next()) {
            PreparedStatement stmt = Database.connection.prepareStatement("INSERT INTO `pickaxelock` (uuid, slot) VALUES (?,?)");
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, 0);
            stmt.executeUpdate();
        }
    }

    public static void getPlayer(UUID uuid) throws SQLException {
        PreparedStatement statement = Database.connection.prepareStatement("SELECT * FROM `pickaxelock` WHERE uuid = ? ORDER BY `id` DESC LIMIT 1");
        statement.setString(1, uuid.toString());
        ResultSet results = statement.executeQuery();

        if (results.next()) {
            PickaxeLock.PickaxeData.put(uuid, results.getInt("slot"));
            if (Config.debug) {
                PickaxeLock.console(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName() + " has his pickaxe in slot: " + PickaxeLock.PickaxeData.get(uuid));
            }
        } else {
            Database.createPlayer(uuid);
            PickaxeLock.PickaxeData.put(uuid, 0);
        }
    }

    public static void savePlayer(UUID uuid, int slot){
        try {
            PreparedStatement statement = Database.connection.prepareStatement("UPDATE `pickaxelock` SET slot = ? WHERE uuid = ?");
            statement.setInt(1, slot);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PickaxeLock.PickaxeData.remove(uuid); // don't need this in this array anymore as player is not online.
    }

}
