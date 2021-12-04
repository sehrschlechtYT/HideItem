package yt.sehrschlecht.hideitem.util;

import yt.sehrschlecht.hideitem.HideItem;
import yt.sehrschlecht.hideitem.data.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HideItemStack {
    private final HideItem plugin;

    public HideItemStack(HideItem plugin) {
        this.plugin = plugin;
    }

    /**
     * Determines wether imputed item is custom item created by HideItem plugin
     *
     * @param item Item to check wether it is custom item
     * @return boolean based on imputed item
     */
    public boolean isHideItem(final ItemStack item) {
        return NBTTags.getBoolean(item, "HIDE_ITEM") || NBTTags.getBoolean(item, "SHOW_ITEM");
    }

    /**
     * This method is used to update all hide/show items in players inventory
     *
     * @param player who's inventory do you want to update items in
     */
    public void updateItems(final Player player) {

        PlayerState state = plugin.getPlayerState().getPlayerState(player);

        if (state == null) plugin.getPlayerState().setPlayerState(player, plugin.getHideItemConfig().DEFAULT_SHOWN() ? PlayerState.SHOWN : PlayerState.HIDDEN);
        if (state == null) state = plugin.getHideItemConfig().DEFAULT_SHOWN() ? PlayerState.SHOWN : PlayerState.HIDDEN;

        final boolean hidden = state.equals("hidden");

        for (int i = 0; i < 27; i++) {
            final ItemStack item = player.getInventory().getItem(i);

            if (item == null) continue;
            if (hidden ? !NBTTags.getBoolean(item, "HIDE_ITEM") : !NBTTags.getBoolean(item, "SHOW_ITEM")) continue;

            player.getInventory().setItem(i, hidden ? plugin.getHideItemConfig().SHOW_ITEM() : plugin.getHideItemConfig().HIDE_ITEM());
        }

        player.updateInventory();
    }
}
