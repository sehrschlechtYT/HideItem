package yt.sehrschlecht.hideitem.commands;

import yt.sehrschlecht.hideitem.data.PlayerState;
import yt.sehrschlecht.hideitem.data.PlayerStateManager;
import yt.sehrschlecht.hideitem.HideItem;
import yt.sehrschlecht.hideitem.util.HidingItem;
import yt.sehrschlecht.hideitem.util.PlayerHiding;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class Commands implements CommandExecutor {
    private final HideItem plugin;

    public Commands (HideItem plugin) {
        this.plugin = plugin;
    }



    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {

        if (command.getName().equalsIgnoreCase("HideItem")) {


            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) return reloadConfig(sender);
                if (args[0].equalsIgnoreCase("info")) return info(sender);
                if (!plugin.getHideItemConfig().DISABLE_COMMANDS()) {
                    if (args.length >= 2 && plugin.getServer().getPlayer(args[1]) != null) {
                        if (args[0].equalsIgnoreCase("hide")) return hideFor(sender, plugin.getServer().getPlayer(args[1]));
                        if (args[0].equalsIgnoreCase("show")) return showFor(sender, plugin.getServer().getPlayer(args[1]));
                        if (args[0].equalsIgnoreCase("toggle")) return toggleFor(sender, plugin.getServer().getPlayer(args[1]));
                    }

                    if (args[0].equalsIgnoreCase("hide")) return hide(sender);
                    if (args[0].equalsIgnoreCase("show")) return show(sender);
                    if (args[0].equalsIgnoreCase("toggle")) return toggle(sender);
                }
            }


        }


        return info(sender);
    }

    private boolean toggle(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7| &fOnly players can use this command!"));
            return true;
        }
        if (!sender.hasPermission("hideitem.toggle") && plugin.getHideItemConfig().REQUIRE_PERMISSION_FOR_COMMANDS()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().NO_PERMISSION_MESSAGE()));
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getCooldowns().isOnCooldown(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().COOLDOWN_MESSAGE().replace("%cooldown%", String.valueOf(plugin.getCooldowns().getCooldown(player.getUniqueId().toString())))));
            return true;
        }

        plugin.getCooldowns().setCooldown(player.getUniqueId().toString());

        final PlayerStateManager playerState = plugin.getPlayerState();
        PlayerState state = playerState.getPlayerState(player);


        if (state == null) state = plugin.getHideItemConfig().DEFAULT_SHOWN() ? PlayerState.SHOWN : PlayerState.HIDDEN;


        if (state.equals(PlayerState.HIDDEN)) {

            new PlayerHiding(plugin).show(player);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().SHOW_MESSAGE()));


            if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveHideItem(player);

            playerState.setPlayerState(player, PlayerState.SHOWN);

        } else if (state.equals(PlayerState.SHOWN)){

            new PlayerHiding(plugin).hide(player);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().HIDE_MESSAGE()));


            if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveShowItem(player);

            playerState.setPlayerState(player, PlayerState.HIDDEN);
        }

        return true;
    }

    private boolean toggleFor(CommandSender sender, final Player player) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7| &fOnly players can use this command!"));
            return true;
        }
        if (!sender.hasPermission("hideitem.toggle.other") && plugin.getHideItemConfig().REQUIRE_PERMISSION_FOR_COMMANDS()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().NO_PERMISSION_MESSAGE()));
            return true;
        }

        final PlayerStateManager playerState = plugin.getPlayerState();
        PlayerState state = playerState.getPlayerState(player);


        if (state == null) state = plugin.getHideItemConfig().DEFAULT_SHOWN() ? PlayerState.SHOWN : PlayerState.HIDDEN;


        if (state.equals(PlayerState.HIDDEN)) {

            new PlayerHiding(plugin).show(player);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().SHOW_MESSAGE()));


            if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveHideItem(player);

            playerState.setPlayerState(player, PlayerState.SHOWN);

        } else if (state.equals(PlayerState.SHOWN)){

            new PlayerHiding(plugin).hide(player);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().HIDE_MESSAGE()));


            if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveShowItem(player);

            playerState.setPlayerState(player, PlayerState.HIDDEN);
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().TOGGLED_FOR_MESSAGE()).replace("%player%", player.getDisplayName()));

        return true;
    }

    private boolean show(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7| &fOnly players can use this command!"));
            return true;
        }
        if (!sender.hasPermission("hideitem.show") && plugin.getHideItemConfig().REQUIRE_PERMISSION_FOR_COMMANDS()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().NO_PERMISSION_MESSAGE()));
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getCooldowns().isOnCooldown(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().COOLDOWN_MESSAGE().replace("%cooldown%", String.valueOf(plugin.getCooldowns().getCooldown(player.getUniqueId().toString())))));
            return true;
        }

        plugin.getCooldowns().setCooldown(player.getUniqueId().toString());

        PlayerStateManager playerState = plugin.getPlayerState();

        new PlayerHiding(plugin).show(player);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().SHOW_MESSAGE()));

        if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveHideItem(player);

        playerState.setPlayerState(player, PlayerState.SHOWN);

        return true;
    }

    private boolean showFor(final CommandSender sender, final Player player) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7| &fOnly players can use this command!"));
            return true;
        }
        if (!sender.hasPermission("hideitem.show.other") && plugin.getHideItemConfig().REQUIRE_PERMISSION_FOR_COMMANDS()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().NO_PERMISSION_MESSAGE()));
            return true;
        }

        PlayerStateManager playerState = plugin.getPlayerState();

        new PlayerHiding(plugin).show(player);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().SHOW_MESSAGE()));

        if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveHideItem(player);

        playerState.setPlayerState(player, PlayerState.SHOWN);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().SHOWN_FOR_MESSAGE()).replace("%player%", player.getDisplayName()));

        return true;
    }

    private boolean hide(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7| &fOnly players can use this command!"));
            return true;
        }
        if (!sender.hasPermission("hideitem.hide") && plugin.getHideItemConfig().REQUIRE_PERMISSION_FOR_COMMANDS()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().NO_PERMISSION_MESSAGE()));
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getCooldowns().isOnCooldown(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().COOLDOWN_MESSAGE().replace("%cooldown%", String.valueOf(plugin.getCooldowns().getCooldown(player.getUniqueId().toString())))));
            return true;
        }

        plugin.getCooldowns().setCooldown(player.getUniqueId().toString());

        PlayerStateManager playerState = plugin.getPlayerState();

        new PlayerHiding(plugin).hide(player);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().HIDE_MESSAGE()));

        if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveShowItem(player);

        playerState.setPlayerState(player, PlayerState.HIDDEN);

        return true;
    }

    private boolean hideFor(final CommandSender sender, final Player player) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7| &fOnly players can use this command!"));
            return true;
        }
        if (!sender.hasPermission("hideitem.hide.other") && plugin.getHideItemConfig().REQUIRE_PERMISSION_FOR_COMMANDS()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().NO_PERMISSION_MESSAGE()));
            return true;
        }

        PlayerStateManager playerState = plugin.getPlayerState();

        new PlayerHiding(plugin).hide(player);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().HIDE_MESSAGE()));

        if (!plugin.getHideItemConfig().DISABLE_ITEMS()) new HidingItem(plugin).giveHideItem(player);

        playerState.setPlayerState(player, PlayerState.HIDDEN);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().HIDDEN_FOR_MESSAGE()).replace("%player%", player.getDisplayName()));

        return true;
    }

    private boolean info (CommandSender sender) {

        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7┃ &fRunning &3HideItem&f v"+plugin.getDescription().getVersion()));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7| &fRunning &3HideItem&f v"+plugin.getDescription().getVersion()));
        }

        return true;
    }

    private boolean reloadConfig(CommandSender sender) {
        if (!sender.hasPermission("hideitem.reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getHideItemConfig().NO_PERMISSION_MESSAGE()));
            return true;
        }

        Date startTime = new Date();
        Date endTime;

        Runnable runnable = () -> {
            plugin.getHideItemConfig().reload();
            plugin.configReloaded();
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            endTime = new Date();
        } catch (InterruptedException e) {
            e.printStackTrace();
            endTime = new Date();
        }

        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7┃ &aSuccessfully reloaded config in %time% ms".replace("%time%", String.valueOf(endTime.getTime() - startTime.getTime()))));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &7| &aSuccessfully reloaded config in %time% ms".replace("%time%", String.valueOf(endTime.getTime() - startTime.getTime()))));
        }

        return true;
    }

}
