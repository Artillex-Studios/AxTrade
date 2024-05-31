package com.artillexstudios.axtrade.hooks.currency;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class PlayerPointsHook implements CurrencyHook {
    private PlayerPointsAPI econ = null;

    @Override
    public void setup() {
        econ = PlayerPoints.getInstance().getAPI();
    }

    @Override
    public String getName() {
        return "PlayerPoints";
    }

    @Override
    public String getDisplayName() {
        return HOOKS.getString("currencies.PlayerPoints.name");
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
        return econ.look(player);
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        econ.give(player, (int) amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        econ.take(player, (int) Math.round(amount));
    }
}