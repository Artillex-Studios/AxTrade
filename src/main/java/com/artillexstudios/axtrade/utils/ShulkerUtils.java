package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axtrade.hooks.HookManager;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

public class ShulkerUtils {

    @NotNull
    public static ItemStack[] getShulkerContents(ItemStack item, boolean includeSelf) {
        if (item == null) return new ItemStack[0];
        if (HookManager.getAxShulkersHook() != null) {
            ItemStack[] items = HookManager.getAxShulkersHook().getItems(item);
            if (items != null) return items;
        }
        if (!(item.getItemMeta() instanceof BlockStateMeta meta)) return new ItemStack[]{item};
        if (!(meta.getBlockState() instanceof Container container)) return new ItemStack[]{item};
        ItemStack[] storage = container.getInventory().getStorageContents();
        if (!includeSelf) return storage;

        ItemStack[] items = new ItemStack[storage.length + 1];
        System.arraycopy(storage, 0, items, 0, storage.length);
        items[storage.length - 1] = item;
        return items;
    }
}
