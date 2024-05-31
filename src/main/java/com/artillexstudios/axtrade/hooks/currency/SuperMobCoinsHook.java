package com.artillexstudios.axtrade.hooks.currency;

import me.swanis.mobcoins.MobCoinsAPI;
import me.swanis.mobcoins.profile.Profile;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class SuperMobCoinsHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "SuperMobCoins";
    }

    @Override
    public String getDisplayName() {
        return HOOKS.getString("currencies.SuperMobCoins.name");
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
        return MobCoinsAPI.getProfileManager().getProfile(player).getMobCoins();
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        final Profile profile = MobCoinsAPI.getProfileManager().getProfile(player);
        profile.setMobCoins((long) (profile.getMobCoins() + amount));
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        final Profile profile = MobCoinsAPI.getProfileManager().getProfile(player);
        profile.setMobCoins((long) (profile.getMobCoins() - amount));
    }
}