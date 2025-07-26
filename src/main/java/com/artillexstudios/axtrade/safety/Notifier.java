package com.artillexstudios.axtrade.safety;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.AxTrade;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Notifier implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (SafetyManager.isSafe()) return;
        if (!event.getPlayer().hasPermission("*") && !event.getPlayer().isOp()) return;
        event.getPlayer().sendMessage(StringUtils.formatToString("\n&#FF6600⚠ &#FF0000[%s] We have disabled some (or all) features for safety reasons!\n&#DDDDDDThis is done to protect your server, please update the plugin to resolve this issue.\n".formatted(AxTrade.getInstance().getName())));
    }

    public void sendAlert() {
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("\n&#FF6600⚠ &#FF0000[%s] We have disabled some (or all) features for safety reasons!\n&#DDDDDDThis is done to protect your server, please update the plugin to resolve this issue.\n".formatted(AxTrade.getInstance().getName())));
    }
}
