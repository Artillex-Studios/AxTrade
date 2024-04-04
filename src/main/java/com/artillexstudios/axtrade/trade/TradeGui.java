package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.ScheduledTask;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.StorageGui;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.artillexstudios.axtrade.AxTrade.GUIS;

public class TradeGui extends GuiFrame {
    private final Trade trade;
    private final TradePlayer player;
    private final boolean shouldMirror;
    protected final StorageGui gui;
    protected final List<Integer> slots = getSlots("own-slots");

    public TradeGui(@NotNull Trade trade, @NotNull TradePlayer player) {
        super(GUIS, player.getPlayer());
        this.trade = trade;
        this.player = player;
        this.shouldMirror = player == trade.player2;
        this.gui = Gui.storage()
                .rows(GUIS.getInt("rows",6))
                .title(StringUtils.format(GUIS.getString("title").replace("%player%", player.getOtherPlayer().getPlayer().getName())))
                .create();
        setGui(gui);

        gui.setDefaultTopClickAction(event -> {
            if (!slots.contains(event.getSlot())) {
                event.setCancelled(true);
                if (event.getCursor() == null) return;
                player.getPlayer().getInventory().addItem(event.getCursor().clone());
                event.getCursor().setAmount(0);
                return;
            }
            Scheduler.get().run(scheduledTask -> trade.update());
        });

        update();
        gui.open(player.getPlayer());
    }

    public void update() {
        if (player.hasConfirmed()) {
            super.createItem("own.confirm-item.slot", "own.confirm-item.cancel", event -> {
                event.setCancelled(true);
                player.cancel();
            }, Map.of(), player.getConfirmed());
        } else {
            super.createItem("own.confirm-item.slot", "own.confirm-item.accept", event -> {
                event.setCancelled(true);
                player.confirm();
            }, Map.of());
        }

        if (player.getOtherPlayer().hasConfirmed()) {
            super.createItem("partner.confirm-item.slot", "partner.confirm-item.cancel", event -> {
                event.setCancelled(true);
            }, Map.of(), player.getOtherPlayer().getConfirmed());
        } else {
            super.createItem("partner.confirm-item.slot", "partner.confirm-item.accept", event -> {
                event.setCancelled(true);
            }, Map.of());
        }
        gui.update();
    }
}
