package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axapi.utils.StringUtils;
import dev.unnm3d.rediseconomy.api.RedisEconomyAPI;
import dev.unnm3d.rediseconomy.currency.Currency;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisEconomyHook implements CurrencyHook {
    private Currency currency = null;
    private final Map<String, Object> settings;
    private final String name;
    private final String internal;

    public RedisEconomyHook(Map<Object, Object> settings) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Object, Object> entry : settings.entrySet()) {
            map.put((String) entry.getKey(), entry.getValue());
        }
        this.settings = map;
        this.internal = (String) settings.get("currency-name");
        this.name = (String) settings.get("name");
    }

    @Override
    public void setup() {
        RedisEconomyAPI api = RedisEconomyAPI.getAPI();
        currency = api.getCurrencyByName(internal);
        if (currency == null) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxTrade] RedisEconomy currency named &#DD0000" + internal + " &#FF0000not found! Change the currency-name or disable the hook to get rid of this warning!"));
        }
    }

    @Override
    public String getName() {
        return internal;
    }

    @Override
    public Map<String, Object> getSettings() {
        return settings;
    }

    @Override
    public boolean worksOffline() {
        return true;
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
        if (currency == null) return 0.0D;
        return currency.getBalance(player);
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        if (currency == null) {
            cf.complete(false);
            return cf;
        }
        EconomyResponse economyResponse = currency.depositPlayer(player, null, amount, null);
        cf.complete(economyResponse.transactionSuccess());
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        if (currency == null) {
            cf.complete(false);
            return cf;
        }
        EconomyResponse economyResponse = currency.withdrawPlayer(player, null, amount, null);
        cf.complete(economyResponse.transactionSuccess());
        return cf;
    }
}