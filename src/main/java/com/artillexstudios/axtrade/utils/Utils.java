package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.hooks.currency.PlaceholderCurrencyHook;
import com.artillexstudios.axtrade.lang.LanguageManager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class Utils {

    @NotNull
    public static String getFormattedItemName(@NotNull ItemStack itemStack) {
        return (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName().isBlank()) ? LanguageManager.getTranslated(itemStack.getType()) : itemStack.getItemMeta().getDisplayName().replace("§", "&");
    }

    @NotNull
    public static String getFormattedCurrency(@NotNull CurrencyHook currencyHook) {
        final String sc = currencyHook instanceof PlaceholderCurrencyHook ? "placeholder-currencies" : "currencies";
        return HOOKS.getString(sc + "." + currencyHook.getName() + ".name");
    }
}
