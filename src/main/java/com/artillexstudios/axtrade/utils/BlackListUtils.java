package com.artillexstudios.axtrade.utils;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class BlackListUtils {

    public static boolean isBlackListed(@Nullable ItemStack it) {
        if (it == null) return false;
        for (String s : CONFIG.getSection("blacklisted-items").getRoutesAsStrings(false)) {
            if (CONFIG.getString("blacklisted-items." + s + ".material") != null) {
                if (!it.getType().toString().equalsIgnoreCase(CONFIG.getString("blacklisted-items." + s + ".material"))) continue;
                return true;
            }

            if (CONFIG.getString("blacklisted-items." + s + ".name-contains") != null) {
                if (it.getItemMeta() == null) continue;
                if (!it.getItemMeta().getDisplayName().contains(CONFIG.getString("blacklisted-items." + s + ".name-contains"))) continue;
                return true;
            }
        }
        return false;
    }
}
