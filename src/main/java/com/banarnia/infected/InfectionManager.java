package com.banarnia.infected;

import com.banarnia.infected.commands.InfectedCommand;
import com.banarnia.infected.events.InfectionCuredEvent;
import com.banarnia.infected.config.Config;
import com.banarnia.infected.events.InfectionEvent;
import com.banarnia.infected.listener.InfectionListener;
import com.banarnia.infected.runnable.InfectionChecker;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class handles everything about the infection.
 */
public class InfectionManager {

    // Plugin instance
    private Main plugin;

    // Config instance
    private Config config;

    // Map of protected players. Contains UUID and time when protection expires.
    private HashMap<UUID, Long> protectedPlayers = Maps.newHashMap();

    // List of infected players. Contains UUID and time when infection expires.
    private HashMap<UUID, Long> infectedPlayers = Maps.newHashMap();

    // Runnable
    private InfectionChecker infectionChecker;

    // ~~~~~~~~~~~~~~~~~~~~ Constructor ~~~~~~~~~~~~~~~~~~~~

    /**
     * Constructor
     * @param plugin Instance of the plugin.
     * @param config Configuration class.
     */
    public InfectionManager(Main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        this.infectionChecker = new InfectionChecker(this);

        // Setup
        setupInfectionChecker();

        // Command Setup
        plugin.getCommandManager().registerCommand(new InfectedCommand(this));

        // Register Listener
        Bukkit.getPluginManager().registerEvents(new InfectionListener(this), plugin);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Runnable Setup ~~~~~~~~~~~~~~~~~~~~

    /**
     * Restarts the runnable to check for new infections.
     */
    public void setupInfectionChecker() {
        this.infectionChecker.restart();
    }

    // ~~~~~~~~~~~~~~~~~~~~ Infection ~~~~~~~~~~~~~~~~~~~~

    /**
     * Get the timestamp when the infection of the player ends.
     * @param player Player that is probably infected.
     * @return Timestamp when infection ends or 0 if the player is not infected.
     */
    public long getInfectionExpireTimestamp(Player player) {
        return infectedPlayers.getOrDefault(player.getUniqueId(), 0L);
    }

    /**
     * Check the map if a player is infected or not.
     * @param player Player to be checked.
     * @return Yes if the player is infected, else false.
     */
    public boolean isInfected(Player player) {
        return getInfectionExpireTimestamp(player) > System.currentTimeMillis();
    }

    /**
     * Infects a player giving him all PotionEffects.
     * @param target Player that gets infected.
     * @param origin Player that had the infection.
     */
    public boolean infect(Player target, Player origin, InfectionEvent.Cause cause) {
        // Check if player is already infected or protected.
        if (isInfected(target) || isProtected(target))
            return false;

        // Check if infection in air is allowed.
        // Deprecation because #isOnGround() is sent by client and can be manipulated. [06.10.2022]
        if (!target.isOnGround() && !config.allowInfectionInAir())
            return false;

        // Check if player is in GameMode Creative.
        if (target.getGameMode() == GameMode.CREATIVE)
            return false;

        // Throw event.
        InfectionEvent event = new InfectionEvent(target, origin, cause);
        event.callEvent();

        // Return false, if event was cancelled.
        if (event.isCancelled())
            return false;

        // Add potion effects.
        config.getEffects().forEach(effect -> target.addPotionEffect(effect));

        // Add to list of infected players.
        infectedPlayers.put(target.getUniqueId(), System.currentTimeMillis() + 1000 * config.getInfectionTime());
        protectedPlayers.remove(target.getUniqueId());

        return true;
    }

    // ~~~~~~~~~~~~~~~~~~~~ Protection ~~~~~~~~~~~~~~~~~~~~

    /**
     * Cures the player and removes the effects. Also sets the protection time for him.
     * @param player Player to be cured.
     */
    public void cure(Player player, InfectionCuredEvent.Cause cause) {
        // Add protection if player did not die.
        if (cause != InfectionCuredEvent.Cause.DEATH)
            protectedPlayers.put(player.getUniqueId(), System.currentTimeMillis() + 1000 * config.getProtectionTime());

        // Remove from map.
        infectedPlayers.remove(player.getUniqueId());

        // Throw event.
        new InfectionCuredEvent(player, cause).callEvent();

        // Remove potion effects if cured by command.
        if (cause == InfectionCuredEvent.Cause.COMMAND)
            config.getEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    /**
     * Check if a player is protected.
     * @param player Player to be checked.
     * @return True if protected, else false.
     */
    public boolean isProtected(Player player) {
        return protectionTimeMillis(player) > 0;
    }

    /**
     * Returns the amount of millis the player is protected.
     * @param player Player that is protected.
     * @return Amount of time that the player is protected.
     */
    public long protectionTimeMillis(Player player) {
        return getProtectionExpireTimestamp(player) - System.currentTimeMillis();
    }

    /**
     * Returns the timestamp when the player will not be protected anymore.
     * @param player Player to check.
     * @return Timestamp when protection expires.
     */
    public long getProtectionExpireTimestamp(Player player) {
        return protectedPlayers.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
    }

    // ~~~~~~~~~~~~~~~~~~~~ Getter & Setter ~~~~~~~~~~~~~~~~~~~~

    public HashMap<UUID, Long> getInfectedPlayers() {
        return infectedPlayers;
    }

    public HashMap<UUID, Long> getProtectedPlayers() {
        return protectedPlayers;
    }

    public Config getConfig() {
        return config;
    }
}
