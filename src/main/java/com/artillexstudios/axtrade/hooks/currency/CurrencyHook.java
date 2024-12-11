package com.artillexstudios.axtrade.hooks.currency;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CurrencyHook {
    void setup();

    String getName();

    String getDisplayName();

    boolean worksOffline();

    boolean usesDouble();

    boolean isPersistent();

    double getBalance(@NotNull UUID player);

    CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount);

    CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount);
}
