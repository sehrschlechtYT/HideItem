package com.vomarek.hideitem.data.database;


import com.vomarek.hideitem.data.PlayerState;

import java.util.UUID;

public interface Database {

    void setState(UUID uuid, PlayerState state);

    PlayerState getState(UUID uuid);

    void createConnection();

    void close();
}
