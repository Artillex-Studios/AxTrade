package com.artillexstudios.axtrade.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AxTradeRequestEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Player sender;
    private final Player receiver;
    private boolean isCancelled = false;

    public AxTradeRequestEvent(Player sender, Player receiver) {
        super(!Bukkit.isPrimaryThread());
        this.sender = sender;
        this.receiver = receiver;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }
}