package com.artillexstudios.axtrade.hooks.currency;

import com.artillexstudios.axquestboard.api.AxQuestBoardAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AxQuestBoardHook implements CurrencyHook {

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "AxQuestBoard";
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
    public void giveBalance(@NotNull UUID player, double amount) {
        AxQuestBoardAPI.modifyPoints(player, (int) amount);
    }

    @Override
    public void takeBalance(@NotNull UUID player, double amount) {
        AxQuestBoardAPI.modifyPoints(player, (int) amount * -1);
    }
}