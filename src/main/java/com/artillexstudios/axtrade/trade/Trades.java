package com.artillexstudios.axtrade.trade;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Trades {
    private static final ArrayList<Trade> trades = new ArrayList<>();

    public static void addTrade(Player p1, Player p2) {
        Trade trade = new Trade(p1, p2);
        trades.add(trade);
        MESSAGEUTILS.sendLang(p1, "trade-started", Map.of("%player%", p2.getName()));
        MESSAGEUTILS.sendLang(p2, "trade-started", Map.of("%player%", p1.getName()));
    }

    public static void removeTrade(Trade trade) {
        trades.remove(trade);
    }

    public static ArrayList<Trade> getTrades() {
        return trades;
    }
}
