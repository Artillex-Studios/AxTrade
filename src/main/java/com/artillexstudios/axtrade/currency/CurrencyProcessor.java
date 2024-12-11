package com.artillexstudios.axtrade.currency;

import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CurrencyProcessor {
    private final Player player;
    private final Set<Map.Entry<CurrencyHook, Double>> currencies;
    private final Map<Map.Entry<CurrencyHook, Double>, Boolean> states = new HashMap<>();

    public CurrencyProcessor(Player player, Set<Map.Entry<CurrencyHook, Double>> currencies) {
        this.player = player;
        this.currencies = currencies;
    }

    public CompletableFuture<Boolean> run() {
        CompletableFuture<Boolean>[] futures = new CompletableFuture[currencies.size()];

        int i = 0;
        for (Map.Entry<CurrencyHook, Double> entry : currencies) {
            futures[i] = entry.getKey().takeBalance(player.getUniqueId(), entry.getValue()); // try taking money, generate futures
            i++;
        }

        // run all futures
        CompletableFuture<Boolean> success = CompletableFuture.allOf(futures).thenApply(unused -> {
            boolean[] actions = new boolean[futures.length];
            boolean anyErrors = false;
            for (int j = 0; j < futures.length; j++) {
                actions[j] = futures[j].join(); // store currency take results
                if (!actions[j]) anyErrors = true; // if any currency take failed, store
            }

            // used if reversing is needed to give back the currency
            int j = 0;
            for (Map.Entry<CurrencyHook, Double> entry : currencies) {
                states.put(entry, actions[j]); // map of currency, amount and did they succeed?
                j++;
            }

            if (anyErrors) { // if there any fails, reverse process and return fail
                reverse(); // todo: use return value maybe
            }

            return !anyErrors; // if failed, return false, otherwise true
        });

        return success;
    }

    public CompletableFuture<Boolean> reverse() {
        for (Map.Entry<Map.Entry<CurrencyHook, Double>, Boolean> entry : states.entrySet()) {
            if (!entry.getValue()) continue; // if failed to send, don't give back
            entry.getKey().getKey().giveBalance(player.getUniqueId(), entry.getKey().getValue());
        }
        return null; // todo: return a future (maybe there is no point tho)
    }
}
