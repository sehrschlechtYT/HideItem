package com.vomarek.hideitem.data;

public class PlayersHidden {
    private static Integer count;

    public static void add() {
        count++;
    }

    public static Integer getCount() {
        return count;
    }
}
