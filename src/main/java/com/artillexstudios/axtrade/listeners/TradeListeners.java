package com.artillexstudios.axtrade.listeners;

import com.artillexstudios.axtrade.trade.Trade;
import com.artillexstudios.axtrade.trade.Trades;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class TradeListeners implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        trade.abort();
    }

    @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        if (System.currentTimeMillis() - trade.getPrepTime() < 1_000L) return;
        trade.abort();
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        event.setCancelled(true);
        trade.abort();
    }

    @EventHandler
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        event.setCancelled(true);
        trade.abort();
    }
}
