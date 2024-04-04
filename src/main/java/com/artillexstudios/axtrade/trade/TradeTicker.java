package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;

import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class TradeTicker {

    public void start() {
        Scheduler.get().runTimer(scheduledTask -> {
            for (Trade trade : Trades.getTrades()) {
                if (!(trade.player1.hasConfirmed() && trade.player2.hasConfirmed())) continue;

                if (trade.player1.getConfirmed() == 1) {
                    MESSAGEUTILS.sendLang(trade.player1.getPlayer(), "trade-completed", Map.of("%player%", trade.player2.getPlayer().getName()));
                    MESSAGEUTILS.sendLang(trade.player2.getPlayer(), "trade-completed", Map.of("%player%", trade.player1.getPlayer().getName()));
                    // todo: transfer items
                    trade.end();
                    continue;
                }

                trade.player1.tick();
                trade.player2.tick();

            }
        }, 20, 20);
    }
}
