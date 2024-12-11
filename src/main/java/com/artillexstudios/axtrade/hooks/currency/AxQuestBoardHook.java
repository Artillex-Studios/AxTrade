package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axquestboard.api.AxQuestBoardAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class AxQuestBoardHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "AxQuestBoard";
    }

    @Override
    public String getDisplayName() {
        return HOOKS.getString("currencies.AxQuestBoard.name");
    }

    @Override
    public boolean worksOffline() {
        return true;
    }

    @Override
    public boolean usesDouble() {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public double getBalance(@NotNull UUID player) {
        return AxQuestBoardAPI.getPoints(player);
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        AxQuestBoardAPI.modifyPoints(player, (int) amount);
        cf.complete(true);
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        AxQuestBoardAPI.modifyPoints(player, (int) amount * -1);
        cf.complete(true);
        return cf;
    }
}