package com.artillexstudios.axtrade.api;

import com.artillexstudios.axtrade.hooks.HookManager;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class AxTradeAPI {

    public static void registerCurrencyHook(@NotNull Plugin plugin, @NotNull CurrencyHook currencyHook) {
        HookManager.registerCurrencyHook(plugin, currencyHook);
    }
}
