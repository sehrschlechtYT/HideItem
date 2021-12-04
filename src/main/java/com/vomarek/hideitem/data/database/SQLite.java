package com.vomarek.hideitem.data.database;

import com.vomarek.hideitem.HideItem;

import java.sql.*;

public class SQLite implements Database {
    private HideItem plugin;

    private Connection connection;

    public SQLite (final HideItem plugin) {
        this.plugin = plugin;

        createConnection();
    }

    private void createConnection () {
        try {

            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/data.db");

            final Statement stmt = connection.createStatement();

            final String sql = "CREATE TABLE IF NOT EXISTS HideItem (player VARCHAR(36) NOT NULL, state VARCHAR(16) , PRIMARY KEY ( player ))";
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setState(String uuid, String state) {
        try {
            if (connection.isClosed()) createConnection();

            final PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO HideItem (player, state) VALUES (?, ?)");

            statement.setString(1, uuid);
            statement.setString(2, state);

            statement.execute();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getState(String uuid) {
        try {
            if (connection.isClosed()) createConnection();

            final PreparedStatement statement = connection.prepareStatement("SELECT * FROM HideItem WHERE player=?");
            statement.setString(1, uuid);

            if (!statement.execute()) return null;

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                if (results.getString("state") == null) continue;

                return results.getString("state");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close () {
        try {
            if (!connection.isClosed()) connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
