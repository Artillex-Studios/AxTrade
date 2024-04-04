package com.artillexstudios.axtrade.hooks.currency;

import me.rivaldev.harvesterhoes.Main;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RivalHarvesterHoesHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "RivalHarvesterHoes";
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
    public void giveBalance(@NotNull UUID player, double amount) {
        Main.instance.getEconomy().giveEconomyAmount(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        Main.instance.getEconomy().removeEconomyAmount(Bukkit.getOfflinePlayer(player), amount);
    }
}