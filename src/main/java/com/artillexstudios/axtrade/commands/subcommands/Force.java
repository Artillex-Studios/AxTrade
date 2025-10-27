package com.artillexstudios.axtrade.commands.subcommands;

import com.artillexstudios.axtrade.trade.Trades;
import org.bukkit.entity.Player;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public enum Force {
    INSTANCE;

    public void execute(Player sender, Player other) {
        if (sender.equals(other)) {
            MESSAGEUTILS.sendLang(sender, "request.cant-trade-self");
            return;
        }
        Trades.addTrade(sender, other);
    }
}
