package com.artillexstudios.axtrade.commands.subcommands;

import org.bukkit.entity.Player;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;
import static com.artillexstudios.axtrade.AxTrade.TOGGLED;

public enum Toggle {
    INSTANCE;

    public void execute(Player sender) {
        boolean toggled = TOGGLED.getBoolean("toggled." + sender.getUniqueId(), false);
        if (toggled) {
            TOGGLED.getBackingDocument().remove("toggled." + sender.getUniqueId());
            MESSAGEUTILS.sendLang(sender, "toggle.enabled");
        } else {
            TOGGLED.set("toggled." + sender.getUniqueId(), true);
            MESSAGEUTILS.sendLang(sender, "toggle.disabled");
        }
        TOGGLED.save();
    }
}
