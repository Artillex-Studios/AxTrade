package com.artillexstudios.axtrade.hooks.currency;

import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.objects.Account;
import me.TechsCode.UltraEconomy.objects.Currency;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class UltraEconomyHook implements CurrencyHook {
    private Currency currency = null;

    @Override
    public void setup() {
        final Optional<Currency> currencyOptional = UltraEconomy.getAPI().getCurrencies().name(HOOKS.getString("currencies.UltraEconomy.currency-name", "coins"));
        if (!currencyOptional.isPresent()) throw new RuntimeException("Currency not found!");
        currency = currencyOptional.get();
    }

    @Override
    public String getName() {
        return "UltraEconomy";
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
        final Optional<Account> account = UltraEconomy.getAPI().getAccounts().uuid(player);
        if (!account.isPresent()) return 0.0D;
        return account.get().getBalance(currency).getOnHand();
    }

    @Override
    public void giveBalance(@NotNull UUID player, double amount) {
        final Optional<Account> account = UltraEconomy.getAPI().getAccounts().uuid(player);
        if (account.isEmpty()) return;
        account.get().addBalance(currency, amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        final Optional<Account> account = UltraEconomy.getAPI().getAccounts().uuid(player);
        if (account.isEmpty()) return;
        account.get().removeBalance(currency, amount);
    }
}