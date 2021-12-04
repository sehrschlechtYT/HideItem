package com.vomarek.hideitem;

import com.vomarek.hideitem.data.PlayerState;
import com.vomarek.hideitem.util.PlayerHiding;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HideItemAPI {
    private final static HideItem plugin = HideItem.getPlugin();

    /**
     * Using this method you can set visibility of other players for player.<br>
     * Player will see vanished players if has hideitem.seevanished permission.<br>
     * No message is sent to player!
     *
     * @param player who to set visibility of others to
     * @param hidden should player have hidden players?
     */

    public static void setHiddenState(final Player player, final Boolean hidden) {
        if (hidden) {
            new PlayerHiding(plugin).hide(player);
            plugin.getHideItemStack().updateItems(player);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPlayerState().setPlayerState(player, PlayerState.HIDDEN));
        } else {
            new PlayerHiding(plugin).show(player);
            plugin.getHideItemStack().updateItems(player);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPlayerState().setPlayerState(player, PlayerState.SHOWN));
        }
    }

    /**
     * Using this method you can hide players for specific player.<br>
     * No message is sent to player!
     *
     * @param player Player who you want to hide others to
     */
    public static void hideFor(final Player player) {
        new PlayerHiding(plugin).hide(player);
        plugin.getHideItemStack().updateItems(player);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPlayerState().setPlayerState(player, PlayerState.HIDDEN));
    }

    /**
     * Using this method you can show players for specific player.<br>
     * Player will see vanished players if has hideitem.seevanished permission.<br>
     * No message is sent to player!
     *
     * @param player Player who you want to show others to
     */
    public static void showFor(final Player player) {
        new PlayerHiding(plugin).show(player);
        plugin.getHideItemStack().updateItems(player);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPlayerState().setPlayerState(player, PlayerState.SHOWN));
    }

    /**
     * With this method you can remove all hide/show items from players inventory
     *
     * @param player Player who you want to remove hide/show items from
     */
    public static void removeItems(final Player player) {
        for (int i = 0; i < 27; i++) {
            final ItemStack item = player.getInventory().getItem(i);

            if (item == null) continue;
            if (!plugin.getHideItemStack().isHideItem(item)) continue;

            player.getInventory().removeItem(item);
        }

        player.updateInventory();
    }

}
