package com.artillexstudios.axtrade.hooks;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.hooks.currency.AxQuestBoardHook;
import com.artillexstudios.axtrade.hooks.currency.BeastTokensHook;
import com.artillexstudios.axtrade.hooks.currency.CoinsEngineHook;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.hooks.currency.EcoBitsHook;
import com.artillexstudios.axtrade.hooks.currency.ExperienceHook;
import com.artillexstudios.axtrade.hooks.currency.KingdomsXHook;
import com.artillexstudios.axtrade.hooks.currency.PlaceholderCurrencyHook;
import com.artillexstudios.axtrade.hooks.currency.PlayerPointsHook;
import com.artillexstudios.axtrade.hooks.currency.RedisEconomyHook;
import com.artillexstudios.axtrade.hooks.currency.RivalHarvesterHoesHook;
import com.artillexstudios.axtrade.hooks.currency.RoyaleEconomyHook;
import com.artillexstudios.axtrade.hooks.currency.SuperMobCoinsHook;
import com.artillexstudios.axtrade.hooks.currency.TheOnlyMobCoins;
import com.artillexstudios.axtrade.hooks.currency.TokenManagerHook;
import com.artillexstudios.axtrade.hooks.currency.UltraEconomyHook;
import com.artillexstudios.axtrade.hooks.currency.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class HookManager {
    private static final ArrayList<CurrencyHook> currency = new ArrayList<>();

    public static void setupHooks() {
        updateHooks();
    }

    public static void updateHooks() {
        currency.removeIf(currencyHook -> !currencyHook.isPersistent());

        if (HOOKS.getBoolean("currencies.Experience.register", true))
            currency.add(new ExperienceHook());

        if (HOOKS.getBoolean("currencies.Vault.register", true) && Bukkit.getPluginManager().getPlugin("Vault") != null) {
            currency.add(new VaultHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into Vault!"));
        }

        if (HOOKS.getBoolean("currencies.PlayerPoints.register", true) && Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
            currency.add(new PlayerPointsHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into PlayerPoints!"));
        }

        if (HOOKS.getBoolean("currencies.CoinsEngine.register", true) && Bukkit.getPluginManager().getPlugin("CoinsEngine") != null) {
            for (Map<Object, Object> curr : HOOKS.getMapList("currencies.CoinsEngine.enabled")) {
                currency.add(new CoinsEngineHook((String) curr.get("currency-name"), (String) curr.get("name")));
            }
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into CoinsEngine!"));
        }

        if (HOOKS.getBoolean("currencies.RoyaleEconomy.register", true) && Bukkit.getPluginManager().getPlugin("RoyaleEconomy") != null) {
            currency.add(new RoyaleEconomyHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into RoyaleEconomy!"));
        }

        if (HOOKS.getBoolean("currencies.UltraEconomy.register", true) && Bukkit.getPluginManager().getPlugin("UltraEconomy") != null) {
            for (Map<Object, Object> curr : HOOKS.getMapList("currencies.UltraEconomy.enabled")) {
                currency.add(new UltraEconomyHook((String) curr.get("currency-name"), (String) curr.get("name")));
            }
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into UltraEconomy!"));
        }

        if (HOOKS.getBoolean("currencies.KingdomsX.register", true) && Bukkit.getPluginManager().getPlugin("Kingdoms") != null) {
            currency.add(new KingdomsXHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into KingdomsX!"));
        }

        if (HOOKS.getBoolean("currencies.RivalHarvesterHoes.register", true) && Bukkit.getPluginManager().getPlugin("RivalHarvesterHoes") != null) {
            currency.add(new RivalHarvesterHoesHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into RivalHarvesterHoes!"));
        }

        if (HOOKS.getBoolean("currencies.SuperMobCoins.register", true) && Bukkit.getPluginManager().getPlugin("SuperMobCoins") != null) {
            currency.add(new SuperMobCoinsHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into SuperMobCoins!"));
        }

        if (HOOKS.getBoolean("currencies.TheOnly-MobCoins.register", true) && Bukkit.getPluginManager().getPlugin("TheOnly-MobCoins") != null) {
            currency.add(new TheOnlyMobCoins());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into TheOnly-MobCoins!"));
        }

        if (HOOKS.getBoolean("currencies.TokenManager.register", true) && Bukkit.getPluginManager().getPlugin("TokenManager") != null) {
            currency.add(new TokenManagerHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into TokenManager!"));
        }

        if (HOOKS.getBoolean("currencies.AxQuestBoard.register", true) && Bukkit.getPluginManager().getPlugin("AxQuestBoard") != null) {
            currency.add(new AxQuestBoardHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into AxQuestBoard!"));
        }

        if (HOOKS.getBoolean("currencies.RedisEconomy.register", true) && Bukkit.getPluginManager().getPlugin("RedisEconomy") != null) {
            for (Map<Object, Object> curr : HOOKS.getMapList("currencies.RedisEconomy.enabled")) {
                currency.add(new RedisEconomyHook((String) curr.get("currency-name"), (String) curr.get("name")));
            }
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into RedisEconomy!"));
        }

        if (HOOKS.getBoolean("currencies.BeastTokens.register", true) && Bukkit.getPluginManager().getPlugin("BeastTokens") != null) {
            currency.add(new BeastTokensHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into BeastTokens!"));
        }

        if (HOOKS.getBoolean("currencies.EcoBits.register", true) && Bukkit.getPluginManager().getPlugin("EcoBits") != null) {
            for (Map<Object, Object> curr : HOOKS.getMapList("currencies.EcoBits.enabled")) {
                currency.add(new EcoBitsHook((String) curr.get("currency-name"), (String) curr.get("name")));
            }
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into EcoBits!"));
        }

        for (String str : HOOKS.getSection("placeholder-currencies").getRoutesAsStrings(false)) {
            if (!HOOKS.getBoolean("placeholder-currencies." + str + ".register", false)) continue;
            currency.add(new PlaceholderCurrencyHook(str, HOOKS.getSection("placeholder-currencies." + str)));
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Loaded placeholder currency " + str + "!"));
        }

        for (CurrencyHook hook : currency) hook.setup();
    }

    @SuppressWarnings("unused")
    public static void registerCurrencyHook(@NotNull Plugin plugin, @NotNull CurrencyHook currencyHook) {
        currency.add(currencyHook);
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into " + plugin.getName() + "! Note: You must set the currency provider to CUSTOM or it will be overridden after reloading!"));
    }

    @NotNull
    public static ArrayList<CurrencyHook> getCurrency() {
        return currency;
    }

    @Nullable
    public static CurrencyHook getCurrencyHook(@NotNull String name) {
        for (CurrencyHook hook : currency) {
            if (!hook.getName().equals(name)) continue;
            return hook;
        }

        return null;
    }
}
