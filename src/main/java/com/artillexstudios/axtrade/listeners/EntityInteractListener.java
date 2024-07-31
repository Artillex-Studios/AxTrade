package com.artillexstudios.axtrade.listeners;

import com.artillexstudios.axtrade.commands.Commands;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class EntityInteractListener implements Listener {
    private static final HashMap<Player, Long> cd = new HashMap<>();

    @EventHandler (ignoreCancelled = true)
    public void onClick(@NotNull PlayerInteractEntityEvent event) {
        if (!CONFIG.getBoolean("shift-click-send-request", true)) return;
        final Player player = event.getPlayer();
        if (!player.hasPermission("axtrade.trade")) return;

        if (cd.containsKey(player) && System.currentTimeMillis() - cd.get(player) < 100L) return;

        if (!player.isSneaking()) return;
        if (!(event.getRightClicked() instanceof Player sendTo)) return;

        cd.put(player, System.currentTimeMillis());
        if (!sendTo.isOnline()) return;

        new Commands().trade(player, sendTo);
    }

    public static void onQuit(Player player) {
        cd.remove(player);
    }
}
