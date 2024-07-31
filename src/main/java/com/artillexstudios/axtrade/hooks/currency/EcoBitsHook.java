package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axapi.utils.StringUtils;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.Currency;
import com.willfp.ecobits.currencies.CurrencyUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class EcoBitsHook implements CurrencyHook {
    private Currency currency = null;
    private final String internal;
    private final String name;

    public EcoBitsHook(String internal, String name) {
        this.internal = internal;
        this.name = name;
    }

    @Override
    public void setup() {
        currency = Currencies.getByID(internal);
        if (currency == null) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxAuctions] EcoBits currency named &#DD0000" + internal + " &#FF0000not found! Change the currency-name or disable the hook to get rid of this warning!"));
        }
    }

    @Override
    public String getName() {
        return "EcoBits-" + internal;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public boolean worksOffline() {
        return false;
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
        if (currency == null) return 0;
        return CurrencyUtils.getBalance(Bukkit.getOfflinePlayer(player), currency).doubleValue();
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        if (currency == null) return;
        CurrencyUtils.adjustBalance(Bukkit.getOfflinePlayer(player), currency, BigDecimal.valueOf(amount));
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        if (currency == null) return;
        CurrencyUtils.adjustBalance(Bukkit.getOfflinePlayer(player), currency, BigDecimal.valueOf(amount * -1));
    }
}