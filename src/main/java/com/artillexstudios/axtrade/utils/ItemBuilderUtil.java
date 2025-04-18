package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ItemBuilderUtil {

    @NotNull
    public static ItemBuilder newBuilder(@NotNull Section section, @Nullable Player player) {
        return newBuilder(section, Map.of(), player);
    }

    @NotNull
    public static ItemBuilder newBuilder(@NotNull Section section, Map<String, String> replacements, @Nullable Player player) {
        final ItemBuilder builder = new ItemBuilder(section);

        section.getOptionalString("name").ifPresent((name) -> {
            if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                name = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, name);
            }
            builder.setName(name, replacements);
        });

        section.getOptionalStringList("lore").ifPresent((lore) -> {
            if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                lore = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, lore);
            }
            builder.setLore(lore, replacements);
        });

        section.getOptionalString("texture").ifPresent((texture) -> {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                texture = texture.replace(entry.getKey(), entry.getValue());
            }
            builder.setTextureValue(texture);
        });

        return builder;
    }

    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder newBuilder(@NotNull ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }
}
