package com.artillexstudios.axtrade.hooks.other;

import com.artillexstudios.axapi.placeholders.PlaceholderHandler;
import com.artillexstudios.axtrade.trade.Trade;
import com.artillexstudios.axtrade.trade.Trades;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.artillexstudios.axtrade.AxTrade.TOGGLED;

public class Placeholders {

    public Placeholders() {
        PlaceholderHandler.registerTransformer(OfflinePlayer.class, Player.class, OfflinePlayer::getPlayer);

        PlaceholderHandler.register("trading", ctx -> {
            Player player = ctx.resolve(Player.class);
            return "" + Trades.isTrading(player);
        });

        PlaceholderHandler.register("partner", ctx -> {
            Player player = ctx.resolve(Player.class);
            Trade trade = Trades.getTrade(player);
            if (trade == null) return "";
            return trade.getOtherPlayer(player).getName();
        });

        PlaceholderHandler.register("enabled", ctx -> {
            Player player = ctx.resolve(Player.class);
            return "" + !TOGGLED.getBoolean("toggled." + player.getUniqueId(), false);
        });

        PlaceholderHandler.register("active_trades", ctx -> {
            return "" + Trades.getTrades().size();
        });
    }
}
