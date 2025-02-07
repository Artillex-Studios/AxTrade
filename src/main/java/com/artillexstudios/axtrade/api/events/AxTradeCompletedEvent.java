package com.artillexstudios.axtrade.api.events;

import com.artillexstudios.axtrade.trade.TradePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AxTradeCompletedEvent extends Event implements Cancellable {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  private final TradePlayer firstPlayer;
  private final TradePlayer secondPlayer;
  private boolean isCancelled = false;

  public AxTradeCompletedEvent(TradePlayer firstPlayer, TradePlayer secondPlayer) {
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean b) {
    isCancelled = b;
  }

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public TradePlayer getFirstPlayer() {
    return this.firstPlayer;
  }

  public TradePlayer getSecondPlayer() {
    return this.secondPlayer;
  }
}
