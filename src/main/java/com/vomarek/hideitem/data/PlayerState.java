package com.vomarek.hideitem.data;

public enum PlayerState {
    SHOWN("shown"),
    HIDDEN("hidden");

    private final String id;

    PlayerState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
