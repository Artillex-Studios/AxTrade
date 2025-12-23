package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axhoes.api.AxHoesAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class AxHoesHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "AxHoes";
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
        return false;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public double getBalance(@NotNull UUID player) {
        if (AxHoesAPI.getEssenceHook() == null) return 0;
        return AxHoesAPI.getEssenceHook().getBalance(player);
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        if (AxHoesAPI.getEssenceHook() == null) return CompletableFuture.completedFuture(false);
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        AxHoesAPI.getEssenceHook().giveBalance(player, amount).thenAccept(cf::complete);
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        if (AxHoesAPI.getEssenceHook() == null) return CompletableFuture.completedFuture(false);
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        AxHoesAPI.getEssenceHook().takeBalance(player, amount).thenAccept(cf::complete);
        return cf;
    }
}