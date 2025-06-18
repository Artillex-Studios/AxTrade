package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axtrade.hooks.HookManager;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.SoundUtils;
import com.artillexstudios.axtrade.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class TradePlayer {
    private final Player player;
    private TradePlayer otherPlayer;
    private TradeGui tradeGui;
    private final Trade trade;
    private final ConcurrentHashMap<CurrencyHook, Double> currencies = new ConcurrentHashMap<>();

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

        Scheduler.get().runAt(otherPlayer.getPlayer().getLocation(), scheduledTask -> {
            this.tradeGui = new TradeGui(trade, this);
            this.tradeGui.open();
        });
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
        for (CurrencyHook currencyHook : HookManager.getCurrency()) {
            Number minimum = (Number) currencyHook.getSettings().getOrDefault("required", 0);
            if (minimum.doubleValue() <= 0) continue;
            if (getCurrency(currencyHook.getName()) + otherPlayer.getCurrency(currencyHook.getName()) < minimum.doubleValue()) {
                MESSAGEUTILS.sendLang(player, "request.below-required-amount", Map.of(
                        "%amount%", NumberUtils.formatNumber(minimum.doubleValue()),
                        "%currency%", Utils.getFormattedCurrency(currencyHook)
                ));
                return;
            }
        }

        this.confirmed = CONFIG.getInt("trade-confirm-seconds", 10);
        trade.update();
        SoundUtils.playSound(player, "accept");
        SoundUtils.playSound(otherPlayer.getPlayer(), "accept");

        getTradeGui().updateTitle();
        otherPlayer.getTradeGui().updateTitle();
    }

    public void cancel() {
        if (confirmed != null) {
            this.confirmed = null;
            otherPlayer.setConfirmed(null);
            SoundUtils.playSound(player, "cancel");
            SoundUtils.playSound(otherPlayer.getPlayer(), "cancel");

            getTradeGui().updateTitle();
            otherPlayer.getTradeGui().updateTitle();
            return;
        }
        if (otherPlayer.setConfirmed(null)) getTradeGui().updateTitle();
    }

    public boolean setConfirmed(Integer confirmed) {
        if (Objects.equals(this.confirmed, confirmed)) return false;
        this.confirmed = confirmed;
        getTradeGui().updateTitle();
        return true;
    }

    public void tick() {
        if (confirmed == null) return;

        confirmed -= 1;
        trade.update();
        SoundUtils.playSound(player, "countdown");
    }

    public ConcurrentHashMap<CurrencyHook, Double> getCurrencies() {
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

    public int getEmptySlots() {
        int am = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null) continue;
            am++;
        }
        return am;
    }

    public enum Result {
        NOT_A_NUMBER,
        CURRENCY_NOT_FOUND,
        TOO_LOW_VALUE,
        NOT_ENOUGH_CURRENCY,
        SUCCESS
    }
}
