package com.artillexstudios.axtrade.utils;

import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.items.component.DataComponents;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NBTUtils {

    public static void writeToNBT(@NotNull ItemStack item, @NotNull String namespace, @NotNull String content) {
        WrappedItemStack.edit(item, wrappedItemStack -> {
            var customData = wrappedItemStack.get(DataComponents.customData());
            customData.putString(namespace, content);
            wrappedItemStack.set(DataComponents.customData(), customData);
            return wrappedItemStack;
        });
    }

    public static void writeToNBT(@NotNull ItemStack item, @NotNull String namespace, int content) {
        WrappedItemStack.edit(item, wrappedItemStack -> {
            var customData = wrappedItemStack.get(DataComponents.customData());
            customData.putInt(namespace, content);
            wrappedItemStack.set(DataComponents.customData(), customData);
            return wrappedItemStack;
        });
    }

    public static void writeToNBT(@NotNull ItemStack item, @NotNull String namespace, boolean content) {
        WrappedItemStack.edit(item, wrappedItemStack -> {
            var customData = wrappedItemStack.get(DataComponents.customData());
            customData.putBoolean(namespace, content);
            wrappedItemStack.set(DataComponents.customData(), customData);
            return wrappedItemStack;
        });
    }

    public static void writeToNBT(@NotNull ItemStack item, @NotNull String namespace, double content) {
        WrappedItemStack.edit(item, wrappedItemStack -> {
            var customData = wrappedItemStack.get(DataComponents.customData());
            customData.putDouble(namespace, content);
            wrappedItemStack.set(DataComponents.customData(), customData);
            return wrappedItemStack;
        });
    }

    public static void writeToNBT(@NotNull ItemStack item, @NotNull String namespace, long content) {
        WrappedItemStack.edit(item, wrappedItemStack -> {
            var customData = wrappedItemStack.get(DataComponents.customData());
            customData.putLong(namespace, content);
            wrappedItemStack.set(DataComponents.customData(), customData);
            return wrappedItemStack;
        });
    }

    public static void writeToNBT(@NotNull ItemStack item, @NotNull String namespace, float content) {
        WrappedItemStack.edit(item, wrappedItemStack -> {
            var customData = wrappedItemStack.get(DataComponents.customData());
            customData.putFloat(namespace, content);
            wrappedItemStack.set(DataComponents.customData(), customData);
            return wrappedItemStack;
        });
    }

    public static void writeToNBT(@NotNull ItemStack item, @NotNull String namespace, UUID content) {
        WrappedItemStack.edit(item, wrappedItemStack -> {
            var customData = wrappedItemStack.get(DataComponents.customData());
            customData.putUUID(namespace, content);
            wrappedItemStack.set(DataComponents.customData(), customData);
            return wrappedItemStack;
        });
    }

    public static boolean containsNBT(@NotNull ItemStack item, @NotNull String namespace) {
        return WrappedItemStack.edit(item, wrappedItemStack -> {
            return wrappedItemStack.get(DataComponents.customData()).contains(namespace);
        });
    }

    @Nullable
    public static String readStringFromNBT(@NotNull ItemStack item, @NotNull String namespace) {
        return WrappedItemStack.edit(item, wrappedItemStack -> {
            final String str = wrappedItemStack.get(DataComponents.customData()).getString(namespace);
            return str.isEmpty() ? null : str;
        });
    }

    public static int readIntegerFromNBT(@NotNull ItemStack item, @NotNull String namespace) {
        return WrappedItemStack.edit(item, wrappedItemStack -> {
            return wrappedItemStack.get(DataComponents.customData()).getInt(namespace);
        });
    }

    public static float readFloatFromNBT(@NotNull ItemStack item, @NotNull String namespace) {
        return WrappedItemStack.edit(item, wrappedItemStack -> {
            return wrappedItemStack.get(DataComponents.customData()).getFloat(namespace);
        });
    }

    public static long readLongFromNBT(@NotNull ItemStack item, @NotNull String namespace) {
        return WrappedItemStack.edit(item, wrappedItemStack -> {
            return wrappedItemStack.get(DataComponents.customData()).getLong(namespace);
        });
    }

    public static double readDoubleFromNBT(@NotNull ItemStack item, @NotNull String namespace) {
        return WrappedItemStack.edit(item, wrappedItemStack -> {
            return wrappedItemStack.get(DataComponents.customData()).getDouble(namespace);
        });
    }

    @Nullable
    public static UUID readUUIDFromNBT(@NotNull ItemStack item, @NotNull String namespace) {
        return WrappedItemStack.edit(item, wrappedItemStack -> {
            return wrappedItemStack.get(DataComponents.customData()).getUUID(namespace);
        });
    }

    public static boolean readBooleanFromNBT(@NotNull ItemStack item, @NotNull String namespace) {
        return WrappedItemStack.edit(item, wrappedItemStack -> {
            return wrappedItemStack.get(DataComponents.customData()).getBoolean(namespace);
        });
    }
}
