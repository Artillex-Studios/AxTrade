package com.artillexstudios.axtrade.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.trade.Trade;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public enum Preview {
    INSTANCE;

    public void execute(Player sender) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Opening trade preview for " + sender.getName() + ", any errors under this should be ignored!"));
        new Trade(sender, sender);
        MESSAGEUTILS.sendLang(sender, "trade.preview-info");
    }
}
