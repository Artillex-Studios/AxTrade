package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axapi.utils.StringUtils;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.objects.Account;
import me.TechsCode.UltraEconomy.objects.Currency;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class UltraEconomyHook implements CurrencyHook {
    private Currency currency = null;
    private final String internal;
    private final String name;

    public UltraEconomyHook(String internal, String name) {
        this.internal = internal;
        this.name = name;
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
    public String getDisplayName() {
        return name;
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
    public void giveBalance(@NotNull UUID player, double amount) {
        if (currency == null) return;
        final Optional<Account> account = UltraEconomy.getAPI().getAccounts().uuid(player);
        if (account.isEmpty()) return;
        account.get().addBalance(currency, amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        if (currency == null) return;
        final Optional<Account> account = UltraEconomy.getAPI().getAccounts().uuid(player);
        if (account.isEmpty()) return;
        account.get().removeBalance(currency, amount);
    }
}