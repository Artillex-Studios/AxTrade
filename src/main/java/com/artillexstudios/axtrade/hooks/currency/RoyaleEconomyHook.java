package com.artillexstudios.axtrade.hooks.currency;

import me.qKing12.RoyaleEconomy.RoyaleEconomy;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class RoyaleEconomyHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "RoyaleEconomy";
    }

    @Override
    public Map<String, Object> getSettings() {
        return HOOKS.getSection("currencies." + getName()).getStringRouteMappedValues(true);
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
        return RoyaleEconomy.apiHandler.balance.getBalance(player.toString());
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        RoyaleEconomy.apiHandler.balance.addBalance(player.toString(), amount);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        RoyaleEconomy.apiHandler.balance.removeBalance(player.toString(), amount);
        return CompletableFuture.completedFuture(true);
    }
}