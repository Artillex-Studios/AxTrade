package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.items.components.DataComponents;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Utils {
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    @NotNull
    public static Component getItemName(@NotNull ItemStack itemStack) {
        final WrappedItemStack wrapped = WrappedItemStack.wrap(itemStack);
        final Component customName = wrapped.get(DataComponents.CUSTOM_NAME);
        if (customName != null) return customName;

        final Component itemName = wrapped.get(DataComponents.ITEM_NAME);
        if (itemName != null) return itemName;

        return Component.translatable(itemStack.getTranslationKey());
    }

    @NotNull
    public static String getPlainItemName(@NotNull ItemStack itemStack) {
        final String plainName = PLAIN_TEXT.serialize(getItemName(itemStack));
        if (!plainName.isBlank() && !plainName.equals(itemStack.getTranslationKey())) return plainName;

        return itemStack.getType().name().toLowerCase(Locale.ROOT).replace('_', ' ');
    }

    @NotNull
    public static String getFormattedCurrency(@NotNull CurrencyHook currencyHook) {
        return currencyHook.getSettings().getOrDefault("name", currencyHook.getName()).toString();
    }
}
