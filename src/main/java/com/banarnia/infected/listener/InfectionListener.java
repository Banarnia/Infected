package com.banarnia.infected.listener;

import com.banarnia.infected.InfectionManager;
import com.banarnia.infected.config.Message;
import com.banarnia.infected.events.InfectionCuredEvent;
import com.banarnia.infected.events.InfectionEvent;
import com.banarnia.infected.events.ProtectionExpiredEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InfectionListener implements Listener {

    private InfectionManager manager;

    public InfectionListener(InfectionManager manager) {
        this.manager = manager;
    }

    // ~~~~~~~~~~~~~~~~~~~~ Infection & Cure ~~~~~~~~~~~~~~~~~~~~

    @EventHandler(priority = EventPriority.HIGH)
    public void handleInfection(InfectionEvent event) {
        // Check if event was cancelled.
        if (event.isCancelled())
            return;

        Player player = event.getTarget();

        // Send message.
        String message = event.getOrigin() != null ? Message.PLAYER_INFECTED.get() :
                                                     Message.PLAYER_INFECTED_COMMAND.get();
        message = message.replace("%target%", event.getTarget().getName());

        if (event.getOrigin() != null)
            message = message.replace("%player%", event.getOrigin().getName());

        Bukkit.broadcastMessage(message);

        // Play sound.
        player.playSound(player, Sound.AMBIENT_CAVE, 1.0f, 1.0f);

        // Set player to glow if enabled.
        if (manager.getConfig().glowEnabled())
            player.setGlowing(true);
    }

    @EventHandler
    public void handleCure(InfectionCuredEvent event) {
        Player player = event.getPlayer();

        // Send message that player is cured.
        String message = Message.PLAYER_CURED.get();
        player.sendMessage(message);

        // If he didn't die tell the player about the protection time.
        if (event.getCause() == InfectionCuredEvent.Cause.DEATH)
            return;

        // Get message.
        message = Message.PLAYER_PROTECTION_STARTS
                         .replace("%time%", String.valueOf(manager.getConfig().getProtectionTime()));
        player.sendMessage(message);

        // Play sound.
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        // Disable glow.
        player.setGlowing(false);
    }

    @EventHandler
    public void handleMilkDrink(PlayerItemConsumeEvent event) {
        // Check if item is milk bucket.
        if (event.getItem().getType() != Material.MILK_BUCKET)
            return;

        // Check if player is infected.
        if (!manager.isInfected(event.getPlayer()))
            return;

        // Cure player.
        Player player = event.getPlayer();
        manager.cure(player, InfectionCuredEvent.Cause.MILK);
    }

    @EventHandler
    public void handleProtectionExpiring(ProtectionExpiredEvent event) {
        Player player = event.getPlayer();

        // Send message.
        Message.PLAYER_PROTECTION_RAN_OUT.send(player);

        // Play sound.
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, 1.0f);
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        // Check if player was infected.
        if (!manager.isInfected(event.getEntity()))
            return;

        manager.cure(event.getEntity(), InfectionCuredEvent.Cause.DEATH);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Clean up on disconnect ~~~~~~~~~~~~~~~~~~~~

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        // Check if player was infected.
        if (!manager.isInfected(event.getPlayer()))
            return;

        // Cure player.
        manager.cure(event.getPlayer(), InfectionCuredEvent.Cause.COMMAND);
        manager.getProtectedPlayers().remove(event.getPlayer().getUniqueId());
    }

}
