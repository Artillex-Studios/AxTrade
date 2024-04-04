package com.artillexstudios.axtrade.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.AxTrade;
import com.artillexstudios.axtrade.trade.Trades;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;
import revxrsal.commands.orphan.Orphans;

import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.GUIS;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Commands implements OrphanCommand {

    @DefaultFor({"~", "~ help"})
    public void help(@NotNull CommandSender sender) {
        if (!sender.hasPermission("axtrade.admin")) {
            for (String m : LANG.getStringList("player-help")) {
                sender.sendMessage(StringUtils.formatToString(m));
            }
        } else {
            for (String m : LANG.getStringList("admin-help")) {
                sender.sendMessage(StringUtils.formatToString(m));
            }
        }
    }

    @Subcommand("reload")
    @CommandPermission(value = "axtrade.admin", defaultAccess = PermissionDefault.OP)
    public void reload(@NotNull CommandSender sender) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD[AxTrade] &#AAFFDDReloading configuration..."));
        if (!CONFIG.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "config.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╠ &#AAFFDDReloaded &fconfig.yml&#AAFFDD!"));

        if (!LANG.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "lang.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╠ &#AAFFDDReloaded &flang.yml&#AAFFDD!"));

        if (!GUIS.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "guis.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╠ &#AAFFDDReloaded &fguis.yml&#AAFFDD!"));

        Commands.registerCommand();

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╚ &#AAFFDDSuccessful reload!"));
        MESSAGEUTILS.sendLang(sender, "reload.success");
    }

    @Subcommand("force")
    @CommandPermission(value = "axtrade.admin", defaultAccess = PermissionDefault.OP)
    public void force(@NotNull Player sender, Player other) {
        Trades.addTrade(sender, other);
    }

    public static void registerCommand() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(AxTrade.getInstance());
        handler.unregisterAllCommands();
        handler.register(Orphans.path(CONFIG.getStringList("command-aliases").toArray(String[]::new)).handler(new Commands()));
        handler.registerBrigadier();
    }
}
