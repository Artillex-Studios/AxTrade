package com.artillexstudios.axtrade.trade;

import org.bukkit.entity.Player;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class TradePlayer {
    private final Player player;
    private TradePlayer otherPlayer;
    private TradeGui tradeGui;
    private final Trade trade;

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
    }

    public void cancel() {
        this.confirmed = null;
        otherPlayer.setConfirmed(null);
        trade.update();
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public void tick() {
        confirmed -= 1;
        trade.update();
    }
}
