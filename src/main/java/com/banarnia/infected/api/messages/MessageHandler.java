package com.banarnia.infected.api.messages;

import com.banarnia.infected.api.util.FileLoader;
import com.google.common.collect.Maps;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;

/**
 * This class handles messages that should be configured in a config file.
 */
public class MessageHandler {

    private FileLoader defaultFile;
    private HashMap<Class<? extends IMessage>, FileLoader> enumMap = Maps.newHashMap();

    // ~~~~~~~~~~~~~~~~~~~~ Constructor ~~~~~~~~~~~~~~~~~~~~

    /**
     * Constructor of the MessageHandler.
     * @param plugin Plugin that the MessageHandler belongs to.
     */
    public MessageHandler(JavaPlugin plugin) {
        // Null check.
        if (plugin == null)
            throw new IllegalArgumentException();

        // Set default file.
        defaultFile = FileLoader.of(plugin.getDataFolder(), "messages.yml");
    }

    // ~~~~~~~~~~~~~~~~~~~~ Registration ~~~~~~~~~~~~~~~~~~~~

    /**
     * Check if an enumeration is already registered.
     * @param enumClass Class to be checked.
     * @return True if the class is registered else false.
     */
    public boolean isRegistered(Class<? extends IMessage> enumClass) {
        return enumMap != null ? enumMap.containsKey(enumClass) : false;
    }

    /**
     * Register an enumeration in the handler.
     * @param enumeration Class to be registered.
     */
    public void register(Class<? extends IMessage> enumeration) {
        register(enumeration, defaultFile);
    }

    /**
     * Register an enumeration with a specific FileLoader.
     * @param enumClass Class to be registered.
     * @param messageFile FileLoader to save the messages in.
     */
    public void register(Class<? extends IMessage> enumClass, FileLoader messageFile) {
        // Null checks.
        if (enumClass == null || messageFile == null || enumMap == null || !enumClass.isEnum())
            throw new IllegalArgumentException();

        // Check if the enumeration was already registered.
        if (isRegistered(enumClass))
            throw new IllegalArgumentException("The Message File was already registered.");

        // Add enumeration to the map.
        enumMap.put(enumClass, messageFile);

        // Load enum.
        load(enumClass);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Loading ~~~~~~~~~~~~~~~~~~~~

    /**
     * Load messages from the config.
     * @param enumClass Class to be loaded.
     */
    public void load(Class<? extends IMessage> enumClass) {
        // Null check.
        if (enumClass == null)
            throw new IllegalArgumentException();

        // Check if enum is registered.
        if (!isRegistered(enumClass))
            throw new IllegalStateException("The Message File was already registered.");

        // Get FileLoader.
        FileLoader fileLoader = enumMap.get(enumClass);

        // Reload file.
        fileLoader.reload();

        // Read values.
        Arrays.stream(enumClass.getEnumConstants()).forEach(enumValue -> {
            // Key to the message.
            String key = enumValue.getKey();

            // Default message.
            String defaultMessage = enumValue.getDefaultMessage();

            // Message of the config, if it exists. Else default message.
            String message = fileLoader.getOrElseSet(key, defaultMessage);

            // Set message value in the enum.
            enumValue.set(message);
        });
    }

    // Reload all messages.
    public void reload() {
        enumMap.keySet().forEach(enumeration -> load(enumeration));
    }

    // ~~~~~~~~~~~~~~~~~~~~ Getter & Setter ~~~~~~~~~~~~~~~~~~~~

    public FileLoader getDefaultFile() {
        return defaultFile;
    }
}