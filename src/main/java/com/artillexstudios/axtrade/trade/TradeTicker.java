package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;

public class TradeTicker {

    public void start() {
        Scheduler.get().runTimer(scheduledTask -> {
            for (Trade trade : Trades.getTrades()) {
                if (!(trade.player1.hasConfirmed() && trade.player2.hasConfirmed())) continue;

                if (trade.player1.getConfirmed() == 1) {
                    trade.complete();
                    continue;
                }

                trade.player1.tick();
                trade.player2.tick();

            }
        }, 20, 20);
    }
}
