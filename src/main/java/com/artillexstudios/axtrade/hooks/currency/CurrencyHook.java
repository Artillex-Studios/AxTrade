package com.artillexstudios.axtrade.hooks.currency;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public interface CurrencyHook {
    void setup();

    String getName();

    default Map<String, Object> getSettings() {
        return HOOKS.getSection("currencies." + getName()).getStringRouteMappedValues(true);
    }

    boolean worksOffline();

    boolean usesDouble();

    boolean isPersistent();

    double getBalance(@NotNull UUID player);

    CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount);

    CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount);
}
