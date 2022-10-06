package com.banarnia.infected.api.messages;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This interface adds methods for custom messages.
 */
public interface IMessage {

    /**
     * Returns the key in a config of a certain message.
     * @return The corresponding key.
     */
    String getKey();

    /**
     * Get the default message, which is hard coded.
     * @return Default message.
     */
    String getDefaultMessage();

    /**
     * Get the configured message.
     * @return
     */
    String get();

    /**
     * Returns the configured message with a replacement.
     * @param prev Chars to be replaced.
     * @param replacement Chars to insert.
     * @return The message with a replacement.
     */
    default String replace(String prev, String replacement) {
        return get().replace(prev, replacement);
    }

    /**
     * Set the message.
     * @param message New message.
     */
    void set(String message);

    /**
     * Insert certain placeholder into the string.
     * @param receiver Receiver of the message.
     * @return The message with parsed placeholder.
     */
    default String getParsed(CommandSender receiver) {
        String name = receiver instanceof Player ? receiver.getName() : "Console";

        return replace("%player%", name);
    }

    /**
     * Send the message to a player or the console.
     * @param receiver Receiver of the message.
     */
    default void send(CommandSender receiver) {
        receiver.sendMessage(getParsed(receiver));
    }

}