package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.utils.SoundUtils;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Trade {
    protected final TradePlayer player1;
    protected final TradePlayer player2;
    protected boolean ended = false;
    protected long prepTime = System.currentTimeMillis();

    public Trade(Player p1, Player p2) {
        this.player1 = new TradePlayer(this, p1);
        this.player2 = new TradePlayer(this, p2);
        player1.setOtherPlayer(player2);
        player2.setOtherPlayer(player1);
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

        for (Map.Entry<CurrencyHook, Double> entry : player1.getCurrencies().entrySet()) {
            entry.getKey().takeBalance(player1.getPlayer().getUniqueId(), entry.getValue());
            entry.getKey().giveBalance(player2.getPlayer().getUniqueId(), entry.getValue());
        }

        for (Map.Entry<CurrencyHook, Double> entry : player2.getCurrencies().entrySet()) {
            entry.getKey().takeBalance(player2.getPlayer().getUniqueId(), entry.getValue());
            entry.getKey().giveBalance(player1.getPlayer().getUniqueId(), entry.getValue());
        }

        MESSAGEUTILS.sendLang(player1.getPlayer(), "trade.completed", Map.of("%player%", player2.getPlayer().getName()));
        MESSAGEUTILS.sendLang(player2.getPlayer(), "trade.completed", Map.of("%player%", player1.getPlayer().getName()));
        SoundUtils.playSound(player1.getPlayer(), "completed");
        SoundUtils.playSound(player2.getPlayer(), "completed");

        player1.getTradeGui().getItems().forEach(itemStack -> {
            if (itemStack == null) return;
            player2.getPlayer().getInventory().addItem(itemStack);
        });
        player2.getTradeGui().getItems().forEach(itemStack -> {
            if (itemStack == null) return;
            player1.getPlayer().getInventory().addItem(itemStack);
        });

        end();
    }

    public long getPrepTime() {
        return prepTime;
    }
}
