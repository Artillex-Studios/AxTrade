package com.artillexstudios.axtrade.hooks.currency;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.UUID;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class CoinsEngineHook implements CurrencyHook {
    private Currency currency = null;

    @Override
    public void setup() {
        currency = CoinsEngineAPI.getCurrency(HOOKS.getString("currencies.CoinsEngine.currency-name", "coins"));
    }

    @Override
    public String getName() {
        return "CoinsEngine";
    }

    @Override
    public boolean worksOffline() {
        return false;
    }

    @Override
    public boolean usesDouble() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public double getBalance(@NotNull UUID player) {
        if (currency == null) return 0;
        return CoinsEngineAPI.getBalance(Bukkit.getPlayer(player), currency);
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        if (currency == null) return;
        CoinsEngineAPI.addBalance(Bukkit.getPlayer(player), currency, amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        if (currency == null) return;
        CoinsEngineAPI.removeBalance(Bukkit.getPlayer(player), currency, amount);
    }
}