package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axapi.reflection.ClassUtils;
import org.bukkit.block.Barrel;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

public class ShulkerUtils {

    @NotNull
    public static ItemStack[] getShulkerContents(@NotNull ItemStack item) {
        if (!(item.getItemMeta() instanceof BlockStateMeta)) return new ItemStack[0];
        final BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        if (meta.getBlockState() instanceof ShulkerBox) {
            final ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
            if (ClassUtils.INSTANCE.classExists("com.artillexstudios.axshulkers.utils.ShulkerUtils") && com.artillexstudios.axshulkers.utils.ShulkerUtils.getShulkerUUID(item) != null) {
                return com.artillexstudios.axshulkers.AxShulkers.getDB().getShulker(com.artillexstudios.axshulkers.utils.ShulkerUtils.getShulkerUUID(item));
            }
            return shulker.getInventory().getContents();
        } else if (meta.getBlockState() instanceof Barrel) {
            final Barrel barrel = (Barrel) meta.getBlockState();
            return barrel.getInventory().getContents();
        }

        return new ItemStack[0];
    }
}
