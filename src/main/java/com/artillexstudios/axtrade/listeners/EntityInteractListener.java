package com.artillexstudios.axtrade.listeners;

import com.artillexstudios.axapi.utils.Cooldown;
import com.artillexstudios.axtrade.request.Requests;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class EntityInteractListener implements Listener {
    private static final Cooldown<Player> cooldown = Cooldown.create();

    @EventHandler (ignoreCancelled = true)
    public void onInteract(@NotNull PlayerInteractEntityEvent event) {
        if (!CONFIG.getBoolean("shift-click-send-request", true)) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("axtrade.trade")) return;
        if (cooldown.hasCooldown(player)) return;
        if (!player.isSneaking()) return;
        if (!(event.getRightClicked() instanceof Player sendTo)) return;
        if (!sendTo.isOnline()) return;

        cooldown.addCooldown(player, 100L);
        Requests.addRequest(player, sendTo);
        event.setCancelled(true);
    }
}
