package yt.sehrschlecht.hideitem.data;

import yt.sehrschlecht.hideitem.HideItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class PlayerStateManager {
    private HideItem plugin;
    private String STORAGE_TYPE;

    private HashMap<String, PlayerState> playerStates;

    public PlayerStateManager(HideItem plugin) {
        this.plugin = plugin;
        this.STORAGE_TYPE = plugin.getHideItemConfig().STORAGE_METHOD();

        playerStates = new HashMap<>();
    }

    public PlayerStateManager setPlayerState(Player player, PlayerState state) {
        playerStates.put(player.getName(), state);

        switch (STORAGE_TYPE.toLowerCase(Locale.ENGLISH)) {
            case "none":
                return this;
            case "file":
                if (plugin.getDataFile() == null) return this;

                try {
                    plugin.getDataFile().set(player.getName(), state.toString());
                    plugin.getDataFile().save(new File(plugin.getDataFolder(),"data.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "mysql":
            case "sqlite":
                if (plugin.getHideItemConfig().DATABASE() == null) return this;
                plugin.getHideItemConfig().DATABASE().setState(player.getUniqueId(), state);
                break;
        }

        return this;
    }

    public PlayerState getPlayerState(OfflinePlayer player) {
        if(player.getName() == null) return null;
        if (playerStates.containsKey(player.getName())) return playerStates.get(player.getName());

        PlayerState state;
        switch (STORAGE_TYPE.toLowerCase(Locale.ENGLISH)) {
            case "none":
                return null;
            case "file":
                if (plugin.getDataFile() == null) return null;

                if(plugin.getDataFile().getString(player.getName()) == null) return null;

                state = PlayerState.valueOf(plugin.getDataFile().getString(player.getName(), ""));

                return state;
            case "mysql":
            case "sqlite":
                if (plugin.getHideItemConfig().DATABASE() == null) return null;

                state = plugin.getHideItemConfig().DATABASE().getState(player.getUniqueId());

                if (state == null) return null;

                return state;
        }

        return null;
    }
}
