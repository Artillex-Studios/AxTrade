package com.artillexstudios.axtrade.hooks.currency;

import me.rivaldev.harvesterhoes.Main;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class RivalHarvesterHoesHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "RivalHarvesterHoes";
    }

    @Override
    public String getDisplayName() {
        return HOOKS.getString("currencies.RivalHarvesterHoes.name");
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
        return Main.instance.getEconomy().getEconomyAmount(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        Main.instance.getEconomy().giveEconomyAmount(Bukkit.getOfflinePlayer(player), amount);
        cf.complete(true);
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        Main.instance.getEconomy().removeEconomyAmount(Bukkit.getOfflinePlayer(player), amount);
        cf.complete(true);
        return cf;
    }
}