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
        final Map<String, String> replacements = Map.of("%player%", receiver.getName());

        var disallowed = CONFIG.getStringList("disallowed-gamemodes");
        if (disallowed.contains(sender.getGameMode().name()) || disallowed.contains(receiver.getGameMode().name())) {
            MESSAGEUTILS.sendLang(sender, "request.disallowed-gamemode", replacements);
            return;
        }

        var blacklisted = CONFIG.getStringList("blacklisted-worlds");
        if (blacklisted.contains(sender.getWorld().getName()) || blacklisted.contains(receiver.getWorld().getName())) {
            MESSAGEUTILS.sendLang(sender, "request.blacklisted-world", replacements);
            return;
        }

        if (sender.isDead() || receiver.isDead()) {
            MESSAGEUTILS.sendLang(sender, "request.not-accepting", replacements);
            return;
        }

        if (sender.equals(receiver)) {
            MESSAGEUTILS.sendLang(sender, "request.cant-trade-self", replacements);
            return;
        }

        if (Trades.isTrading(receiver) || Trades.isTrading(sender)) {
            MESSAGEUTILS.sendLang(sender, "request.already-in-trade", replacements);
            return;
        }

        int maxDist = CONFIG.getInt("trade-max-distance", 10);
        if (maxDist != -1 && (sender.getWorld() != receiver.getWorld() || maxDist < sender.getLocation().distance(receiver.getLocation()))) {
            MESSAGEUTILS.sendLang(sender, "request.too-far", replacements);
            return;
        }

        var request = Requests.getRequest(sender, receiver);
        if (request != null && !request.getSender().equals(sender)) {
            Trades.addTrade(sender, receiver);
            requests.remove(request);
            return;
        }

        if (request != null && System.currentTimeMillis() - request.getTime() < CONFIG.getInt("trade-request-expire-seconds", 60) * 1_000L) {
            MESSAGEUTILS.sendLang(sender, "request.already-sent", replacements);
            return;
        }

        requests.add(new Request(sender, receiver));

        MESSAGEUTILS.sendLang(sender, "request.sent-sender", replacements);
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