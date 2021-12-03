package com.vomarek.hideitem.data;

import com.vomarek.hideitem.HideItem;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class PlayerState {
    private HideItem plugin;
    private String STORAGE_TYPE;

    private HashMap<String, String> playerStates;

    public PlayerState(HideItem plugin) {
        this.plugin = plugin;
        this.STORAGE_TYPE = plugin.getHideItemConfig().STORAGE_METHOD();

        playerStates = new HashMap<>();
    }

    public PlayerState setPlayerState(Player player, String state) {
        playerStates.put(player.getName(), state);

        switch (STORAGE_TYPE.toLowerCase(Locale.ENGLISH)) {
            case "none":
                return this;
            case "file":
                if (plugin.getDataFile() == null) return this;

                try {
                    plugin.getDataFile().set(player.getName(), state);
                    plugin.getDataFile().save(new File(plugin.getDataFolder(),"data.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "mysql":
            case "sqlite":
                if (plugin.getHideItemConfig().DATABASE() == null) return this;
                plugin.getHideItemConfig().DATABASE().setState(player.getUniqueId().toString(), state);
                break;
        }

        return this;
    }

    public String getPlayerState(Player player) {
        if (playerStates.containsKey(player.getName())) return playerStates.get(player.getName());

        String state;
        switch (STORAGE_TYPE.toLowerCase(Locale.ENGLISH)) {
            case "none":
                return null;
            case "file":
                if (plugin.getDataFile() == null) return null;

                state = plugin.getDataFile().getString(player.getName(), "");

                if (state.equals("")) return null;

                return state;
            case "mysql":
            case "sqlite":
                if (plugin.getHideItemConfig().DATABASE() == null) return null;

                state = plugin.getHideItemConfig().DATABASE().getState(player.getUniqueId().toString());

                if (state == null) return null;

                return state;
        }

        return null;
    }
}
