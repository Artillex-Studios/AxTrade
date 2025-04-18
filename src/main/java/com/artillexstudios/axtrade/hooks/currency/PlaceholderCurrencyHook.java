package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlaceholderCurrencyHook implements CurrencyHook {
    private final String name;
    private final Section section;
    private DecimalFormat df;

    public PlaceholderCurrencyHook(String name, Section section) {
        this.name = name;
        this.section = section;
    }

    @Override
    public void setup() {
        df = new DecimalFormat("#");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> getSettings() {
        return section.getStringRouteMappedValues(true);
    }

    @Override
    public boolean worksOffline() {
        return section.getBoolean("works-offline", false);
    }

    @Override
    public boolean usesDouble() {
        return section.getBoolean("uses-double", false);
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public double getBalance(@NotNull UUID player) {
        final OfflinePlayer pl = Bukkit.getOfflinePlayer(player);
        final String placeholder = section.getString("settings.raw-placeholder");
        return Double.parseDouble(PlaceholderAPI.setPlaceholders(pl.getPlayer() == null ? pl : pl.getPlayer(), placeholder));
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        final OfflinePlayer pl = Bukkit.getOfflinePlayer(player);
        if (pl.getName() == null) {
            return CompletableFuture.completedFuture(false);
        }
        final String placeholder = section.getString("settings.give-command")
                .replace("%amount%", parseNumber(amount))
                .replace("%player%", pl.getName());
        return CompletableFuture.completedFuture(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholder));
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        final OfflinePlayer pl = Bukkit.getOfflinePlayer(player);
        if (pl.getName() == null) {
            return CompletableFuture.completedFuture(false);
        }
        final String placeholder = section.getString("settings.take-command")
                .replace("%amount%", parseNumber(amount))
                .replace("%player%", pl.getName());
        return CompletableFuture.completedFuture(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholder));
    }

    private String parseNumber(double amount) {
        return df.format(usesDouble() ? amount : Math.round(amount));
    }
}