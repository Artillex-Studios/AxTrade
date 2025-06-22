package com.artillexstudios.axtrade.api.events;

import com.artillexstudios.axtrade.trade.Trade;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AxTradeAbortEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Trade trade;

    public AxTradeAbortEvent(Trade trade) {
        super(!Bukkit.isPrimaryThread());
        this.trade = trade;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Trade getTrade() {
        return trade;
    }
}
