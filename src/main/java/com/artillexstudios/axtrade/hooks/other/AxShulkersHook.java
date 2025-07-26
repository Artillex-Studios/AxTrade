package com.artillexstudios.axtrade.hooks.other;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AxShulkersHook {

    public ItemStack[] getItems(ItemStack item) {
        if (!ShulkerUtils.isShulker(item)) return null;
        UUID uuid = ShulkerUtils.getShulkerUUID(item);
        if (uuid == null) return null;
        Shulkerbox shulkerbox = Shulkerboxes.getShulkerMap().get(uuid);
        if (shulkerbox == null) return AxShulkers.getDB().getShulker(uuid);
        return shulkerbox.getShulkerInventory().getStorageContents();
    }
}
