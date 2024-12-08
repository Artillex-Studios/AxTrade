package com.artillexstudios.axtrade.commands;

import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.AxTrade;
import com.artillexstudios.axtrade.hooks.HookManager;
import com.artillexstudios.axtrade.lang.LanguageManager;
import com.artillexstudios.axtrade.request.Requests;
import com.artillexstudios.axtrade.trade.Trades;
import com.artillexstudios.axtrade.utils.CommandMessages;
import com.artillexstudios.axtrade.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.orphan.OrphanCommand;
import revxrsal.commands.orphan.Orphans;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.GUIS;
import static com.artillexstudios.axtrade.AxTrade.HOOKS;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;
import static com.artillexstudios.axtrade.AxTrade.TOGGLED;

@CommandPermission(value = "axtrade.trade")
public class Commands implements OrphanCommand {

    public void help(@NotNull CommandSender sender) {
        if (sender.hasPermission("axtrade.admin")) {
            for (String m : LANG.getStringList("admin-help")) {
                sender.sendMessage(StringUtils.formatToString(m));
            }
        } else {
            for (String m : LANG.getStringList("player-help")) {
                sender.sendMessage(StringUtils.formatToString(m));
            }
        }
    }

    @DefaultFor({"~"})
    public void trade(@NotNull Player sender, @Optional Player other) {
        if (other == null) {
            help(sender);
            return;
        }

        Requests.addRequest(sender, other);
    }

    @Subcommand("accept")
    public void accept(@NotNull Player sender, @NotNull Player other) {
        var request = Requests.getRequest(sender, other);
        if (request == null || request.getSender().equals(sender) || !request.isActive()) {
            MESSAGEUTILS.sendLang(sender, "request.no-request", Map.of("%player%", other.getName()));
            return;
        }

        Requests.addRequest(sender, other);
    }

    @Subcommand("deny")
    public void deny(@NotNull Player sender, @NotNull Player other) {
        var request = Requests.getRequest(sender, other);
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

    @CommandPermission("axtrade.toggle")
    @Subcommand("toggle")
    public void toggle(@NotNull Player sender) {
        boolean toggled = TOGGLED.getBoolean("toggled." + sender.getUniqueId(), false);
        if (toggled) {
            TOGGLED.getBackingDocument().remove("toggled." + sender.getUniqueId());
            MESSAGEUTILS.sendLang(sender, "toggle.enabled");
        } else {
            TOGGLED.set("toggled." + sender.getUniqueId(), true);
            MESSAGEUTILS.sendLang(sender, "toggle.disabled");
        }
        TOGGLED.save();
    }

    @Subcommand("reload")
    @CommandPermission(value = "axtrade.admin")
    public void reload(@NotNull CommandSender sender) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD[AxTrade] &#AAFFDDReloading configuration..."));
        if (!CONFIG.reload()) {
            MESSAGEUTILS.sendLang(sender, "reload.failed", Map.of("%file%", "config.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╠ &#AAFFDDReloaded &fconfig.yml&#AAFFDD!"));

        if (!LANG.reload()) {
            MESSAGEUTILS.sendLang(sender, "reload.failed", Map.of("%file%", "lang.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╠ &#AAFFDDReloaded &flang.yml&#AAFFDD!"));

        if (!GUIS.reload()) {
            MESSAGEUTILS.sendLang(sender, "reload.failed", Map.of("%file%", "guis.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╠ &#AAFFDDReloaded &fguis.yml&#AAFFDD!"));

        if (!HOOKS.reload()) {
            MESSAGEUTILS.sendLang(sender, "reload.failed", Map.of("%file%", "currencies.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╠ &#AAFFDDReloaded &fcurrencies.yml&#AAFFDD!"));

        LanguageManager.reload();

        new HookManager().updateHooks();
        NumberUtils.reload();

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╚ &#AAFFDDSuccessful reload!"));
        MESSAGEUTILS.sendLang(sender, "reload.success");
    }

    @Subcommand("force")
    @CommandPermission(value = "axtrade.admin")
    public void force(@NotNull Player sender, Player other) {
        if (sender.equals(other)) {
            MESSAGEUTILS.sendLang(sender, "request.cant-trade-self");
            return;
        }
        Trades.addTrade(sender, other);
    }

//    @Subcommand("test")
//    @CommandPermission(value = "axtrade.admin")
//    public void test(@NotNull Player sender) {
//        Trades.addTrade(sender, sender);
//    }

    private static BukkitCommandHandler handler = null;
    public static void registerCommand() {
        if (handler == null) {
            handler = BukkitCommandHandler.create(AxTrade.getInstance());

            handler.registerValueResolver(0, OfflinePlayer.class, context -> {
                String value = context.pop();
                if (value.equalsIgnoreCase("self") || value.equalsIgnoreCase("me"))
                    return ((BukkitCommandActor) context.actor()).requirePlayer();
                OfflinePlayer player = NMSHandlers.getNmsHandler().getCachedOfflinePlayer(value);
                if (player == null && !(player = Bukkit.getOfflinePlayer(value)).hasPlayedBefore())
                    throw new InvalidPlayerException(context.parameter(), value);
                return player;
            });

            handler.getAutoCompleter().registerParameterSuggestions(OfflinePlayer.class, (args, sender, command) -> {
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toSet());
            });

            handler.getTranslator().add(new CommandMessages());
            handler.setLocale(new Locale("en", "US"));
        }
        handler.unregisterAllCommands();
        handler.register(Orphans.path(CONFIG.getStringList("command-aliases").toArray(String[]::new)).handler(new Commands()));
        handler.registerBrigadier();
    }
}
