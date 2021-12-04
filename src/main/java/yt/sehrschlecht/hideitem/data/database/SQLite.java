package yt.sehrschlecht.hideitem.data.database;

import yt.sehrschlecht.hideitem.HideItem;
import yt.sehrschlecht.hideitem.data.PlayerState;

import java.sql.*;
import java.util.UUID;

public class SQLite implements Database {
    private HideItem plugin;

    private Connection connection;

    public SQLite(final HideItem plugin) {
        this.plugin = plugin;

        createConnection();
    }

    @Override
    public void createConnection() {
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
    public void setState(UUID uuid, PlayerState state) {
        try {
            if (connection.isClosed()) createConnection();

            final PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO HideItem (player, state) VALUES (?, ?)");

            statement.setString(1, uuid.toString());
            statement.setString(2, state.getId());

            statement.execute();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerState getState(UUID uuid) {
        try {
            if (connection.isClosed()) createConnection();

            final PreparedStatement statement = connection.prepareStatement("SELECT * FROM HideItem WHERE player=?");
            statement.setString(1, uuid.toString());

            if (!statement.execute()) return null;

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                if (results.getString("state") == null) continue;

                return PlayerState.valueOf(results.getString("state"));
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (!connection.isClosed()) connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
