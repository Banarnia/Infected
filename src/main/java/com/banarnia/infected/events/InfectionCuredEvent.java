package com.banarnia.infected.events;

import com.banarnia.infected.api.events.BanarniaEvent;
import com.banarnia.infected.events.InfectionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class InfectionCuredEvent extends BanarniaEvent {
    private Player player;
    private Cause cause;

    // ~~~~~~~~~~~~~~~~~~~~ Constructor ~~~~~~~~~~~~~~~~~~~~

    /**
     * Constructor
     * @param player Player that got infected.
     * @param cause Cause of cure.
     */
    public InfectionCuredEvent(Player player, Cause cause) {
        this.player = player;
        this.cause = cause;
    }

    // ~~~~~~~~~~~~~~~~~~~~ Enum for different causes ~~~~~~~~~~~~~~~~~~~~

    public enum Cause {
        EXPIRED, COMMAND, MILK, DEATH
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

    public Cause getCause() {
        return cause;
    }
}
