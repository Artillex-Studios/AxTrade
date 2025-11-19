package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axtrade.api.events.AxTradeAbortEvent;
import com.artillexstudios.axtrade.api.events.AxTradeCompleteEvent;
import com.artillexstudios.axtrade.currency.CurrencyProcessor;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.utils.HistoryUtils;
import com.artillexstudios.axtrade.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.SoundUtils;
import com.artillexstudios.axtrade.utils.TaxUtils;
import com.artillexstudios.axtrade.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.LANG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Trade {
    protected final TradePlayer player1;
    protected final TradePlayer player2;
    private boolean ended = false;
    protected long prepTime = System.currentTimeMillis();

    public Trade(Player p1, Player p2) {
        this.player1 = new TradePlayer(this, p1);
        this.player2 = new TradePlayer(this, p2);
        player1.setOtherPlayer(player2);
        player2.setOtherPlayer(player1);

        HistoryUtils.writeToHistory(String.format("Started: %s - %s", player1.getPlayer().getName(), player2.getPlayer().getName()));
    }

    public void update() {
        player1.getTradeGui().update();
        player2.getTradeGui().update();
    }

    public void end() {
        ended = true;
        Scheduler.get().run(scheduledTask -> Trades.removeTrade(this));
        player1.getPlayer().closeInventory();
        player1.getPlayer().updateInventory();
        player2.getPlayer().closeInventory();
        player2.getPlayer().updateInventory();
    }

    public void abort() {
        abort(false);
    }

    public void abort(boolean force) {
        if (!force && ended) return;

        Player p1 = player1.getPlayer();
        Player p2 = player2.getPlayer();

        // Set ended=true immediately to prevent handleClose() from calling abort() again
        ended = true;

        AxTradeAbortEvent event = new AxTradeAbortEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        // Collect and clone items BEFORE closing GUI, then clear slots immediately to prevent duplication
        List<org.bukkit.inventory.ItemStack> player1Items = new ArrayList<>();
        TradeGui gui1 = player1.getTradeGui();
        org.bukkit.inventory.Inventory inv1 = gui1.gui.getInventory();
        for (int slot : gui1.slots) {
            org.bukkit.inventory.ItemStack item = inv1.getItem(slot);
            if (item != null) {
                player1Items.add(item.clone());
                inv1.setItem(slot, null); // Clear slot immediately after cloning
            }
        }

        List<org.bukkit.inventory.ItemStack> player2Items = new ArrayList<>();
        if (player2.getTradeGui() != null) {
            TradeGui gui2 = player2.getTradeGui();
            org.bukkit.inventory.Inventory inv2 = gui2.gui.getInventory();
            for (int slot : gui2.slots) {
                org.bukkit.inventory.ItemStack item = inv2.getItem(slot);
                if (item != null) {
                    player2Items.add(item.clone());
                    inv2.setItem(slot, null); // Clear slot immediately after cloning
                }
            }
        }

        // Return items synchronously BEFORE closing GUI
        for (org.bukkit.inventory.ItemStack item : player1Items) {
            java.util.HashMap<Integer, org.bukkit.inventory.ItemStack> leftover = p1.getInventory().addItem(item);
            if (!leftover.isEmpty() && p1.isOnline()) {
                // Drop items on ground if inventory is full
                p1.getWorld().dropItemNaturally(p1.getLocation(), item);
            }
        }

        for (org.bukkit.inventory.ItemStack item : player2Items) {
            java.util.HashMap<Integer, org.bukkit.inventory.ItemStack> leftover = p2.getInventory().addItem(item);
            if (!leftover.isEmpty() && p2.isOnline()) {
                // Drop items on ground if inventory is full
                p2.getWorld().dropItemNaturally(p2.getLocation(), item);
            }
        }

        HistoryUtils.writeToHistory(String.format("Aborted: %s - %s", player1.getPlayer().getName(), player2.getPlayer().getName()));
        if (p1.isOnline()) {
            MESSAGEUTILS.sendLang(p1, "trade.aborted", Map.of("%player%", p2.getName()));
            SoundUtils.playSound(p1, "aborted");
        }
        if (p2.isOnline()) {
            MESSAGEUTILS.sendLang(p2, "trade.aborted", Map.of("%player%", p1.getName()));
            SoundUtils.playSound(p2, "aborted");
        }

        // Close GUI and remove trade AFTER returning items
        Scheduler.get().run(scheduledTask -> Trades.removeTrade(this));
        p1.closeInventory();
        p1.updateInventory();
        p2.closeInventory();
        p2.updateInventory();
    }

    public void complete() {
        end();
        for (Map.Entry<CurrencyHook, Double> entry : player1.getCurrencies().entrySet()) {
            if (entry.getKey().getBalance(player1.getPlayer().getUniqueId()) < entry.getValue()) {
                abort(true);
                return;
            }
        }

        for (Map.Entry<CurrencyHook, Double> entry : player2.getCurrencies().entrySet()) {
            if (entry.getKey().getBalance(player2.getPlayer().getUniqueId()) < entry.getValue()) {
                abort(true);
                return;
            }
        }

        AxTradeCompleteEvent event = new AxTradeCompleteEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            abort(true);
            return;
        }

        CurrencyProcessor currencyProcessor1 = new CurrencyProcessor(player1.getPlayer(), player1.getCurrencies().entrySet());
        currencyProcessor1.run().thenAccept(success1 -> {
            if (!success1) {
                abort(true);
                return;
            }

            CurrencyProcessor currencyProcessor2 = new CurrencyProcessor(player2.getPlayer(), player2.getCurrencies().entrySet());
            currencyProcessor2.run().thenAccept(success2 -> {
                if (!success2) {
                    abort(true);
                    currencyProcessor1.reverse();
                    return;
                }

                MESSAGEUTILS.sendLang(player1.getPlayer(), "trade.completed", Map.of("%player%", player2.getPlayer().getName()));
                MESSAGEUTILS.sendLang(player2.getPlayer(), "trade.completed", Map.of("%player%", player1.getPlayer().getName()));
                SoundUtils.playSound(player1.getPlayer(), "completed");
                SoundUtils.playSound(player2.getPlayer(), "completed");

                List<String> player1Currencies = new ArrayList<>();
                for (Map.Entry<CurrencyHook, Double> entry : player1.getCurrencies().entrySet()) {
                    double amountAfterTax = TaxUtils.getTotalAfterTax(entry.getValue(), entry.getKey());

                    entry.getKey().giveBalance(player2.getPlayer().getUniqueId(), amountAfterTax);

                    String currencyName = Utils.getFormattedCurrency(entry.getKey());
                    String fullCurrencyAmount = NumberUtils.formatNumber(entry.getValue());
                    String taxedCurrencyAmount = NumberUtils.formatNumber(amountAfterTax);

                    if (amountAfterTax == entry.getValue())
                        player1Currencies.add(currencyName + ": " + taxedCurrencyAmount);
                    else
                        player1Currencies.add(currencyName + ": " + taxedCurrencyAmount + " (+ tax: " + NumberUtils.formatNumber(entry.getValue() - amountAfterTax) + ")");

                    if (CONFIG.getBoolean("enable-trade-summaries")) {
                        MESSAGEUTILS.sendFormatted(player2.getPlayer(), LANG.getString("summary.get.currency"), Map.of("%amount%", taxedCurrencyAmount, "%currency%", currencyName));
                        MESSAGEUTILS.sendFormatted(player1.getPlayer(), LANG.getString("summary.give.currency"), Map.of("%amount%", fullCurrencyAmount, "%currency%", currencyName));
                    }
                }

                List<String> player2Currencies = new ArrayList<>();
                for (Map.Entry<CurrencyHook, Double> entry : player2.getCurrencies().entrySet()) {
                    double amountAfterTax = TaxUtils.getTotalAfterTax(entry.getValue(), entry.getKey());

                    entry.getKey().giveBalance(player1.getPlayer().getUniqueId(), amountAfterTax);

                    String currencyName = Utils.getFormattedCurrency(entry.getKey());
                    String fullCurrencyAmount = NumberUtils.formatNumber(entry.getValue());
                    String taxedCurrencyAmount = NumberUtils.formatNumber(amountAfterTax);

                    if (amountAfterTax == entry.getValue())
                        player2Currencies.add(currencyName + ": " + taxedCurrencyAmount);
                    else
                        player2Currencies.add(currencyName + ": " + taxedCurrencyAmount + " (+ tax: " + NumberUtils.formatNumber(entry.getValue() - amountAfterTax) + ")");

                    if (CONFIG.getBoolean("enable-trade-summaries")) {
                        MESSAGEUTILS.sendFormatted(player2.getPlayer(), LANG.getString("summary.give.currency"), Map.of("%amount%", fullCurrencyAmount, "%currency%", currencyName));
                        MESSAGEUTILS.sendFormatted(player1.getPlayer(), LANG.getString("summary.get.currency"), Map.of("%amount%", taxedCurrencyAmount, "%currency%", currencyName));
                    }
                }

                List<String> player1Items = new ArrayList<>();
                player1.getTradeGui().getItems(false).forEach(itemStack -> {
                    if (itemStack == null) return;
                    Scheduler.get().runAt(player2.getPlayer().getLocation(), task -> {
                        ContainerUtils.INSTANCE.addOrDrop(player2.getPlayer().getInventory(), List.of(itemStack), player2.getPlayer().getLocation());
                    });
                    final String itemName = Utils.getFormattedItemName(itemStack);
                    int itemAm = itemStack.getAmount();
                    player1Items.add(itemAm + "x " + itemName);
                    if (CONFIG.getBoolean("enable-trade-summaries")) {
                        MESSAGEUTILS.sendFormatted(player1.getPlayer(), LANG.getString("summary.give.item"), Map.of("%amount%", "" + itemAm, "%item%", itemName));
                        MESSAGEUTILS.sendFormatted(player2.getPlayer(), LANG.getString("summary.get.item"), Map.of("%amount%", "" + itemAm, "%item%", itemName));
                    }
                });

                List<String> player2Items = new ArrayList<>();
                player2.getTradeGui().getItems(false).forEach(itemStack -> {
                    if (itemStack == null) return;
                    Scheduler.get().runAt(player1.getPlayer().getLocation(), task -> {
                        ContainerUtils.INSTANCE.addOrDrop(player1.getPlayer().getInventory(), List.of(itemStack), player1.getPlayer().getLocation());
                    });
                    final String itemName = Utils.getFormattedItemName(itemStack);
                    int itemAm = itemStack.getAmount();
                    player2Items.add(itemAm + "x " + itemName);
                    if (CONFIG.getBoolean("enable-trade-summaries")) {
                        MESSAGEUTILS.sendFormatted(player2.getPlayer(), LANG.getString("summary.give.item"), Map.of("%amount%", "" + itemAm, "%item%", itemName));
                        MESSAGEUTILS.sendFormatted(player1.getPlayer(), LANG.getString("summary.get.item"), Map.of("%amount%", "" + itemAm, "%item%", itemName));
                    }
                });

                HistoryUtils.writeToHistory(
                        String.format("%s: [Currencies: %s] [Items: %s] | %s: [Currencies: %s] [Items: %s]",
                                player1.getPlayer().getName(), player1Currencies.isEmpty() ? "---" : String.join(", ", player1Currencies), player1Items.isEmpty() ? "---" : String.join(", ", player1Items), player2.getPlayer().getName(), player2Currencies.isEmpty() ? "---" : String.join(", ", player2Currencies), player2Items.isEmpty() ? "---" : String.join(", ", player2Items)));
            });
        });
    }

    public long getPrepTime() {
        return prepTime;
    }

    public TradePlayer getPlayer1() {
        return player1;
    }

    public TradePlayer getPlayer2() {
        return player2;
    }

    public Player getOtherPlayer(Player player) {
        return player1.getPlayer().equals(player) ? player2.getPlayer() : player1.getPlayer();
    }

    public boolean isEnded() {
        return ended;
    }
}
