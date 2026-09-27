package com.artillexstudios.axtrade.commands;

import com.artillexstudios.axtrade.AxTrade;
import com.artillexstudios.axtrade.utils.CommandMessages;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.dynamic.Annotations;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.orphan.Orphans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.LANG;

public class CommandManager {
    private static BukkitCommandHandler handler = null;

    public static void load() {
        handler = BukkitCommandHandler.create(AxTrade.getInstance());

        handler.getTranslator().add(new CommandMessages());
        handler.setLocale(Locale.of("en", "US"));

        handler.registerAnnotationReplacer(Subcommand.class, (element, subcommand) -> {
            List<String> values = new ArrayList<>();
            for (String sub : subcommand.value()) {
                List<String> list = LANG.getStringList("subcommands." + sub);
                if (list != null && !list.isEmpty()) {
                    values.addAll(list);
                } else {
                    String single = LANG.getString("subcommands." + sub);
                    if (single != null && !single.isBlank()) {
                        values.add(single);
                    } else {
                        values.add(sub);
                    }
                }
            }
            if (values.isEmpty()) {
                return Collections.singletonList(subcommand);
            }
            return Collections.singletonList(Annotations.create(Subcommand.class, "value", values.toArray(String[]::new)));
        });

        reload();
    }

    public static void reload() {
        handler.unregisterAllCommands();

        List<String> aliases = CONFIG.getStringList("command-aliases");
        if (!aliases.isEmpty()) {
            handler.register(Orphans.path(aliases.toArray(String[]::new)).handler(new Commands()));
        }

        handler.registerBrigadier();
    }

    public static String getSubcommand(String key, String fallback) {
        List<String> list = LANG.getStringList("subcommands." + key);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        String single = LANG.getString("subcommands." + key);
        if (single != null && !single.isBlank()) {
            return single;
        }
        return fallback;
    }
}
