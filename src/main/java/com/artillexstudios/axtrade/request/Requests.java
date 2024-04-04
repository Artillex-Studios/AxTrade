package com.artillexstudios.axtrade.request;

import com.artillexstudios.axtrade.trade.Trades;
import com.artillexstudios.axtrade.utils.SoundUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Requests {
    private static final ArrayList<Request> requests = new ArrayList<>();

    public static void addRequest(@NotNull Player sender, @NotNull Player receiver) {
        if (sender.isDead() || receiver.isDead()) {
            MESSAGEUTILS.sendLang(sender, "request.not-accepting", Map.of("%player%", receiver.getName()));
            return;
        }

        if (sender.equals(receiver)) {
            MESSAGEUTILS.sendLang(sender, "request.cant-trade-self", Map.of("%player%", receiver.getName()));
            return;
        }

        if (Trades.isTrading(receiver) || Trades.isTrading(sender)) {
            MESSAGEUTILS.sendLang(sender, "request.already-in-trade", Map.of("%player%", receiver.getName()));
            return;
        }

        int maxDist = CONFIG.getInt("trade-max-distance", 10);
        if (maxDist != -1 && (sender.getWorld() != receiver.getWorld() || maxDist < sender.getLocation().distance(receiver.getLocation()))) {
            MESSAGEUTILS.sendLang(sender, "request.too-far", Map.of("%player%", receiver.getName()));
            return;
        }

        var request = Requests.getRequest(sender, receiver);
        if (request != null && !request.getSender().equals(sender)) {
            Trades.addTrade(sender, receiver);
            requests.remove(request);
            return;
        }

        if (request != null && System.currentTimeMillis() - request.getTime() < CONFIG.getInt("trade-request-expire-seconds", 60) * 1_000L) {
            MESSAGEUTILS.sendLang(sender, "request.already-sent", Map.of("%player%", receiver.getName()));
            return;
        }

        requests.add(new Request(sender, receiver));

        MESSAGEUTILS.sendLang(sender, "request.sent-sender", Map.of("%player%", receiver.getName()));
        MESSAGEUTILS.sendLang(receiver, "request.sent-receiver", Map.of("%player%", sender.getName()));
        SoundUtils.playSound(sender, "requested");
        SoundUtils.playSound(receiver, "requested");
    }

    public static boolean hasRequest(Player p1, Player p2) {
        for (Request rq : requests) {
            if (!((rq.getSender() == p1 || rq.getSender() == p2) && (rq.getReceiver() == p1 || rq.getReceiver() == p2))) continue;
            return true;
        }

        return false;
    }

    @Nullable
    public static Request getRequest(Player p1, Player p2) {
        for (Request rq : requests) {
            if (!((rq.getSender() == p1 || rq.getSender() == p2) && (rq.getReceiver() == p1 || rq.getReceiver() == p2))) continue;
            return rq;
        }

        return null;
    }
}