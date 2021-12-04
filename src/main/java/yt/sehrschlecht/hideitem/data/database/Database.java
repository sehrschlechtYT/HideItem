package yt.sehrschlecht.hideitem.data.database;


import yt.sehrschlecht.hideitem.data.PlayerState;

import java.util.UUID;

public interface Database {

    void setState(UUID uuid, PlayerState state);

    PlayerState getState(UUID uuid);

    void createConnection();

    void close();
}
