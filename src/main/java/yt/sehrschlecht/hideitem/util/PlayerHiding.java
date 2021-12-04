package yt.sehrschlecht.hideitem.util;

import yt.sehrschlecht.hideitem.HideItem;
import yt.sehrschlecht.hideitem.data.PlayersHidden;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class PlayerHiding {
    private HideItem plugin;

    public PlayerHiding(final HideItem plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public void hideSinglePlayer(final Player player, final Player target) {
        PlayersHidden.add();
        player.hidePlayer(plugin, target);
    }

    @SuppressWarnings("deprecation")
    public void showSinglePlayer(final Player player, final Player target) {
        if (isVanished(target) && !player.hasPermission("hideitem.seevanished")) return;
        player.showPlayer(plugin, target);
    }

    @SuppressWarnings("deprecation")
    public void hide(final Player player) {
        for (final Player currentPlayer : plugin.getServer().getOnlinePlayers()) {
            PlayersHidden.add();
            player.hidePlayer(plugin, currentPlayer);
        }
    }

    @SuppressWarnings("deprecation")
    public void show(final Player player) {
        for (final Player currentPlayer : plugin.getServer().getOnlinePlayers()) {
            if (isVanished(currentPlayer) && !player.hasPermission("hideitem.seevanished")) continue;
            player.showPlayer(plugin, currentPlayer);
        }
    }

    private boolean isVanished(final Player player) {
        for (final MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
