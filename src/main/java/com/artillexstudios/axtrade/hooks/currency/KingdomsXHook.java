package com.artillexstudios.axtrade.hooks.currency;

import org.jetbrains.annotations.NotNull;
import org.kingdoms.constants.player.KingdomPlayer;

import java.util.UUID;

public class KingdomsXHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "KingdomsX";
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
        final KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player);
        if (kingdomPlayer.getKingdom() == null) return 0.0D;
        return kingdomPlayer.getKingdom().getResourcePoints();
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        final KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player);
        kingdomPlayer.getKingdom().addResourcePoints((long) amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        final KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player);
        kingdomPlayer.getKingdom().addResourcePoints((long) (amount * -1));
    }
}