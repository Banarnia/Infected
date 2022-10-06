package com.banarnia.infected.config;

import com.banarnia.infected.api.util.FileLoader;
import com.google.common.collect.Lists;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * This class handles the configuration of the plugin.
 */
public class Config {

    // Instances
    private JavaPlugin plugin;
    private FileLoader config;

    // Config options
    private int infectionTime;
    private List<PotionEffect> effects = Lists.newArrayList();
    private double infectionRadius;
    private int protectionTime;
    private int checkTime;
    private boolean allowInfectionInAir;
    private boolean allowGlow;

    // ~~~~~~~~~~~~~~~~~~~~ Constructor ~~~~~~~~~~~~~~~~~~~~

    /**
     * Constructor.
     * @param plugin Plugin that the instance belongs to.
     * @param configFile Configuration file.
     */
    public Config(JavaPlugin plugin, FileLoader configFile) {
        this.plugin = plugin;
        this.config = configFile;

        // Load config.
        load();
    }

    // ~~~~~~~~~~~~~~~~~~~~ Config loading ~~~~~~~~~~~~~~~~~~~~

    /**
     * Load all the configurable values.
     */
    public void load() {
        // Get infection time.
        infectionTime = config.getOrElseSet("infection-time-seconds", 60);

        // Setup default effects.
        if (!config.hasConfigurationSection("effects")) {
            config.set("effects.0.type", PotionEffectType.BLINDNESS.getName());
            config.set("effects.0.amplifier", 2);

            config.set("effects.1.type", PotionEffectType.WITHER.getName());
            config.set("effects.1.amplifier", 2);

            config.save();
        }

        // Clear potionEffects before reloading.
        effects.clear();

        // Load effects.
        ConfigurationSection section = config.getConfigurationSection("effects");
        for (String key : section.getKeys(false)) {
            ConfigurationSection effectSection = section.getConfigurationSection(key);

            String effectName = effectSection.getString("type");
            int duration = 20 * infectionTime;
            int amplifier = effectSection.getInt("amplifier");

            // Check if given PotionEffectType is valid.
            PotionEffectType effectType = PotionEffectType.getByName(effectName);
            if (effectType == null) {
                plugin.getLogger().warning("Could not load PotionEffect: " + effectName);
                plugin.getLogger().warning("Skipping...");
                continue;
            }

            // Create potion effect.
            PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);

            // Add effect to list.
            effects.add(potionEffect);
        }

        // Radius to infect other players.
        infectionRadius = config.getOrElseSet("infection-radius", 5.0);

        // Seconds the player is protected after he cured the infection.
        protectionTime = config.getOrElseSet("protection-time-seconds", 30);

        // Ticks between every infection check.
        checkTime = config.getOrElseSet("infection-check-time-ticks", 20);

        // Allow infection of other player while in the air.
        allowInfectionInAir = config.getOrElseSet("infection-while-in-air", false);

        // Allow glowing of infected players.
        allowGlow = config.getOrElseSet("glow-enabled", true);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Getter & Setter ~~~~~~~~~~~~~~~~~~~~

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public double getInfectionRadius() {
        return infectionRadius;
    }

    public int getCheckTime() {
        return checkTime;
    }

    public int getProtectionTime() {
        return protectionTime;
    }

    public int getInfectionTime() {
        return infectionTime;
    }

    public boolean allowInfectionInAir() {
        return allowInfectionInAir;
    }

    public boolean glowEnabled() {
        return allowGlow;
    }
}
