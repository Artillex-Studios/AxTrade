package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.ItemBuilderUtil;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiFrame {
    private static final ItemStack air = new ItemStack(Material.AIR);
    protected final Config file;
    protected BaseGui gui;
    protected Player player;

    public GuiFrame(Config file, Player player) {
        this.file = file;
        this.player = player;
    }

    public void setGui(BaseGui gui) {
        this.gui = gui;
        for (String str : file.getBackingDocument().getRoutesAsStrings(false)) createItem(str);
    }

    @NotNull
    public Config getFile() {
        return file;
    }

    protected ItemStack buildItem(@NotNull String key) {
        if (file.getSection(key) == null) return air;
        return ItemBuilderUtil.newBuilder(file.getSection(key), player).get();
    }

    protected ItemStack buildItem(@NotNull String key, Map<String, String> replacements) {
        if (file.getSection(key) == null) return air;
        return ItemBuilderUtil.newBuilder(file.getSection(key), replacements, player).get();
    }

    protected void createItem(@NotNull String route) {
        createItem(route, event -> event.setCancelled(true), Map.of());
    }

    protected void createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action) {
        createItem(route, action, Map.of());
    }

    protected void createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements) {
        createItem(route + ".slot", route, action, replacements);
    }

    protected void createItem(@NotNull String slotRoute, String itemRoute, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements) {
        createItem(slotRoute, itemRoute, action, replacements, 1);
    }

    protected void createItem(@NotNull String slotRoute, String itemRoute, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements, int amount) {
        if (file.getString(itemRoute + ".type") == null && file.getString(itemRoute + ".material") == null) return;
        final ItemStack it = buildItem(itemRoute, replacements);
        it.setAmount(amount);
        final GuiItem guiItem = new GuiItem(it, action);
        gui.setItem(getSlots(slotRoute), guiItem);
    }

    protected List<Integer> getSlots(String r) {
        final List<Integer> slots = new ArrayList<>();

        if (!file.getStringList(r).isEmpty()) {
            for (Object route : file.getStringList(r)) {
                if (NumberUtils.isInt(("" + route))) {
                    slots.add(Integer.parseInt(("" + route)));
                } else {
                    String[] split = ("" + route).split("-");
                    int min = Integer.parseInt(split[0]);
                    int max = Integer.parseInt(split[1]);
                    for (int i = min; i <= max; i++) {
                        slots.add(i);
                    }
                }
            }
        } else {
            slots.add(file.getInt(r));
        }

        return slots;
    }
}
