package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.utils.HistoryUtils;
import com.artillexstudios.axtrade.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.SoundUtils;
import com.artillexstudios.axtrade.utils.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Trade {
    protected final TradePlayer player1;
    protected final TradePlayer player2;
    private boolean ended = false;
    protected long prepTime = System.currentTimeMillis();

    public Trade(Player p1, Player p2) {
        this.player1 = new TradePlayer(this, p1);
        this.player2 = new TradePlayer(this, p2);
        player1.setOtherPlayer(player2);
        player2.setOtherPlayer(player1);

        HistoryUtils.writeToHistory(String.format("Started: %s - %s", player1.getPlayer().getName(), player2.getPlayer().getName()));
    }

    public void update() {
        player1.getTradeGui().update();
        player2.getTradeGui().update();
    }

    public void abort() {
        if (ended) return;
        player1.getTradeGui().getItems().forEach(itemStack -> {
            if (itemStack == null) return;
            player1.getPlayer().getInventory().addItem(itemStack);
        });
        player2.getTradeGui().getItems().forEach(itemStack -> {
            if (itemStack == null) return;
            player2.getPlayer().getInventory().addItem(itemStack);
        });
        HistoryUtils.writeToHistory(String.format("Aborted: %s - %s", player1.getPlayer().getName(), player2.getPlayer().getName()));
        MESSAGEUTILS.sendLang(player1.getPlayer(), "trade.aborted", Map.of("%player%", player2.getPlayer().getName()));
        MESSAGEUTILS.sendLang(player2.getPlayer(), "trade.aborted", Map.of("%player%", player1.getPlayer().getName()));
        SoundUtils.playSound(player1.getPlayer(), "aborted");
        SoundUtils.playSound(player2.getPlayer(), "aborted");
        end();
    }

    public void end() {
        if (ended) return;
        ended = true;
        Scheduler.get().run(scheduledTask -> Trades.removeTrade(Trade.this));
        player1.getPlayer().closeInventory();
        player1.getPlayer().updateInventory();
        player2.getPlayer().closeInventory();
        player2.getPlayer().updateInventory();
    }

    public void complete() {
        for (Map.Entry<CurrencyHook, Double> entry : player1.getCurrencies().entrySet()) {
            if (entry.getKey().getBalance(player1.getPlayer().getUniqueId()) < entry.getValue()) {
                abort();
                return;
            }
        }

        for (Map.Entry<CurrencyHook, Double> entry : player2.getCurrencies().entrySet()) {
            if (entry.getKey().getBalance(player2.getPlayer().getUniqueId()) < entry.getValue()) {
                abort();
                return;
            }
        }

        MESSAGEUTILS.sendLang(player1.getPlayer(), "trade.completed", Map.of("%player%", player2.getPlayer().getName()));
        MESSAGEUTILS.sendLang(player2.getPlayer(), "trade.completed", Map.of("%player%", player1.getPlayer().getName()));
        SoundUtils.playSound(player1.getPlayer(), "completed");
        SoundUtils.playSound(player2.getPlayer(), "completed");

        List<String> player1Currencies = new ArrayList<>();
        for (Map.Entry<CurrencyHook, Double> entry : player1.getCurrencies().entrySet()) {
            entry.getKey().takeBalance(player1.getPlayer().getUniqueId(), entry.getValue());
            entry.getKey().giveBalance(player2.getPlayer().getUniqueId(), entry.getValue());
            String currencyName = Utils.getFormattedCurrency(entry.getKey());
            String currencyAm = NumberUtils.formatNumber(entry.getValue());
            player1Currencies.add(currencyName + ": " + currencyAm);
            if (CONFIG.getBoolean("enable-trade-summaries")) {
                MESSAGEUTILS.sendFormatted(player2.getPlayer(), LANG.getString("summary.get.currency"), Map.of("%amount%", currencyAm, "%currency%", currencyName));
                MESSAGEUTILS.sendFormatted(player1.getPlayer(), LANG.getString("summary.give.currency"), Map.of("%amount%", currencyAm, "%currency%", currencyName));
            }
        }

        List<String> player2Currencies = new ArrayList<>();
        for (Map.Entry<CurrencyHook, Double> entry : player2.getCurrencies().entrySet()) {
            entry.getKey().takeBalance(player2.getPlayer().getUniqueId(), entry.getValue());
            entry.getKey().giveBalance(player1.getPlayer().getUniqueId(), entry.getValue());
            String currencyName = Utils.getFormattedCurrency(entry.getKey());
            String currencyAm = NumberUtils.formatNumber(entry.getValue());
            player2Currencies.add(currencyName + ": " + currencyAm);
            if (CONFIG.getBoolean("enable-trade-summaries")) {
                MESSAGEUTILS.sendFormatted(player2.getPlayer(), LANG.getString("summary.give.currency"), Map.of("%amount%", currencyAm, "%currency%", currencyName));
                MESSAGEUTILS.sendFormatted(player1.getPlayer(), LANG.getString("summary.get.currency"), Map.of("%amount%", currencyAm, "%currency%", currencyName));
            }
        }

        List<String> player1Items = new ArrayList<>();
        player1.getTradeGui().getItems().forEach(itemStack -> {
            if (itemStack == null) return;
            Scheduler.get().runAt(player2.getPlayer().getLocation(), task -> {
                ContainerUtils.INSTANCE.addOrDrop(player2.getPlayer().getInventory(), List.of(itemStack), player2.getPlayer().getLocation());
            });
            final String itemName = Utils.getFormattedItemName(itemStack);
            int itemAm = itemStack.getAmount();
            player1Items.add(itemAm + "x " + itemName);
            if (CONFIG.getBoolean("enable-trade-summaries")) {
                MESSAGEUTILS.sendFormatted(player1.getPlayer(), LANG.getString("summary.give.item"), Map.of("%amount%", "" + itemAm, "%item%", itemName));
                MESSAGEUTILS.sendFormatted(player2.getPlayer(), LANG.getString("summary.get.item"), Map.of("%amount%", "" + itemAm, "%item%", itemName));
            }
        });

        List<String> player2Items = new ArrayList<>();
        player2.getTradeGui().getItems().forEach(itemStack -> {
            if (itemStack == null) return;
            Scheduler.get().runAt(player1.getPlayer().getLocation(), task -> {
                ContainerUtils.INSTANCE.addOrDrop(player1.getPlayer().getInventory(), List.of(itemStack), player1.getPlayer().getLocation());
            });
            final String itemName = Utils.getFormattedItemName(itemStack);
            int itemAm = itemStack.getAmount();
            player2Items.add(itemAm + "x " + itemName);
            if (CONFIG.getBoolean("enable-trade-summaries")) {
                MESSAGEUTILS.sendFormatted(player2.getPlayer(), LANG.getString("summary.give.item"), Map.of("%amount%", "" + itemAm, "%item%", itemName));
                MESSAGEUTILS.sendFormatted(player1.getPlayer(), LANG.getString("summary.get.item"), Map.of("%amount%", "" + itemAm, "%item%", itemName));
            }
        });

        HistoryUtils.writeToHistory(
                String.format("%s: [Currencies: %s] [Items: %s] | %s: [Currencies: %s] [Items: %s]",
        player1.getPlayer().getName(), player1Currencies.isEmpty() ? "---" : String.join(", ", player1Currencies), player1Items.isEmpty() ? "---" : String.join(", ", player1Items), player2.getPlayer().getName(), player2Currencies.isEmpty() ? "---" : String.join(", ", player2Currencies), player2Items.isEmpty() ? "---" : String.join(", ", player2Items)));

        end();
    }

    public long getPrepTime() {
        return prepTime;
    }

    public TradePlayer getPlayer1() {
        return player1;
    }

    public TradePlayer getPlayer2() {
        return player2;
    }

    public Player getOtherPlayer(Player player) {
        return player1.getPlayer().equals(player) ? player2.getPlayer() : player1.getPlayer();
    }

    public boolean isEnded() {
        return ended;
    }
}
