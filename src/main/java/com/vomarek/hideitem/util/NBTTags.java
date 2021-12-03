package com.vomarek.hideitem.util;

import com.vomarek.hideitem.HideItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class NBTTags {
    private static NamespacedKey key(String tag) {
        return new NamespacedKey(HideItem.getPlugin(), tag);
    }

    public static boolean getBoolean(ItemStack stack, String tag) {
        if(stack.getItemMeta() == null) return false;
        PersistentDataContainer dataContainer = stack.getItemMeta().getPersistentDataContainer();
        return dataContainer.has(key(tag), PersistentDataType.BYTE)
                && (dataContainer.get(key(tag), PersistentDataType.BYTE) == 1);
    }

    public static ItemStack setBoolean(ItemStack stack, String tag, boolean bool) {
        ItemMeta meta = stack.getItemMeta();
        if(meta == null) return stack;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(key(tag), PersistentDataType.BYTE, (byte) (bool ? 1 : 0));
        stack.setItemMeta(meta);
        return stack;
    }
}
