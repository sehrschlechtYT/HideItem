package yt.sehrschlecht.hideitem.data.database;

import yt.sehrschlecht.hideitem.data.PlayerState;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.*;
import java.util.UUID;

public class MySQL implements Database {
    private Connection conn;

    private final String HOST;
    private final Integer PORT;
    private final String USER;
    private final String PASSWORD;
    private final String DATABASE;
    private final String TABLE;

    public MySQL(final YamlConfiguration config) {

        HOST = config.getString("mysql.host", "localhost");
        PORT = config.getInt("mysql.port", 3306);
        DATABASE = config.getString("mysql.database", "HideItem");
        TABLE = config.getString("mysql.table", "HideItem");
        USER = config.getString("mysql.user", "root");
        PASSWORD = config.getString("mysql.password", "");


        createConnection();
    }

    @Override
    public void createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://"+HOST+":"+PORT+"/?useUnicode=true&characterEncoding=utf8&useSSL=false&verifyServerCertificate=false", USER, PASSWORD);

            final Statement stmt = conn.createStatement();

            String sql = "CREATE DATABASE IF NOT EXISTS "+DATABASE;
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS "+DATABASE+"."+TABLE+" (player VARCHAR(36) NOT NULL, state VARCHAR(16) , PRIMARY KEY ( player ))";
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setState(UUID uuid, PlayerState state) {
        try {
            if (conn.isClosed()) createConnection();

            final PreparedStatement stmt = conn.prepareStatement("INSERT INTO "+DATABASE+"."+TABLE+" (player, state) VALUES (?, ?) ON DUPLICATE KEY UPDATE state=?");
            stmt.setString(1, uuid.toString());
            stmt.setString(2, state.getId());
            stmt.setString(3, state.getId());

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerState getState(UUID uuid) {
        try {
            if (conn.isClosed()) createConnection();

            final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM "+DATABASE+"."+TABLE+" WHERE player=?");
            stmt.setString(1, uuid.toString());

            if (!stmt.execute()) return null;

            ResultSet results = stmt.getResultSet();

            while (results.next()) {
                if (results.getString("state") == null) continue;

                return PlayerState.valueOf(results.getString("state"));
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (!conn.isClosed()) conn.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
