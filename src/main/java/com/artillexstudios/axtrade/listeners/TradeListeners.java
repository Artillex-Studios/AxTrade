package com.artillexstudios.axtrade.listeners;

import com.artillexstudios.axtrade.request.Request;
import com.artillexstudios.axtrade.request.Requests;
import com.artillexstudios.axtrade.trade.Trade;
import com.artillexstudios.axtrade.trade.Trades;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class TradeListeners implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        handleQuitTrade(event);
        handleQuitRequest(event);
        EntityInteractListener.onQuit(event.getPlayer());
    }

    public void handleQuitTrade(@NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        trade.abort();
    }

    public void handleQuitRequest(@NotNull PlayerQuitEvent event) {
        final Iterator<Request> iterator = Requests.getRequests().iterator();
        while (iterator.hasNext()) {
            Request request = iterator.next();
            if (request.getSender().equals(event.getPlayer())) {
                iterator.remove();
                continue;
            }
            if (request.getReceiver().equals(event.getPlayer())) {
                iterator.remove();
                if (!request.isActive()) continue;
                MESSAGEUTILS.sendLang(request.getSender(), "request.expired", Map.of("%player%", request.getReceiver().getName()));
            }
        }
    }

    @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        if (System.currentTimeMillis() - trade.getPrepTime() < 1_000L) return;
        if (event.getFrom().distanceSquared(event.getTo()) == 0) return;
        trade.abort();
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Trade trade = Trades.getTrade(player);
        if (trade == null) return;
        if (System.currentTimeMillis() - trade.getPrepTime() < 1_000L) return;
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
