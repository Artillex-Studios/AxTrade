package com.artillexstudios.axtrade.hooks.currency;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public Map<String, Object> getSettings() {
        return HOOKS.getSection("currencies." + getName()).getStringRouteMappedValues(true);
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
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        EconomyResponse response = econ.depositPlayer(Bukkit.getOfflinePlayer(player), amount);
        return CompletableFuture.completedFuture(response.transactionSuccess());
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        EconomyResponse response = econ.withdrawPlayer(Bukkit.getOfflinePlayer(player), amount);
        return CompletableFuture.completedFuture(response.transactionSuccess());
    }
}