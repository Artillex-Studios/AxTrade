package com.artillexstudios.axtrade.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;
import static com.artillexstudios.axtrade.AxTrade.TOGGLED;

public enum Toggle {
    INSTANCE;

    public void execute(CommandSender sender, Player other) {
        if (other != null) sender = other;
        if (!(sender instanceof Player player)) {
            throw new SenderNotPlayerException();
        }
        boolean toggled = TOGGLED.getBoolean("toggled." + player.getUniqueId(), false);
        if (toggled) {
            TOGGLED.getBackingDocument().remove("toggled." + player.getUniqueId());
            MESSAGEUTILS.sendLang(player, "toggle.enabled");
        } else {
            TOGGLED.set("toggled." + player.getUniqueId(), true);
            MESSAGEUTILS.sendLang(player, "toggle.disabled");
        }
        TOGGLED.save();
    }
}
