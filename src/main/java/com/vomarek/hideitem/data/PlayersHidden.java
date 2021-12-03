package com.vomarek.hideitem.data;

public class PlayersHidden {
    private static Integer count = 0;

    public static void add() {
        count++;
    }

    public static Integer getCount() {
        return count;
    }
}
