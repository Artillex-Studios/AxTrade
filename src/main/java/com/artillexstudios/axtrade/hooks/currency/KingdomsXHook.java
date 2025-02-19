package com.artillexstudios.axtrade.hooks.currency;

import org.jetbrains.annotations.NotNull;
import org.kingdoms.constants.player.KingdomPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class KingdomsXHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "KingdomsX";
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
        final KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player);
        if (kingdomPlayer.getKingdom() == null) return 0.0D;
        return kingdomPlayer.getKingdom().getResourcePoints();
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        final KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player);
        kingdomPlayer.getKingdom().addResourcePoints((long) amount);
        cf.complete(true);
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        final KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player);
        kingdomPlayer.getKingdom().addResourcePoints((long) (amount * -1));
        cf.complete(true);
        return cf;
    }
}