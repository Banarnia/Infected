package com.banarnia.infected.runnable;

import com.banarnia.infected.InfectionManager;
import com.banarnia.infected.Main;
import com.banarnia.infected.events.InfectionCuredEvent;
import com.banarnia.infected.events.InfectionEvent;
import com.banarnia.infected.events.ProtectionExpiredEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * This runnable check the infection periodically.
 */
public class InfectionChecker implements Runnable {

    // Manager
    private InfectionManager manager;

    // Task ID
    private int taskID = -1;

    /**
     * Constructor
     * @param manager Instance of the Manager class.
     */
    public InfectionChecker(InfectionManager manager) {
        this.manager = manager;
    }

    /**
     * Restart the task.
     */
    public void restart() {
        // Stop task.
        stop();

        // Get repeat delay.
        int checkTime = manager.getConfig().getCheckTime();

        // Start task.
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), this, 0, checkTime);
    }

    /**
     * Stops the task.
     */
    public void stop() {
        if (taskID == -1)
            return;

        Bukkit.getScheduler().cancelTask(taskID);
    }

    /**
     * Checks every infected player periodically.
     * If there are not infected players nearby they will get infected as well.
     * Players in GameMode Creative are not affected.
     */
    @Override
    public void run() {
        for (Map.Entry<UUID, Long> entry : manager.getInfectedPlayers().entrySet()) {
            // Check if player is online.
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                manager.getInfectedPlayers().remove(entry.getKey());
                continue;
            }

            // Check if player is still infected.
            if (!manager.isInfected(player)) {
                manager.cure(player, InfectionCuredEvent.Cause.EXPIRED);
                continue;
            }

            // Check nearby players.
            // Infect them, if they are not in GameMode Creative.
            double radius = manager.getConfig().getInfectionRadius();
            player.getNearbyEntities(radius, radius, radius).stream()
                    .filter(target -> target instanceof Player)
                    .filter(target -> !manager.isInfected((Player) target))
                    .forEach(target -> manager.infect((Player) target, player, InfectionEvent.Cause.INFECTION));
        }

        // Check if protection status ended.
        for (UUID uuid : manager.getProtectedPlayers().keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            // Check if player is online.
            if (player == null || !player.isOnline()) {
                manager.getProtectedPlayers().remove(uuid);
                continue;
            }

            // Check if protection ran out.
            boolean protectedPlayer = manager.isProtected(player);

            if (protectedPlayer)
                continue;

            // Call Event when protection runs out.
            new ProtectionExpiredEvent(player).callEvent();

            // Remove Player from HashMap.
            manager.getProtectedPlayers().remove(uuid);
        }
    }

}
