package com.artillexstudios.axtrade.request;

import com.artillexstudios.axapi.nms.wrapper.ServerPlayerWrapper;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.api.events.AxTradeRequestEvent;
import com.artillexstudios.axtrade.trade.Trades;
import com.artillexstudios.axtrade.utils.SoundUtils;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;
import static com.artillexstudios.axtrade.AxTrade.TOGGLED;

public class Requests {
    private static final ArrayList<Request> requests = new ArrayList<>();

    public static void addRequest(@NotNull Player sender, @NotNull Player receiver) {
        final Map<String, String> replacements = Map.of("%player%", receiver.getName());

        boolean disallowSameIp = CONFIG.getBoolean("disallow-same-ip-trade", false);
        if (disallowSameIp && sender.getAddress() != null && receiver.getAddress() != null && sender.getAddress().getAddress().equals(receiver.getAddress().getAddress())) {
            MESSAGEUTILS.sendLang(sender, "request.same-ip", replacements);
            return;
        }

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

        if (TOGGLED.getBoolean("toggled." + receiver.getUniqueId())) {
            MESSAGEUTILS.sendLang(sender, "request.not-accepting", replacements);
            return;
        }

        if (TOGGLED.getBoolean("toggled." + sender.getUniqueId())) {
            MESSAGEUTILS.sendLang(sender, "request.disabled-trading", replacements);
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

        final Request request = Requests.getRequest(sender, receiver);
        if (request != null) {
            if (!request.getSender().equals(sender)) {
                Trades.addTrade(sender, receiver);
                requests.remove(request);
                request.deactivate();
                return;
            }
            if (!request.isActive() || System.currentTimeMillis() - request.getTime() < CONFIG.getInt("trade-request-expire-seconds", 60) * 1_000L) {
                MESSAGEUTILS.sendLang(sender, "request.already-sent", replacements);
                return;
            }
        }

        final AxTradeRequestEvent apiEvent = new AxTradeRequestEvent(sender, receiver);
        Bukkit.getPluginManager().callEvent(apiEvent);
        if (apiEvent.isCancelled()) return;

        requests.add(new Request(sender, receiver));

        MESSAGEUTILS.sendLang(sender, "request.sent-sender", replacements);

        Map<String, String> replacements2 = Map.of("%player%", sender.getName());
        if (LANG.getSection("request.sent-receiver") == null) // this is for backwards compatibility
            MESSAGEUTILS.sendLang(receiver, "request.sent-receiver", replacements2);
        else {
            ServerPlayerWrapper receiverWrap = ServerPlayerWrapper.wrap(receiver);
            receiverWrap.message(StringUtils.format(CONFIG.getString("prefix") + LANG.getString("request.sent-receiver.info"), replacements2));
            receiverWrap.message(StringUtils.format(LANG.getString("request.sent-receiver.accept.message"), replacements2)
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, StringUtils.format(LANG.getString("request.sent-receiver.accept.hover"), replacements2)))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/trade accept " + sender.getName())));
            receiverWrap.message(StringUtils.format(LANG.getString("request.sent-receiver.deny.message"), replacements2)
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, StringUtils.format(LANG.getString("request.sent-receiver.deny.hover"), replacements2)))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/trade deny " + sender.getName())));
        }
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

    public static ArrayList<Request> getRequests() {
        return requests;
    }
}