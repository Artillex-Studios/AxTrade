package com.artillexstudios.axtrade.request;

import org.bukkit.entity.Player;

public class Request {
    private final long time;
    private final Player sender;
    private final Player receiver;
    private boolean declined = false;

    public Request(Player sender, Player receiver) {
        this.time = System.currentTimeMillis();
        this.sender = sender;
        this.receiver = receiver;
    }

    public long getTime() {
        return time;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Player getSender() {
        return sender;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void decline() {
        declined = true;
    }
}
