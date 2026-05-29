package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axtrade.hooks.HookManager;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShulkerUtils {

    public static List<ItemStack> getStorageContents(ItemStack item, boolean includeSelf) {
        if (item.getType().name().endsWith("BUNDLE")) return getBundleContents(item, includeSelf);
        return getShulkerContents(item, includeSelf);
    }

    @NotNull
    private static List<ItemStack> getShulkerContents(ItemStack item, boolean includeSelf) {
        if (item == null) return new ArrayList<>();
        if (HookManager.getAxShulkersHook() != null) {
            ItemStack[] items = HookManager.getAxShulkersHook().getItems(item);
            if (items != null) {
                List<ItemStack> storage = new ArrayList<>(Arrays.asList(items));
                if (includeSelf) storage.add(item);
                return storage;
            }
        }
        if (!(item.getItemMeta() instanceof BlockStateMeta meta)) return List.of(item);
        if (!(meta.getBlockState() instanceof Container container)) return List.of(item);
        List<ItemStack> storage = new ArrayList<>(Arrays.asList(container.getInventory().getStorageContents()));
        if (includeSelf) storage.add(item);
        return storage;
    }

    @NotNull
    private static List<ItemStack> getBundleContents(ItemStack item, boolean includeSelf) {
        if (item == null) return new ArrayList<>();
        if (!(item.getItemMeta() instanceof BundleMeta meta)) return List.of(item);
        List<ItemStack> storage = new ArrayList<>(meta.getItems());
        if (includeSelf) storage.add(item);
        return storage;
    }
}
