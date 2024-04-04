package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axapi.utils.StringUtils;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.artillexstudios.axtrade.AxTrade.BUKKITAUDIENCES;
import static com.artillexstudios.axtrade.AxTrade.LANG;

public class SoundUtils {

    public static void playSound(@NotNull Player player, @Nullable String route) {
        if (route == null) return;
        if (LANG.getString("sounds." + route, "").isBlank()) return;

        try {
            final Sound sound = Sound.sound().type(Key.key(LANG.getString("sounds." + route))).build();
            BUKKITAUDIENCES.player(player).playSound(sound);
        } catch (InvalidKeyException ex) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FFFFAA[AxAuctions] The sound %sound% does not exist, section: %section%!".replace("%sound%", LANG.getString("sounds." + route)).replace("%section%", route)));
        }
    }
}
