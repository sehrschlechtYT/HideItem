package yt.sehrschlecht.hideitem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import yt.sehrschlecht.hideitem.HideItem;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabComplete implements TabCompleter {
    private HideItem plugin;

    public TabComplete(final HideItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length >= 1) {
            if (command.getName().equalsIgnoreCase("hideitem")) {
                if (sender.hasPermission("hideitem.reload")) {
                    if (!plugin.getHideItemConfig().DISABLE_COMMANDS())
                        return Stream.of("info", "reload", "toggle", "show", "hide").filter(s -> s.startsWith(args[0].toLowerCase(Locale.ENGLISH))).collect(Collectors.toList());
                    return Stream.of("info", "reload").filter(s -> s.startsWith(args[0].toLowerCase(Locale.ENGLISH))).collect(Collectors.toList());
                } else {
                    if (!plugin.getHideItemConfig().DISABLE_COMMANDS())
                        return Stream.of("info", "toggle", "show", "hide").filter(s -> s.startsWith(args[0].toLowerCase(Locale.ENGLISH))).collect(Collectors.toList());;
                    return Stream.of("info").filter(s -> s.startsWith(args[0].toLowerCase(Locale.ENGLISH))).collect(Collectors.toList());
                }
            }
        }

        return null;
    }
}
