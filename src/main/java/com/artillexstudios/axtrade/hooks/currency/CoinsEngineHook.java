package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axapi.utils.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoinsEngineHook implements CurrencyHook {
    private Currency currency = null;
    private final Map<String, Object> settings;
    private final String internal;

    public CoinsEngineHook(Map<Object, Object> settings) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Object, Object> entry : settings.entrySet()) {
            map.put((String) entry.getKey(), entry.getValue());
        }
        this.settings = map;
        this.internal = (String) settings.get("currency-name");
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
    public Map<String, Object> getSettings() {
        return settings;
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
        if (currency == null) {
            return CompletableFuture.completedFuture(false);
        }
        CoinsEngineAPI.addBalance(Bukkit.getPlayer(player), currency, amount);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        if (currency == null) {
            return CompletableFuture.completedFuture(false);
        }
        CoinsEngineAPI.removeBalance(Bukkit.getPlayer(player), currency, amount);
        return CompletableFuture.completedFuture(true);
    }
}