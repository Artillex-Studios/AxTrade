package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axapi.utils.StringUtils;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.Currency;
import com.willfp.ecobits.currencies.CurrencyUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EcoBitsHook implements CurrencyHook {
    private Currency currency = null;
    private final Map<String, Object> settings;
    private final String internal;

    public EcoBitsHook(Map<Object, Object> settings) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Object, Object> entry : settings.entrySet()) {
            map.put((String) entry.getKey(), entry.getValue());
        }
        this.settings = map;
        this.internal = (String) settings.get("currency-name");
    }

    @Override
    public void setup() {
        currency = Currencies.getByID(internal);
        if (currency == null) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxTrade] EcoBits currency named &#DD0000" + internal + " &#FF0000not found! Change the currency-name or disable the hook to get rid of this warning!"));
        }
    }

    @Override
    public String getName() {
        return "EcoBits-" + internal;
    }

    @Override
    public Map<String, Object> getSettings() {
        return settings;
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
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        if (currency == null) {
            return CompletableFuture.completedFuture(false);
        }
        CurrencyUtils.adjustBalance(Bukkit.getOfflinePlayer(player), currency, BigDecimal.valueOf(amount));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        if (currency == null) {
            return CompletableFuture.completedFuture(false);
        }
        CurrencyUtils.adjustBalance(Bukkit.getOfflinePlayer(player), currency, BigDecimal.valueOf(amount * -1));
        return CompletableFuture.completedFuture(true);
    }
}