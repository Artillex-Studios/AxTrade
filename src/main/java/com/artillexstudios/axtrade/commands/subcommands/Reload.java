package com.artillexstudios.axtrade.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.hooks.HookManager;
import com.artillexstudios.axtrade.lang.LanguageManager;
import com.artillexstudios.axtrade.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.GUIS;
import static com.artillexstudios.axtrade.AxTrade.HOOKS;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public enum Reload {
    INSTANCE;

    public void execute(CommandSender sender) {
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

        HookManager.updateHooks();
        NumberUtils.reload();

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD╚ &#AAFFDDSuccessful reload!"));
        MESSAGEUTILS.sendLang(sender, "reload.success");
    }
}
