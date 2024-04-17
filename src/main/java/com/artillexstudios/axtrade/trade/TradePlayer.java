package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axtrade.hooks.HookManager;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.SoundUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class TradePlayer {
    private final Player player;
    private TradePlayer otherPlayer;
    private TradeGui tradeGui;
    private final Trade trade;
    private final HashMap<CurrencyHook, Double> currencies = new HashMap<>();

    // confirmed
    // null > not confirmed
    // number > decrease every sec
    private Integer confirmed = null;

    public TradePlayer(Trade trade, Player player) {
        this.player = player;
        this.trade = trade;
    }

    public void setOtherPlayer(TradePlayer otherPlayer) {
        this.otherPlayer = otherPlayer;
        this.tradeGui = new TradeGui(trade, this);
    }

    public Player getPlayer() {
        return player;
    }

    public TradePlayer getOtherPlayer() {
        return otherPlayer;
    }

    public TradeGui getTradeGui() {
        return tradeGui;
    }

    public Integer getConfirmed() {
        return confirmed;
    }

    public boolean hasConfirmed() {
        return confirmed != null;
    }

    public void confirm() {
        this.confirmed = CONFIG.getInt("trade-confirm-seconds", 10);
        trade.update();
        SoundUtils.playSound(player, "accept");
        SoundUtils.playSound(otherPlayer.getPlayer(), "accept");
    }

    public void cancel() {
        if (confirmed != null) {
            this.confirmed = null;
            SoundUtils.playSound(player, "cancel");
            SoundUtils.playSound(otherPlayer.getPlayer(), "cancel");
        }
        otherPlayer.setConfirmed(null);
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public void tick() {
        confirmed -= 1;
        trade.update();
        SoundUtils.playSound(player, "countdown");
    }

    public HashMap<CurrencyHook, Double> getCurrencies() {
        return currencies;
    }

    public double getCurrency(String currency) {
        final CurrencyHook currencyHook = HookManager.getCurrencyHook(currency);
        if (currencyHook == null) return 0;
        if (!currencies.containsKey(currencyHook)) return 0;
        return currencies.get(currencyHook);
    }

    public Result setCurrency(String currency, String am) {
        final Double unformatted = NumberUtils.parseNumber(am);
        if (unformatted == null && !com.artillexstudios.axapi.utils.NumberUtils.isDouble(am)) return Result.NOT_A_NUMBER;
        double amount = unformatted == null ? Double.parseDouble(am) : unformatted;
        if (Double.isNaN(amount)) return Result.NOT_A_NUMBER;
        final CurrencyHook currencyHook = HookManager.getCurrencyHook(currency);
        if (currencyHook == null) return Result.CURRENCY_NOT_FOUND;
        amount = currencyHook.usesDouble() ? amount : Math.round(amount);
        if (amount < 0.1) return Result.TOO_LOW_VALUE;
        if (currencyHook.getBalance(player.getUniqueId()) < amount) return Result.NOT_ENOUGH_CURRENCY;
        currencies.put(currencyHook, amount);
        return Result.SUCCESS;
    }

    public enum Result {
        NOT_A_NUMBER,
        CURRENCY_NOT_FOUND,
        TOO_LOW_VALUE,
        NOT_ENOUGH_CURRENCY,
        SUCCESS
    }
}
