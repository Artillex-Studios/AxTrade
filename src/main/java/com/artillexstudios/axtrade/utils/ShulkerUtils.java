package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axtrade.hooks.HookManager;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

public class ShulkerUtils {

    @NotNull
    public static ItemStack[] getShulkerContents(ItemStack item) {
        if (item == null) return new ItemStack[0];
        if (HookManager.getAxShulkersHook() != null) {
            ItemStack[] items = HookManager.getAxShulkersHook().getItems(item);
            if (items != null) return items;
        }
        if (!(item.getItemMeta() instanceof BlockStateMeta meta)) return new ItemStack[]{item};
        if (!(meta.getBlockState() instanceof Container container)) return new ItemStack[]{item};
        return container.getInventory().getStorageContents();
    }
}
