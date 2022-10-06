package com.banarnia.infected.config;

import com.banarnia.infected.api.messages.IMessage;
import org.bukkit.ChatColor;

/**
 * This enumeration contains all messages that can be configured.
 */
public enum Message implements IMessage {

    // ~~~~~~~~~~~~~~~~~~~~ Messages ~~~~~~~~~~~~~~~~~~~~

    PLAYER_INFECTED_COMMAND("&a%target% &ehas been infected!"),
    PLAYER_INFECTED("&a%target% &ehas been infected by &6%player%&e!"),
    PLAYER_CURED("&eYou are cured from your §ainfection&e!"),
    PLAYER_PROTECTION_STARTS("&eYou will be protected from infections for &a%time% seconds&e!"),
    PLAYER_PROTECTION_RAN_OUT("&cYou are not protected from infections anymore!"),
    ERROR_PLAYER_ALREADY_INFECTED("§cThis player is already infected!"),
    ERROR_PLAYER_CANT_GET_INFECTED("§cThis player can't get infected!"),
    ERROR_PLAYER_IS_NOT_INFECTED("§cThis player is not infected!");

    // ~~~~~~~~~~~~~~~~~~~~ Methods ~~~~~~~~~~~~~~~~~~~~

    private String defaultMessage;
    private String message;

    Message(String defaultMessage) {
        this.defaultMessage = defaultMessage;
        this.message = defaultMessage;
    }

    @Override
    public String getKey() {
        return String.valueOf(this);
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public String get() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public void set(String message) {
        this.message = message;
    }
}
