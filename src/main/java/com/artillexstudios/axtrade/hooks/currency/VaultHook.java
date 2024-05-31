package com.artillexstudios.axtrade.hooks.currency;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class VaultHook implements CurrencyHook {
    private Economy econ = null;

    @Override
    public void setup() {
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;

        econ = rsp.getProvider();
    }

    @Override
    public String getName() {
        return "Vault";
    }

    @Override
    public String getDisplayName() {
        return HOOKS.getString("currencies.Vault.name");
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
        return econ.getBalance(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        econ.depositPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        econ.withdrawPlayer(Bukkit.getOfflinePlayer(player), amount);
    }
}