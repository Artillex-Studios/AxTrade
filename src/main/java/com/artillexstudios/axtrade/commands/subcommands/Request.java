package com.artillexstudios.axtrade.commands.subcommands;

import com.artillexstudios.axtrade.request.Requests;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public enum Request {
    INSTANCE;

    public void execute(Player sender, @Nullable Player other) {
        if (other == null) {
            Help.INSTANCE.execute(sender);
            return;
        }

        Requests.addRequest(sender, other);
    }
}
