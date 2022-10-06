package com.banarnia.infected.events;

import com.banarnia.infected.api.events.BanarniaEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class InfectionEvent extends BanarniaEvent implements Cancellable {

    private Player target;
    private Player origin;
    private Cause cause;
    private boolean cancelled;

    // ~~~~~~~~~~~~~~~~~~~~ Constructor ~~~~~~~~~~~~~~~~~~~~

    /**
     * Constructor
     * @param target Player that got infected.
     * @param origin Player that infected the target.
     * @param cause Cause for the infection.
     */
    public InfectionEvent(Player target, Player origin, Cause cause) {
        this.target = target;
        this.origin = origin;
        this.cause = cause;
    }

    /**
     * Constructor
     * @param target Player that got infected.
     * @param origin Player that infected the target.
     */
    public InfectionEvent(Player target, Player origin) {
        this(target, origin, Cause.INFECTION);
    }

    /**
     * Constructor
     * @param target Player that got infected.
     */
    public InfectionEvent(Player target) {
        this(target, null, Cause.COMMAND);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Enum for different causes ~~~~~~~~~~~~~~~~~~~~

    public enum Cause {
        COMMAND, INFECTION
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Cause getCause() {
        return cause;
    }

    public Player getOrigin() {
        return origin;
    }

    public Player getTarget() {
        return target;
    }
}
