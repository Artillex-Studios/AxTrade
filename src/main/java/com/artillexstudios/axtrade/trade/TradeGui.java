package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.Cooldown;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.hooks.HookManager;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.utils.BlacklistUtils;
import com.artillexstudios.axtrade.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.ShulkerUtils;
import com.artillexstudios.axtrade.utils.TaxUtils;
import com.artillexstudios.axtrade.utils.Utils;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.GUIS;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class TradeGui extends GuiFrame {
    private static final Cooldown<Player> confirmCooldown = new Cooldown<>();
    protected final Trade trade;
    private final TradePlayer player;
    protected final StorageGui gui;
    protected final List<Integer> slots = getSlots("own-slots");
    protected final List<Integer> otherSlots = getSlots("partner-slots");
    private boolean inSign = false;

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
        gui.setDefaultTopClickAction(this::handleClickTop);
        gui.setDragAction(this::handleDrag);
        gui.setPlayerInventoryAction(this::handleClickBottom);
        gui.setCloseGuiAction(this::handleClose);

        if (trade.isEnded()) return;

        update();
        gui.open(player.getPlayer());
        updateTitle();
        opened = true;
    }

    public void update() {
        if (player.hasConfirmed()) {
            super.createItem("own.confirm-item.slot", "own.confirm-item.cancel", event -> {
                event.setCancelled(true);
                if (confirmCooldown.hasCooldown(player.getPlayer())) return;
                confirmCooldown.addCooldown(player.getPlayer(), 50L);
                player.cancel();
                trade.update();
            }, Map.of(), player.getConfirmed());
        } else {
            super.createItem("own.confirm-item.slot", "own.confirm-item.accept", event -> {
                event.setCancelled(true);
                if (confirmCooldown.hasCooldown(player.getPlayer())) return;
                confirmCooldown.addCooldown(player.getPlayer(), 50L);
                player.confirm();
            }, Map.of());
        }

        if (player.getOtherPlayer().hasConfirmed()) {
            super.createItem("partner.confirm-item.slot", "partner.confirm-item.cancel", event -> {
                event.setCancelled(true);
            }, Map.of(
                    "%own-name%", player.getPlayer().getName(),
                    "%partner-name%", player.getOtherPlayer().getPlayer().getName()
            ), player.getOtherPlayer().getConfirmed());
        } else {
            super.createItem("partner.confirm-item.slot", "partner.confirm-item.accept", event -> {
                event.setCancelled(true);
            }, Map.of(
                    "%own-name%", player.getPlayer().getName(),
                    "%partner-name%", player.getOtherPlayer().getPlayer().getName()
            ));
        }

        for (String currencyItem : GUIS.getSection("own").getRoutesAsStrings(false)) {
            final String currencyStr = GUIS.getString("own." + currencyItem + ".currency", null);
            if (currencyStr == null) continue;

            CurrencyHook currencyHook = HookManager.getCurrencyHook(currencyStr);
            if (currencyHook == null) continue;
            super.createItem("own." + currencyItem, event -> {
                handleCurrencyClick(currencyStr, event);
            }, Map.of(
                    "%amount%", NumberUtils.formatNumber(player.getCurrency(currencyStr)),
                    "%tax-amount%", NumberUtils.formatNumber(TaxUtils.getTotalAfterTax(player.getCurrency(currencyStr), currencyHook)),
                    "%tax-percent%", NumberUtils.formatNumber(TaxUtils.getTaxPercent(currencyHook).doubleValue()),
                    "%tax-fee%", NumberUtils.formatNumber(TaxUtils.getTotalTax(player.getCurrency(currencyStr), currencyHook))
            ));
        }

        for (String currencyItem : GUIS.getSection("partner").getRoutesAsStrings(false)) {
            final String currencyStr = GUIS.getString("partner." + currencyItem + ".currency", null);
            if (currencyStr == null) continue;

            CurrencyHook currencyHook = HookManager.getCurrencyHook(currencyStr);
            if (currencyHook == null) continue;
            super.createItem("partner." + currencyItem, event -> {
                event.setCancelled(true);
            }, Map.of(
                    "%amount%", NumberUtils.formatNumber(player.getOtherPlayer().getCurrency(currencyStr)),
                    "%tax-amount%", NumberUtils.formatNumber(TaxUtils.getTotalAfterTax(player.getOtherPlayer().getCurrency(currencyStr), currencyHook)),
                    "%tax-percent%", NumberUtils.formatNumber(TaxUtils.getTaxPercent(currencyHook).doubleValue()),
                    "%tax-fee%", NumberUtils.formatNumber(TaxUtils.getTotalTax(player.getOtherPlayer().getCurrency(currencyStr), currencyHook))
            ));
        }

        for (int slot : otherSlots) {
            gui.removeItem(slot);
        }

        if (!opened) return;

        List<ItemStack> otherItems = player.getOtherPlayer().getTradeGui().getItems(true);
        int n = 0;
        for (int slot : otherSlots) {
            if (otherItems.get(n) != null)
                gui.updateItem(slot, new GuiItem(otherItems.get(n), event -> event.setCancelled(true)));
            n++;
        }
    }

    private void handleClickTop(InventoryClickEvent event) {
        if (confirmCooldown.hasCooldown(player.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        ItemStack it = getItem(event);

        if (BlacklistUtils.isBlacklisted(it)) {
            event.setCancelled(true);
            MESSAGEUTILS.sendLang(player.getPlayer(), "trade.blacklisted-item");
            return;
        }

        // prevent move with number key
        if (event.getCurrentItem() == null && it != null && checkFull(event)) return;

        if (event.getCurrentItem() != null && event.getClick().isRightClick() && Tag.SHULKER_BOXES.isTagged(event.getCurrentItem().getType())) {
            handleShulkerClick(event);
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
    }

    private void handleClickBottom(InventoryClickEvent event) {
        ItemStack it = getItem(event);

        if (BlacklistUtils.isBlacklisted(it)) {
            event.setCancelled(true);
            MESSAGEUTILS.sendLang(player.getPlayer(), "trade.blacklisted-item");
            return;
        }

        if (event.getCurrentItem() != null) {
            if (checkFull(event)) return;

            if (event.isShiftClick() && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && !slots.contains(event.getView().getTopInventory().firstEmpty())) {
                event.setCancelled(true);
                for (int i : slots) {
                    if (gui.getInventory().getItem(i) == null) {
                        gui.getInventory().setItem(i, event.getCurrentItem().clone());
                        event.getCurrentItem().setAmount(0);
                        break;
                    }
                }
            }
        }

        player.cancel();
        Scheduler.get().run(scheduledTask -> trade.update());
    }

    private void handleDrag(InventoryDragEvent event) {
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
    }

    private void handleClose(InventoryCloseEvent event) {
        if (inSign) return;
        trade.abort();
    }

    private boolean checkFull(Cancellable event) {
        if (!CONFIG.getBoolean("prevent-adding-items-when-inventory-full", true)) return false;

        // get items in gui
        int filledSlots = getItems(false).size();
        // get how many empty inventory slots the other player has
        int emptySlots = player.getOtherPlayer().getEmptySlots();

        if (filledSlots >= emptySlots) {
            event.setCancelled(true);
            MESSAGEUTILS.sendLang(player.getPlayer(), "trade.inventory-full");
            return true;
        }
        return false;
    }

    private void handleCurrencyClick(String currencyStr, InventoryClickEvent event) {
        event.setCancelled(true);
        player.cancel();
        trade.update();
        inSign = true;
        trade.prepTime = System.currentTimeMillis();
        event.getWhoClicked().closeInventory();

        var lines = StringUtils.formatList(LANG.getStringList("currency-editor-sign"));
        lines.set(0, Component.empty());

        var sign = new SignInput.Builder().setLines(lines).setHandler((player1, result) -> {
            if (trade.isEnded()) return;
            trade.prepTime = System.currentTimeMillis();
            String am = result[0];
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
                if (trade.isEnded()) return;
                gui.open(player.getPlayer());
                inSign = false;
                trade.update();
                updateTitle();
            });
        }).build(player.getPlayer());
        sign.open();
    }

    public void handleShulkerClick(InventoryClickEvent event) {
        event.setCancelled(true);
        player.cancel();
        trade.update();
        inSign = true;
        trade.prepTime = System.currentTimeMillis();
        event.getWhoClicked().closeInventory();

        BaseGui shulkerGui = Gui.storage().rows(3).title(StringUtils.format(Utils.getFormattedItemName(event.getCurrentItem()))).disableAllInteractions().create();
        shulkerGui.getInventory().setContents(ShulkerUtils.getShulkerContents(event.getCurrentItem()));
        shulkerGui.setCloseGuiAction(e -> Scheduler.get().run(t -> {
            if (trade.isEnded()) return;
            trade.prepTime = System.currentTimeMillis();
            gui.open(player.getPlayer());
            inSign = false;
            trade.update();
            updateTitle();
        }));
        shulkerGui.open(player.getPlayer());
    }

    @Nullable
    private ItemStack getItem(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (event.getClick() == ClickType.SWAP_OFFHAND && event.getClickedInventory().getType() != InventoryType.PLAYER) {
                return player.getPlayer().getInventory().getItemInOffHand();
            }
            if (event.getClick() == ClickType.NUMBER_KEY) {
                // when using a number key, the game will move it from the another inventory, so use the opposite of the clicked inventory
                Inventory inventory = event.getClickedInventory().getType() == InventoryType.PLAYER ? event.getView().getTopInventory() : event.getView().getBottomInventory();
                return inventory.getItem(event.getHotbarButton());
            }
        }
        return event.getCurrentItem();
    }

    public List<ItemStack> getItems(boolean includeAir) {
        final List<ItemStack> items = new ArrayList<>();
        for (int slot : slots) {
            ItemStack item = gui.getInventory().getItem(slot);
            if (!includeAir && item == null) continue;
            items.add(item);
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
