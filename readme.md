# HideItem
**by sehrschlechtYT**    
HideItem is spigot plugin used to give players the option to toggle the visibility of other players.  
Documentation [link](https://docs.vomarek.com/hideitem/hideitem)  
This plugin was originally created by **qKing12** [Link](https://www.spigotmc.org/resources/hideitem-hide-players-1-8-1-15.70313/) and by 1vomarek1 [Link](https://www.spigotmc.org/resources/hideitem-abandoned-1-8-1-15.80167/)

### Difference between this plugin and the old plugin
* The code is cleaner
* Some bugs were fixed and the dependencies updated
* A expansion for PlaceholderAPI was added
* Support for MongoDB was added
* All reflection code has been removed to make the plugin compatible with new spigot versions without updating it
* The minimum server version was changed to 1.14.4
* Support for custom heads was added

## API

### Import with maven
To import HideItem, you currently have to build it with maven (`maven clean install`).
The API is now in your local maven repository.
You can use it without adding any repositories.

### Methods

```
package yt.sehrschlecht.hideitem;

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
```

## Support
If you need help with the plugin, you can join my [discord support server](https://discord.gg/KsRHqkMn4H)
