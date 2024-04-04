package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Trade {
    protected final TradePlayer player1;
    protected final TradePlayer player2;
    private boolean ended = false;

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
        // todo: refund items
        MESSAGEUTILS.sendLang(player1.getPlayer(), "trade-aborted", Map.of("%player%", player2.getPlayer().getName()));
        MESSAGEUTILS.sendLang(player2.getPlayer(), "trade-aborted", Map.of("%player%", player1.getPlayer().getName()));
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
}
