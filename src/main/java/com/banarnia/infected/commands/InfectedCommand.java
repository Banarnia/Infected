package com.banarnia.infected.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.banarnia.infected.InfectionManager;
import com.banarnia.infected.Main;
import com.banarnia.infected.config.Message;
import com.banarnia.infected.events.InfectionCuredEvent;
import com.banarnia.infected.events.InfectionEvent;

/**
 * This class adds the commands to start an infection.
 */
@CommandAlias("infected")
@CommandPermission("infected.admin")
public class InfectedCommand extends BaseCommand {

    private InfectionManager manager;

    // ~~~~~~~~~~~~~~~~~~~~ Constructor ~~~~~~~~~~~~~~~~~~~~

    /**
     * Constructor
     * @param manager Instance of the manager.
     */
    public InfectedCommand(InfectionManager manager) {
        this.manager = manager;
    }

    // ~~~~~~~~~~~~~~~~~~~~ Commands ~~~~~~~~~~~~~~~~~~~~

    /**
     * Reload the configuration files.
     * @param sender
     */
    @Subcommand("reload")
    public void reload(CommandIssuer sender) {
        // Reload config.
        manager.getConfig().load();

        // Reload language file.
        Main.getInstance().getMessageHandler().reload();

        // Send message.
        sender.sendMessage("§eThe configs have been reloaded.");
    }

    /**
     * Infect a player.
     * @param sender Command executor.
     * @param target Player to get infected.
     */
    @Subcommand("infect")
    @CommandCompletion("@players")
    public void infect(CommandIssuer sender, OnlinePlayer target) {
        // Check if player is already infected.
        if (manager.isInfected(target.getPlayer())) {
            sender.sendMessage(Message.ERROR_PLAYER_ALREADY_INFECTED.get());
            return;
        }

        // Infect player.
        boolean success = manager.infect(target.getPlayer(), null, InfectionEvent.Cause.COMMAND);

        // If it was not successful give an information to executor.
        if (!success)
            sender.sendMessage(Message.ERROR_PLAYER_CANT_GET_INFECTED.get());
    }

    /**
     * Cure a player.
     * @param sender Command Executor.
     * @param target Player to get cured.
     */
    @Subcommand("cure")
    @CommandCompletion("@players")
    public void cure(CommandIssuer sender, OnlinePlayer target) {
        // Check if player is infected.
        if (!manager.isInfected(target.getPlayer())) {
            sender.sendMessage(Message.ERROR_PLAYER_IS_NOT_INFECTED.get());
            return;
        }

        // Cure player.
        manager.cure(target.player, InfectionCuredEvent.Cause.COMMAND);
    }

}
