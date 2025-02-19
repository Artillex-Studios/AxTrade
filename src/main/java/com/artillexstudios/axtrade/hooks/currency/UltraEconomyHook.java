package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axapi.utils.StringUtils;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.objects.Account;
import me.TechsCode.UltraEconomy.objects.Currency;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UltraEconomyHook implements CurrencyHook {
    private Currency currency = null;
    private final Map<String, Object> settings;
    private final String internal;

    public UltraEconomyHook(Map<Object, Object> settings) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Object, Object> entry : settings.entrySet()) {
            map.put((String) entry.getKey(), entry.getValue());
        }
        this.settings = map;
        this.internal = (String) settings.get("currency-name");
    }

    @Override
    public void setup() {
        final Optional<Currency> currencyOptional = UltraEconomy.getAPI().getCurrencies().name(internal);
        if (currencyOptional.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxTrade] UltraEconomy currency named &#DD0000" + internal + " &#FF0000not found! Change the currency-name or disable the hook to get rid of this warning!"));
            return;
        }
        currency = currencyOptional.get();
    }

    @Override
    public String getName() {
        return "UltraEconomy-" + internal;
    }

    @Override
    public Map<String, Object> getSettings() {
        return settings;
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
        if (currency == null) return 0.0D;
        final Optional<Account> account = UltraEconomy.getAPI().getAccounts().uuid(player);
        return account.map(value -> value.getBalance(currency).getOnHand()).orElse(0.0D);
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        if (currency == null) {
            cf.complete(false);
            return cf;
        }
        final Optional<Account> account = UltraEconomy.getAPI().getAccounts().uuid(player);
        if (account.isEmpty()) {
            cf.complete(false);
            return cf;
        }
        cf.complete(account.get().addBalance(currency, amount));
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        if (currency == null) {
            cf.complete(false);
            return cf;
        }
        final Optional<Account> account = UltraEconomy.getAPI().getAccounts().uuid(player);
        if (account.isEmpty()) {
            cf.complete(false);
            return cf;
        }
        cf.complete(account.get().removeBalance(currency, amount));
        return cf;
    }
}