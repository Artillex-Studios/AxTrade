package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.scheduler.ScheduledTask;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.utils.BlackListUtils;
import com.artillexstudios.axtrade.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.ShulkerUtils;
import com.artillexstudios.axtrade.utils.Utils;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.artillexstudios.axtrade.AxTrade.GUIS;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class TradeGui extends GuiFrame {
    protected final Trade trade;
    private final TradePlayer player;
    protected final StorageGui gui;
    protected final List<Integer> slots = getSlots("own-slots");
    protected final List<Integer> otherSlots = getSlots("partner-slots");
    private boolean inSign = false;
//    private final ItemStack fullSlot;

    public TradeGui(@NotNull Trade trade, @NotNull TradePlayer player) {
        super(GUIS, player.getPlayer(), trade);
        this.trade = trade;
        this.player = player;
        this.gui = Gui.storage()
                .rows(GUIS.getInt("rows",6))
                .title(Component.empty())
                .disableItemDrop()
                .create();
        setGui(gui);

//        fullSlot = new ItemBuilder(GUIS.getSection("full-slot")).get();
//        NBTUtils.writeToNBT(fullSlot, "axtrade-full-slot", true);

        gui.setDefaultTopClickAction(event -> {
            final ItemStack it = event.getClick() == ClickType.NUMBER_KEY ? event.getView().getBottomInventory().getItem(event.getHotbarButton()) : event.getCurrentItem();
//            if (it != null && NBTUtils.containsNBT(it, "axtrade-full-slot")) {
//                event.setCancelled(true);
//                return;
//            }
            if (BlackListUtils.isBlackListed(it)) {
                event.setCancelled(true);
                MESSAGEUTILS.sendLang(player.getPlayer(), "trade.blacklisted-item");
                return;
            }

            if (event.getCurrentItem() != null && event.getClick().isRightClick() && event.getCurrentItem().getType().toString().endsWith("SHULKER_BOX")) {
                event.setCancelled(true);
                player.cancel();
                trade.update();
                inSign = true;
                trade.prepTime = System.currentTimeMillis();
                event.getWhoClicked().closeInventory();

                final BaseGui shulkerGui = Gui.storage().rows(3).title(StringUtils.format(Utils.getFormattedItemName(event.getCurrentItem()))).disableAllInteractions().create();
                shulkerGui.getInventory().setContents(ShulkerUtils.getShulkerContents(event.getCurrentItem()));
                shulkerGui.setCloseGuiAction(shulkerEvent -> Scheduler.get().run(new Consumer<>() {
                    @Override
                    public void accept(ScheduledTask scheduledTask) {
                        if (trade.ended) return;
                        trade.prepTime = System.currentTimeMillis();
                        gui.open(player.getPlayer());
                        inSign = false;
                        trade.update();
                        updateTitle();
                    }
                }));
                shulkerGui.open(player.getPlayer());
                return;
            }

            if (!slots.contains(event.getSlot())) {
                event.setCancelled(true);
                if (event.getCursor() == null) return;
                player.getPlayer().getInventory().addItem(event.getCursor().clone());
                event.getCursor().setAmount(0);
                return;
            }

            player.cancel();
            Scheduler.get().run(scheduledTask -> trade.update());
        });

        gui.setDragAction(event -> {
            boolean ownInv = true;
            for (int s : event.getRawSlots()) {
                if (s > 53) continue;
                ownInv = false;
                break;
            }

            Scheduler.get().run(scheduledTask -> trade.update());
            if (ownInv) return;

            if (!new HashSet<>(slots).containsAll(event.getInventorySlots())) {
                event.setCancelled(true);
                return;
            }

            player.cancel();
        });

        gui.setPlayerInventoryAction(event -> {
            final ItemStack it = event.getClick() == ClickType.NUMBER_KEY ? event.getView().getBottomInventory().getItem(event.getHotbarButton()) : event.getCurrentItem();
            if (BlackListUtils.isBlackListed(it)) {
                event.setCancelled(true);
                MESSAGEUTILS.sendLang(player.getPlayer(), "trade.blacklisted-item");
                return;
            }

            if (event.getCurrentItem() != null && event.isShiftClick() && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && !slots.contains(event.getView().getTopInventory().firstEmpty())) {
                event.setCancelled(true);
                for (int i : slots) {
                    if (gui.getInventory().getItem(i) == null) {
                        gui.getInventory().setItem(i, event.getCurrentItem().clone());
                        event.getCurrentItem().setAmount(0);
                        break;
                    }
                }
            }

            player.cancel();
            Scheduler.get().run(scheduledTask -> trade.update());
        });

        gui.setCloseGuiAction(event -> {
            if (inSign) return;
            trade.abort();
        });

        if (trade.ended) return;

        update();
        gui.open(player.getPlayer());
        updateTitle();
        opened = true;
    }

//    private final HashSet<Integer> lockedSlot = new HashSet<>();

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

                var lines = StringUtils.formatList(LANG.getStringList("currency-editor-sign"));
                lines.set(0, Component.empty());

                var sign = new SignInput.Builder().setLines(lines).setHandler((player1, result) -> {
                    if (trade.ended) return;
                    trade.prepTime = System.currentTimeMillis();
                    String am = PlainTextComponentSerializer.plainText().serialize(result[0]);
                    TradePlayer.Result addResult = player.setCurrency(currencyStr, am);
                    if (addResult == TradePlayer.Result.SUCCESS) {
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
                    Scheduler.get().run(scheduledTask -> {
                        gui.open(player.getPlayer());
                        inSign = false;
                        trade.update();
                        updateTitle();
                    });
                }).build(player.getPlayer());
                sign.open();
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

//        for (Integer i : lockedSlot) {
//            gui.removeItem(i);
//        }

        // full inventory checker - start
//        var fullSlots = new ArrayList<>(slots);
//
//        int empty = 0;
//        for (ItemStack stack : player.getOtherPlayer().getPlayer().getInventory().getStorageContents()) {
//            if (stack == null) empty++;
//        }
//
//        for (int i = 0; i < slots.size() - empty; i++) {
//            int slot = fullSlots.remove(fullSlots.size() - 1);
//            gui.updateItem(slot, fullSlot); // todo: make configurable item, add special nbt, cancel on click
//            lockedSlot.add(slot);
//        }
        // full inventory checker - end

        if (!opened) return;
        var otherItems = player.getOtherPlayer().getTradeGui().getItems();
        int n = 0;
        for (int slot : otherSlots) {
            if (otherItems.get(n) != null)
                gui.updateItem(slot, new GuiItem(otherItems.get(n), event -> event.setCancelled(true)));
//            if (otherItems.size() <= n) break;
//            final ItemStack item = otherItems.get(n);
//            if (item != null && !NBTUtils.containsNBT(item, "axtrade-full-slot")) {
//                gui.updateItem(slot, new GuiItem(item, event -> event.setCancelled(true)));
//            }
            n++;
        }
    }

    public List<ItemStack> getItems() {
        final List<ItemStack> items = new ArrayList<>();
        for (int slot : slots) {
            items.add(gui.getInventory().getItem(slot));
//            final ItemStack item = gui.getInventory().getItem(slot);
//            if (item != null && NBTUtils.containsNBT(item, "axtrade-full-slot")) continue;
//            items.add(item);
        }
        return items;
    }

    public void updateTitle() {
        Component title = StringUtils.format(
                GUIS.getString("title")
                        .replace("%player%", player.getOtherPlayer().getPlayer().getName())
                        .replace("%own-status%", player.hasConfirmed() ? LANG.getString("placeholders.ready") : LANG.getString("placeholders.waiting"))
                        .replace("%partner-status%", player.getOtherPlayer().hasConfirmed() ? LANG.getString("placeholders.ready") : LANG.getString("placeholders.waiting"))
        );

        final Inventory topInv = player.getPlayer().getOpenInventory().getTopInventory();
        if (topInv.equals(gui.getInventory())) {
            NMSHandlers.getNmsHandler().setTitle(player.getPlayer().getOpenInventory().getTopInventory(), title);
        }
    }
}
