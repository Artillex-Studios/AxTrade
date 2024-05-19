package com.artillexstudios.axtrade.listeners;

import com.artillexstudios.axtrade.commands.Commands;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class PlayerInteractEntityListener implements Listener {
    private final WeakHashMap<Player, Long> cd = new WeakHashMap<>();

    @EventHandler (ignoreCancelled = true)
    public void onClick(@NotNull PlayerInteractEntityEvent event) {
        if (!CONFIG.getBoolean("shift-click-send-request", true)) return;
        final Player player = event.getPlayer();

        if (cd.containsKey(player) && System.currentTimeMillis() - cd.get(player) < 100L) return;

        if (!player.isSneaking()) return;
        if (!(event.getRightClicked() instanceof Player)) return;

        cd.put(player, System.currentTimeMillis());
        final Player sendTo = (Player) event.getRightClicked();
        if (!sendTo.isOnline()) return;

        new Commands().trade(player, sendTo);
    }
}
