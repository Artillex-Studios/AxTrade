package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;
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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
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
        if (player1.getTradeGui() != null) player1.getTradeGui().update();
        if (player2.getTradeGui() != null) player2.getTradeGui().update();
    }

    public void end() {
        ended = true;
        Scheduler.get().run(task -> Trades.removeTrade(this));

        if (player1 != null && player1.getPlayer().isOnline()) {
            Scheduler.get().runAt(player1.getPlayer().getLocation(), scheduledTask -> {
                player1.getPlayer().closeInventory();
                player1.getPlayer().updateInventory();
            });
        }

        if (player2 != null && player2.getPlayer().isOnline()) {
            Scheduler.get().runAt(player2.getPlayer().getLocation(), scheduledTask -> {
                player2.getPlayer().closeInventory();
                player2.getPlayer().updateInventory();
            });
        }
    }

    public void abort() {
        abort(false);
    }

    public void abort(boolean force) {
        if (!force && ended) return;

        AxTradeAbortEvent event = new AxTradeAbortEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        end();
        if (player1.getTradeGui() != null) {
            player1.getTradeGui().getItems(false).forEach(itemStack -> {
                if (itemStack == null) return;
                addOrDrop(player1.getPlayer().getInventory(), List.of(itemStack), player1.getPlayer().getLocation());
            });
        }
        if (player2.getTradeGui() != null) {
            player2.getTradeGui().getItems(false).forEach(itemStack -> {
                if (itemStack == null) return;
                addOrDrop(player2.getPlayer().getInventory(), List.of(itemStack), player2.getPlayer().getLocation());
            });
        }
        HistoryUtils.writeToHistory(String.format("Aborted: %s - %s", player1.getPlayer().getName(), player2.getPlayer().getName()));
        MESSAGEUTILS.sendLang(player1.getPlayer(), "trade.aborted", Map.of("%player%", player2.getPlayer().getName()));
        MESSAGEUTILS.sendLang(player2.getPlayer(), "trade.aborted", Map.of("%player%", player1.getPlayer().getName()));
        SoundUtils.playSound(player1.getPlayer(), "aborted");
        SoundUtils.playSound(player2.getPlayer(), "aborted");
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
                    addOrDrop(player2.getPlayer().getInventory(), List.of(itemStack), player2.getPlayer().getLocation());
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
                    addOrDrop(player1.getPlayer().getInventory(), List.of(itemStack), player1.getPlayer().getLocation());
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

    private void addOrDrop(Inventory inventory, List<ItemStack> items, Location location) {
        Location copy = location.clone();
        Scheduler.get().executeAt(copy, () -> {
            for( ItemStack key : items) {
                HashMap<Integer, ItemStack> remaining = inventory.addItem(key);
                remaining.forEach((k, v) -> copy.getWorld().dropItem(copy, v));
            }
        });
    }
}
