package com.artillexstudios.axtrade.commands.subcommands;

import com.artillexstudios.axtrade.request.Request;
import com.artillexstudios.axtrade.request.Requests;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public enum Accept {
    INSTANCE;

    public void execute(Player sender, Player other) {
        Request request = Requests.getRequest(sender, other);
        if (request == null || request.getSender().equals(sender) || !request.isActive()) {
            MESSAGEUTILS.sendLang(sender, "request.no-request", Map.of("%player%", other.getName()));
            return;
        }

        Requests.addRequest(sender, other);
    }
}
