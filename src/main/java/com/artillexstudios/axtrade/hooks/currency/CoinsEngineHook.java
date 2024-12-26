package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axapi.utils.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoinsEngineHook implements CurrencyHook {
    private Currency currency = null;
    private final String internal;
    private final String name;

    public CoinsEngineHook(String internal, String name) {
        this.internal = internal;
        this.name = name;
    }

    @Override
    public void setup() {
        currency = CoinsEngineAPI.getCurrency(internal);
        if (currency == null) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxTrade] CoinsEngine currency named &#DD0000" + internal + " &#FF0000not found! Change the currency-name or disable the hook to get rid of this warning!"));
        }
    }

    @Override
    public String getName() {
        return "CoinsEngine-" + internal;
    }

    @Override
    public String getDisplayName() {
        return name;
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
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        if (currency == null) {
            cf.complete(false);
            return cf;
        }
        CoinsEngineAPI.addBalance(Bukkit.getPlayer(player), currency, amount);
        cf.complete(true);
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        if (currency == null) {
            cf.complete(false);
            return cf;
        }
        CoinsEngineAPI.removeBalance(Bukkit.getPlayer(player), currency, amount);
        cf.complete(true);
        return cf;
    }
}