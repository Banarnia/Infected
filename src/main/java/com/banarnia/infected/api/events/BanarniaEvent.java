package com.banarnia.infected.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

/**
 * Additional Event class that adds an easy to call functions.
 */
public abstract class BanarniaEvent extends Event {

    /**
     * Fires the event.
     */
    public void callEvent() {
        Bukkit.getPluginManager().callEvent(this);
    }

}
