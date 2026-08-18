package com.artillexstudios.axtrade.commands;

import com.artillexstudios.axtrade.AxTrade;
import com.artillexstudios.axtrade.utils.CommandMessages;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.orphan.Orphans;

import java.util.List;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class CommandManager {
    private static BukkitCommandHandler handler = null;

    public static void load() {
        handler = BukkitCommandHandler.create(AxTrade.getInstance());

        handler.getTranslator().add(new CommandMessages());
        handler.setLocale(AxTrade.getConfiguredLocale());

        reload();
    }

    public static void reload() {
        handler.unregisterAllCommands();
        handler.setLocale(AxTrade.getConfiguredLocale());

        List<String> aliases = CONFIG.getStringList("command-aliases");
        if (!aliases.isEmpty()) {
            handler.register(Orphans.path(aliases.toArray(String[]::new)).handler(new Commands()));
        }

        handler.registerBrigadier();
    }
}
