package com.banarnia.infected;

import co.aikar.commands.BukkitCommandManager;
import com.banarnia.infected.api.messages.MessageHandler;
import com.banarnia.infected.config.Config;
import com.banarnia.infected.api.util.FileLoader;
import com.banarnia.infected.config.Message;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class.
 */
public class Main extends JavaPlugin {

    // Static instance
    private static Main instance;

    // Config
    private Config config;

    // Manager
    private MessageHandler messageHandler;
    private BukkitCommandManager commandManager;
    private InfectionManager infectionManager;

    /**
     * Execute when plugin is enabled.
     */
    @Override
    public void onEnable() {
        // Static instance.
        instance = this;

        // Init managers and configs.
        init();
    }

    /**
     * Init all managers and configs.
     */
    private void init() {
        this.config = new Config(this, FileLoader.of(getDataFolder(), "config.yml"));
        this.messageHandler = new MessageHandler(this);
        this.messageHandler.register(Message.class);
        this.commandManager = new BukkitCommandManager(this);
        this.infectionManager = new InfectionManager(this, config);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Getter & Setter ~~~~~~~~~~~~~~~~~~~~

    public static Main getInstance() {
        return instance;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public BukkitCommandManager getCommandManager() {
        return commandManager;
    }
}