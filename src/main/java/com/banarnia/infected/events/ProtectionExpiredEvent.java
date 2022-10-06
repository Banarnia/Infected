package com.banarnia.infected.events;

import com.banarnia.infected.api.events.BanarniaEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ProtectionExpiredEvent extends BanarniaEvent {
    private Player player;

    // ~~~~~~~~~~~~~~~~~~~~ Constructor ~~~~~~~~~~~~~~~~~~~~

    /**
     * Constructor
     * @param player Player whose protection expired.
     */
    public ProtectionExpiredEvent(Player player) {
        this.player = player;
    }

    // ~~~~~~~~~~~~~~~~~~~~ Weird bukkit event thing ~~~~~~~~~~~~~~~~~~~~

    // HandlerList
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    // ~~~~~~~~~~~~~~~~~~~~ Getter & Setter ~~~~~~~~~~~~~~~~~~~~

    public Player getPlayer() {
        return player;
    }
}