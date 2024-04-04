package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.AxTrade;
import com.artillexstudios.axtrade.utils.NumberUtils;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.GUIS;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class TradeGui extends GuiFrame {
    private final Trade trade;
    private final TradePlayer player;
    protected final StorageGui gui;
    protected final List<Integer> slots = getSlots("own-slots");
    protected final List<Integer> otherSlots = getSlots("partner-slots");
    private boolean inSign = false;

    public TradeGui(@NotNull Trade trade, @NotNull TradePlayer player) {
        super(GUIS, player.getPlayer());
        this.trade = trade;
        this.player = player;
        this.gui = Gui.storage()
                .rows(GUIS.getInt("rows",6))
                .title(StringUtils.format(GUIS.getString("title").replace("%player%", player.getOtherPlayer().getPlayer().getName())))
                .disableItemDrop()
                .create();
        setGui(gui);

        gui.setDefaultTopClickAction(event -> {
            player.cancel();
            if (!slots.contains(event.getSlot())) {
                event.setCancelled(true);
                if (event.getCursor() == null) return;
                player.getPlayer().getInventory().addItem(event.getCursor().clone());
                event.getCursor().setAmount(0);
                return;
            }
            Scheduler.get().run(scheduledTask -> trade.update());
        });

        gui.setDragAction(event -> {
            if (event.getInventory() == player.getPlayer().getInventory()) return;
            if (!new HashSet<>(slots).containsAll(event.getInventorySlots())) {
                event.setCancelled(true);
                return;
            }
            player.cancel();
            Scheduler.get().run(scheduledTask -> trade.update());
        });

        gui.setPlayerInventoryAction(event -> {
            if (event.isShiftClick() && !slots.contains(event.getView().getTopInventory().firstEmpty())) {
                event.setCancelled(true);
                return;
            }
            player.cancel();
            Scheduler.get().run(scheduledTask -> trade.update());
        });

        gui.setCloseGuiAction(event -> {
            if (inSign) return;
            trade.abort();
        });

        update();
        gui.open(player.getPlayer());
        opened = true;
    }

    public void update() {
        if (player.hasConfirmed()) {
            super.createItem("own.confirm-item.slot", "own.confirm-item.cancel", event -> {
                event.setCancelled(true);
                player.cancel();
                trade.update();
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

        for (String currencyItem : GUIS.getSection("own").getRoutesAsStrings(false)) {
            final String currencyStr = GUIS.getString("own." + currencyItem + ".currency", null);
            if (currencyStr == null) continue;

            super.createItem("own." + currencyItem, event -> {
                event.setCancelled(true);
                player.cancel();
                trade.update();
                inSign = true;
                trade.prepTime = System.currentTimeMillis();
                event.getWhoClicked().closeInventory();

                var lines = LANG.getStringList("currency-editor-sign");
                lines = StringUtils.formatListToString(lines);
                final SignGUI signGUI = SignGUI.builder().setLines(lines.toArray(new String[0])).setHandler((player1, result) -> List.of(SignGUIAction.runSync(AxTrade.getInstance(), () -> {
                    if (trade.ended) return;
                    trade.prepTime = System.currentTimeMillis();
                    String am = result.getLine(0);
                    TradePlayer.Result addResult = TradePlayer.Result.NOT_A_NUMBER;
                    if (com.artillexstudios.axapi.utils.NumberUtils.isDouble(am) && (addResult = player.setCurrency(currencyStr, Double.parseDouble(am))) == TradePlayer.Result.SUCCESS) {
                        MESSAGEUTILS.sendLang(player1, "currency-editor.success");
                    } else {
                        switch (addResult) {
                            case NOT_ENOUGH_CURRENCY:
                                MESSAGEUTILS.sendLang(player1, "currency-editor.not-enough");
                                break;
                            default:
                                MESSAGEUTILS.sendLang(player1, "currency-editor.failed");
                                break;
                        }
                    }
                    gui.open(player.getPlayer());
                    inSign = false;
                    trade.update();
                }))).build();
                signGUI.open(player.getPlayer());
            }, Map.of("%amount%", NumberUtils.formatNumber(player.getCurrency(currencyStr))));
        }

        for (String currencyItem : GUIS.getSection("partner").getRoutesAsStrings(false)) {
            final String currencyStr = GUIS.getString("partner." + currencyItem + ".currency", null);
            if (currencyStr == null) continue;

            super.createItem("partner." + currencyItem, event -> {
                event.setCancelled(true);
            }, Map.of(
                    "%amount%", NumberUtils.formatNumber(player.getOtherPlayer().getCurrency(currencyStr)),
                    "%player%", player.getOtherPlayer().getPlayer().getName()
            ));
        }

        for (int slot : otherSlots) {
            gui.removeItem(slot);
        }

        if (!opened) return;
        var otherItems = player.getOtherPlayer().getTradeGui().getItems();
        int n = 0;
        for (int slot : otherSlots) {
            if (otherItems.get(n) != null)
                gui.updateItem(slot, new GuiItem(otherItems.get(n), event -> event.setCancelled(true)));
            n++;
        }
    }

    public List<ItemStack> getItems() {
        final List<ItemStack> items = new ArrayList<>();
        for (int slot : slots) {
            items.add(gui.getInventory().getItem(slot));
        }
        return items;
    }
}
