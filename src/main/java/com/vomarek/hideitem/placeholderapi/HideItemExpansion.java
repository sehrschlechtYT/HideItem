package com.vomarek.hideitem.placeholderapi;

import com.vomarek.hideitem.HideItem;
import com.vomarek.hideitem.data.PlayerState;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class HideItemExpansion extends PlaceholderExpansion {
    private final HideItem plugin;

    public HideItemExpansion(HideItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "hideitem";
    }

    @Override
    public @NotNull String getAuthor() {
        return "sehrschlechtYT";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equalsIgnoreCase("state")) {
            if(player == null) return "";
            PlayerState state = plugin.getPlayerState().getPlayerState(player);
            return state == null ? "" : state.getId();
        }
        if(params.startsWith("state_")) {
            if(player == null) return "";
            String[] args = params.split("_");
            if(args.length == 2) {
                OfflinePlayer target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);
                if(target == null) return "";
                PlayerState state = plugin.getPlayerState().getPlayerState(player);
                return state == null ? "" : state.getId();
            }
        }
        return null;
    }
}
