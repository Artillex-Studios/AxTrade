package com.artillexstudios.axtrade.api.events;

import com.artillexstudios.axtrade.trade.Trade;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AxTradeCompleteEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Trade trade;
    private boolean isCancelled = false;

    public AxTradeCompleteEvent(Trade trade) {
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

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Trade getTrade() {
        return trade;
    }
}
