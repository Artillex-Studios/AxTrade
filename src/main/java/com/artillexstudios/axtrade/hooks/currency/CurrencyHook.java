package com.artillexstudios.axtrade.hooks.currency;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface CurrencyHook {
    void setup();

    String getName();

    boolean worksOffline();

    boolean usesDouble();

    boolean isPersistent();

    double getBalance(@NotNull UUID player);

    void giveBalance(@NotNull UUID player, double amount);

    void takeBalance(@NotNull UUID player, double amount);
}
