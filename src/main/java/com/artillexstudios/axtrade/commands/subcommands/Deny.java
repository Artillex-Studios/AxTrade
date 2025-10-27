package com.artillexstudios.axtrade.commands.subcommands;

import com.artillexstudios.axtrade.request.Request;
import com.artillexstudios.axtrade.request.Requests;
import com.artillexstudios.axtrade.utils.SoundUtils;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public enum Deny {
    INSTANCE;

    public void execute(Player sender, Player other) {
        Request request = Requests.getRequest(sender, other);
        if (request == null || request.getSender().equals(sender) || !request.isActive()) {
            MESSAGEUTILS.sendLang(sender, "request.no-request", Map.of("%player%", other.getName()));
            return;
        }

        request.deactivate();
        MESSAGEUTILS.sendLang(request.getSender(), "request.deny-sender", Map.of("%player%", request.getReceiver().getName()));
        MESSAGEUTILS.sendLang(request.getReceiver(), "request.deny-receiver", Map.of("%player%", request.getSender().getName()));
        SoundUtils.playSound(request.getSender(), "deny");
        SoundUtils.playSound(request.getReceiver(), "deny");
    }
}
