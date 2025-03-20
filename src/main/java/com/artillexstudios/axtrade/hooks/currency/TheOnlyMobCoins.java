package com.artillexstudios.axtrade.hooks.currency;

import me.aglerr.mobcoins.api.MobCoinsAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class TheOnlyMobCoins implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "TheOnly-MobCoins";
    }

    @Override
    public Map<String, Object> getSettings() {
        return HOOKS.getSection("currencies." + getName()).getStringRouteMappedValues(true);
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
        if (MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)) == null) return 0;
        return MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)).getCoins();
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        if (MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)) == null) {
            return CompletableFuture.completedFuture(false);
        }
        MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)).addCoins(amount);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        if (MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)) == null) {
            return CompletableFuture.completedFuture(false);
        }
        MobCoinsAPI.getPlayerData(Bukkit.getPlayer(player)).reduceCoins(amount);
        return CompletableFuture.completedFuture(true);
    }
}