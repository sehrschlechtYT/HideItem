package yt.sehrschlecht.hideitem.events;

import yt.sehrschlecht.hideitem.HideItem;
import yt.sehrschlecht.hideitem.data.PlayerState;
import yt.sehrschlecht.hideitem.data.PlayerStateManager;
import yt.sehrschlecht.hideitem.util.HidingItem;
import yt.sehrschlecht.hideitem.util.NBTTags;
import yt.sehrschlecht.hideitem.util.PlayerHiding;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class EventsClass implements Listener {
    private final HideItem plugin;

    public EventsClass(final HideItem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() != null && !event.getHand().equals(EquipmentSlot.HAND)) return;

        if (event.getItem() == null) return;

        ItemStack stack = event.getItem();

        if (!NBTTags.getBoolean(stack, "HIDE_ITEM") && !NBTTags.getBoolean(stack, "SHOW_ITEM")) return;

        event.setCancelled(true);

        if(event.getAction().equals(Action.PHYSICAL)) return;

        if (!player.hasPermission("hideitem.toggle") && plugin.getHideItemConfig().REQUIRE_PERMISSION_FOR_ITEMS()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().NO_PERMISSION_MESSAGE()));
            return;
        }

        if (plugin.getCooldowns().isOnCooldown(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().COOLDOWN_MESSAGE().replace("%cooldown%", String.valueOf(plugin.getCooldowns().getCooldown(player.getUniqueId().toString())))));
            return;
        }

        plugin.getCooldowns().setCooldown(player.getUniqueId().toString());


        final PlayerStateManager playerState = plugin.getPlayerState();
        PlayerState state = playerState.getPlayerState(player);


        if (state == null) playerState.setPlayerState(player, plugin.getHideItemConfig().DEFAULT_SHOWN() ? PlayerState.SHOWN : PlayerState.HIDDEN);
        if (state == null) state = plugin.getHideItemConfig().DEFAULT_SHOWN() ? PlayerState.SHOWN : PlayerState.HIDDEN;


        if (state.equals(PlayerState.HIDDEN)) {

            new PlayerHiding(plugin).show(player);


            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().SHOW_MESSAGE()));


            if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveHideItem(player);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> playerState.setPlayerState(player, PlayerState.SHOWN));

        } else if (state.equals(PlayerState.SHOWN)) {
            new PlayerHiding(plugin).hide(player);

            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().HIDE_MESSAGE()));

            if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveShowItem(player);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> playerState.setPlayerState(player, PlayerState.HIDDEN));
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteractEntity(final PlayerInteractEntityEvent event){
        ItemStack stack  = event.getPlayer().getInventory().getItemInMainHand();

        if (NBTTags.getBoolean(stack, "SHOW_ITEM") || NBTTags.getBoolean(stack, "HIDE_ITEM")) event.setCancelled(true);
    }


    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final PlayerState state = plugin.getPlayerState().getPlayerState(player);

        boolean hasHiddenPlayers;

        if (Arrays.asList(PlayerState.values()).contains(state)) hasHiddenPlayers = state.equals(PlayerState.HIDDEN); else hasHiddenPlayers = !plugin.getHideItemConfig().DEFAULT_SHOWN();

        final ItemStack hideItem = hasHiddenPlayers ? plugin.getHideItemConfig().SHOW_ITEM() : plugin.getHideItemConfig().HIDE_ITEM();

        // Hide Players
        if (hasHiddenPlayers) {
            new PlayerHiding(plugin).hide(player);
        }

        if (plugin.getHideItemConfig().DISABLE_ITEMS()) return;

        final ItemMeta iMeta = hideItem.getItemMeta();

        String name = iMeta.getDisplayName();

        if (plugin.getPAPI()) name = PlaceholderAPI.setPlaceholders(player, name);

        iMeta.setDisplayName(name.replace("&", "§"));

        hideItem.setItemMeta(iMeta);

        // Give hide / show item to correct slot
        if (plugin.getHideItemConfig().FIRST_FREE_SLOT()) {
            for (ItemStack i : player.getInventory().getContents()) {
                if (i == null) continue;
                if (NBTTags.getBoolean(i, "HIDE_ITEM") || NBTTags.getBoolean(i, "SHOW_ITEM")) return;
            }

            player.getInventory().addItem(hideItem);

        } else {

            player.getInventory().setItem(plugin.getHideItemConfig().ITEM_SLOT() - 1, hideItem);

        }
    }

    @EventHandler
    public void onJoinHide(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        for (final Player p : plugin.getServer().getOnlinePlayers()) {
            final PlayerState state = plugin.getPlayerState().getPlayerState(p);

            if (state == null) continue;

            if(state.equals(PlayerState.HIDDEN)) {
                new PlayerHiding(plugin).hideSinglePlayer(p, player);
            } else if(state.equals(PlayerState.SHOWN)) {
                new PlayerHiding(plugin).showSinglePlayer(p, player);
            }
        }

    }


    @EventHandler (ignoreCancelled = true)
    public void onDrop(final PlayerDropItemEvent event) {
        if (plugin.getHideItemConfig().DISABLE_ITEMS()) return;
        if (!plugin.getHideItemConfig().FIXED_ITEM()) return;

        if (event.getItemDrop() == null) return;

        if (event.getItemDrop().getItemStack() == null) return;

        final ItemStack i = event.getItemDrop().getItemStack();

        event.setCancelled(NBTTags.getBoolean(i, "HIDE_ITEM") || NBTTags.getBoolean(i, "SHOW_ITEM"));

    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        if (plugin.getHideItemConfig().DISABLE_ITEMS()) return;

        if (event.getDrops() == null) return;

        for (final ItemStack i : event.getEntity().getInventory().getContents()) {

            if (i == null || !i.hasItemMeta()) continue;

            if (!NBTTags.getBoolean(i, "HIDE_ITEM") && !NBTTags.getBoolean(i, "SHOW_ITEM")) continue;

            event.getDrops().remove(i);
        }
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        if (plugin.getHideItemConfig().DISABLE_ITEMS()) return;

        final Player player = event.getPlayer();

        final PlayerState state = plugin.getPlayerState().getPlayerState(player);

        boolean hasHiddenPlayers;

        if (Arrays.asList(PlayerState.values()).contains(state)) hasHiddenPlayers = state.equals(PlayerState.HIDDEN); else hasHiddenPlayers = !plugin.getHideItemConfig().DEFAULT_SHOWN();

        final ItemStack hideItem = hasHiddenPlayers ? plugin.getHideItemConfig().SHOW_ITEM() : plugin.getHideItemConfig().HIDE_ITEM();

        final ItemMeta iMeta = hideItem.getItemMeta();

        String name = iMeta.getDisplayName();

        if (plugin.getPAPI()) name = PlaceholderAPI.setPlaceholders(player, name);

        iMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        hideItem.setItemMeta(iMeta);

        if (plugin.getHideItemConfig().FIRST_FREE_SLOT()) {
            player.getInventory().addItem(hideItem);
        } else {
            player.getInventory().setItem(plugin.getHideItemConfig().ITEM_SLOT() - 1, hideItem);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onClick(final InventoryClickEvent event) {
        if (plugin.getHideItemConfig().DISABLE_ITEMS()) return;
        if (!plugin.getHideItemConfig().FIXED_ITEM()) return;

        if (event.getCurrentItem() != null) {
            if (NBTTags.getBoolean(event.getCurrentItem(), "SHOW_ITEM") || NBTTags.getBoolean(event.getCurrentItem(), "HIDE_ITEM")) {
                event.setCancelled(true);
            }
        }

        if (event.getCursor() != null) {
            if (NBTTags.getBoolean(event.getCursor(), "SHOW_ITEM") || NBTTags.getBoolean(event.getCursor(), "HIDE_ITEM")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onInventoryMove(final InventoryMoveItemEvent event) {
        if(plugin.getHideItemConfig().DISABLE_ITEMS()) return;
        if(!plugin.getHideItemConfig().FIXED_ITEM()) return;

        if(NBTTags.getBoolean(event.getItem(), "SHOW_ITEM") || NBTTags.getBoolean(event.getItem(), "HIDE_ITEM")) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (plugin.getHideItemConfig().DISABLE_COMMANDS()) return;
        if (!plugin.getHideItemConfig().USE_ALIASES()) return;

        if (event.getMessage().equalsIgnoreCase(plugin.getHideItemConfig().HIDE_ALIAS())) event.setMessage("/hideitem hide");
        if (event.getMessage().equalsIgnoreCase(plugin.getHideItemConfig().SHOW_ALIAS())) event.setMessage("/hideitem show");
        if (event.getMessage().equalsIgnoreCase(plugin.getHideItemConfig().TOGGLE_ALIAS())) event.setMessage("/hideitem toggle");
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTab(final TabCompleteEvent event) {
        final String buffer = event.getBuffer().toLowerCase();

        final List<String> completions = event.getCompletions();

        if (plugin.getHideItemConfig().TOGGLE_ALIAS().startsWith(buffer)) completions.add(plugin.getHideItemConfig().TOGGLE_ALIAS());
        if (plugin.getHideItemConfig().HIDE_ALIAS().startsWith(buffer)) completions.add(plugin.getHideItemConfig().HIDE_ALIAS());
        if (plugin.getHideItemConfig().SHOW_ALIAS().startsWith(buffer)) completions.add(plugin.getHideItemConfig().SHOW_ALIAS());

        event.setCompletions(completions);
    }
}
