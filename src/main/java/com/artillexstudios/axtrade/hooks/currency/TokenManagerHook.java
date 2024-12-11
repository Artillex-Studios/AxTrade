package com.artillexstudios.axtrade.hooks.currency;

import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class TokenManagerHook implements CurrencyHook {
    private TokenManager eco;

    @Override
    public void setup() {
        eco = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
    }

    @Override
    public String getName() {
        return "TokenManager";
    }

    @Override
    public String getDisplayName() {
        return HOOKS.getString("currencies.TokenManager.name");
    }

    @Override
    public boolean worksOffline() {
        return false;
    }

    @Override
    public boolean usesDouble() {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public double getBalance(@NotNull UUID player) {
        return eco.getTokens(Bukkit.getPlayer(player)).orElse(0);
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        cf.complete(eco.addTokens(Bukkit.getPlayer(player), (long) amount));
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        cf.complete(eco.removeTokens(Bukkit.getPlayer(player), (long) amount));
        return cf;
    }
}