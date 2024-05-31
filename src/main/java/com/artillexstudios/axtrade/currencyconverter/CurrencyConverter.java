package com.artillexstudios.axtrade.currencyconverter;

import com.artillexstudios.axapi.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyConverter {

    public CurrencyConverter(Config config) {
        if (config.getString("currencies.CoinsEngine.currency-name", null) == null) return;

        final List<Map<String, String>> coinsEngine = new ArrayList<>();

        coinsEngine.add(Map.of(
                "currency-name", config.getString("currencies.CoinsEngine.currency-name", "coins"),
                "name", config.getString("currencies.CoinsEngine.name", "coins")
        ));

        config.set("currencies.CoinsEngine.enabled", coinsEngine);

        config.getBackingDocument().remove("currencies.CoinsEngine.currency-name");
        config.getBackingDocument().remove("currencies.CoinsEngine.name");

        final List<Map<String, String>> ultraEconomy = new ArrayList<>();

        ultraEconomy.add(Map.of(
                "currency-name", config.getString("currencies.UltraEconomy.currency-name", "coins"),
                "name", config.getString("currencies.UltraEconomy.name", "coins")
        ));

        config.set("currencies.UltraEconomy.enabled", ultraEconomy);

        config.getBackingDocument().remove("currencies.UltraEconomy.currency-name");
        config.getBackingDocument().remove("currencies.UltraEconomy.name");

        config.save();
    }
}
