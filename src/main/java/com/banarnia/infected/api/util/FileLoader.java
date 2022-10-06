package com.banarnia.infected.api.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * This class adds a simple to use FileLoader for YAML-Configuration.
 */
public class FileLoader {

    private boolean loaded;
    private String path;
    private File directory;
    private File file;
    private YamlConfiguration config;

    // ~~~~~~~~~~~~~~~~~~~~ Static instance creation ~~~~~~~~~~~~~~~~~~~~

    /**
     * Create FileLoader with static method.
     * @param directoryPath Path of the directory where the file is located.
     * @param fileName Name of the config file.
     * @return
     */
    public static FileLoader of(String directoryPath, String fileName) {
        return new FileLoader(directoryPath, fileName);
    }

    /**
     * Create FileLoader with static method.
     * @param directory Directory where the file is located.
     * @param fileName Name of the config file.
     * @return
     */
    public static FileLoader of(File directory, String fileName) {
        return new FileLoader(directory, fileName);
    }

    /**
     * Create FileLoader with static method.
     * @param directory Directory where the file is located.
     * @param file Config file.
     * @return
     */
    public static FileLoader of(File directory, File file) {
        return new FileLoader(directory, file);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Constructors ~~~~~~~~~~~~~~~~~~~~

    /**
     * Constructor for the FileLoader.
     * @param directory Directory where the file is located.
     * @param file Config file.
     */
    public FileLoader(File directory, File file) {
        // Null checks
        if (directory == null || file == null)
            throw new NullPointerException();

        this.path = file.getPath();
        this.directory = directory;
        this.file = file;

        // Create file if it doesn't exist yet.
        if (!file.exists()) {
            // Create folder.
            directory.mkdirs();

            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Load YamlConfiguration.
        initConfig();

        // Mark config as loaded.
        this.loaded = file != null;
    }

    /**
     * Constructor for the FileLoader.
     * @param directory Directory where the file is located.
     * @param fileName Name of the config file.
     */
    public FileLoader(File directory, String fileName) {
        this(directory, new File(directory, fileName));
    }

    /**
     * Constructor for the FileLoader.
     * @param directoryPath Path of the directory where the file is located.
     * @param fileName Name of the config file.
     */
    public FileLoader(String directoryPath, String fileName) {
        this(new File(directoryPath), fileName);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Init ~~~~~~~~~~~~~~~~~~~~

    /**
     * Init the YamlConfiguration.
     * @return True or false, depending on the init success.
     */
    private boolean initConfig() {
        // Null check.
        if (file == null) {
            Bukkit.getLogger().severe("Could not load config file.");
            return false;
        }

        // Load YamlConfiguration.
        this.config = YamlConfiguration.loadConfiguration(file);

        return true;
    }

    // ~~~~~~~~~~~~~~~~~~~~ File methods ~~~~~~~~~~~~~~~~~~~~

    /**
     * Save the current configuration in the file. Reload file afterwards.
     * @return Instance for chaining.
     */
    public FileLoader save() {
        return save(true);
    }

    /**
     * Save the current configuration in the file.
     * @param reload Reload file afterwards or not.
     * @return Instance for chaining.
     */
    public FileLoader save(boolean reload) {
        // Null checks.
        if (file == null || config == null)
            throw new IllegalStateException();

        // Save file.
        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("File could not be saved: "  + this.path);
        }

        // Reload file if wanted.
        return reload ? reload() : this;
    }

    /**
     * Reloads the file.
     * @return Instance for chaining.
     */
    public FileLoader reload() {
        // Null check.
        if (path == null)
            throw new IllegalArgumentException();

        // Create file.
        this.file = new File(this.path);

        // Null check.
        if (file == null)
            throw new NullPointerException();

        // Create YamlConfiguration.
        this.config = YamlConfiguration.loadConfiguration(this.file);

        return this;
    }

    // ~~~~~~~~~~~~~~~~~~~~ Write values ~~~~~~~~~~~~~~~~~~~~

    /**
     * Check if key exists.
     * @param key Key to request.
     * @return True if the key exists, else false.
     */
    public boolean isSet(String key) {
        return config.isSet(key);
    }

    /**
     * Check if a ConfigurationSection exists.
     * @param path Key to the section.
     * @return True if the section exists, else false.
     */
    public boolean hasConfigurationSection(String path) {
        return getConfigurationSection(path) != null;
    }

    /**
     * Get the value of a given key. Returns a default value if it does not exist and sets it in the file.
     * @param key Key in the config.
     * @param def Default value to return if it does not exist.
     * @return The config value or default value, if it does not exist in the config.
     * @param <T> Generic type of the value.
     */
    public <T> T getOrElseSet(String key, T def) {
        // Check if key exists in the config.
        if (isSet(key))
            // Return value from config.
            return (T) config.get(key);

        // Set default value and save config.
        config.set(key, def);
        save();

        return def;
    }

    /**
     * Write a value into the config. Separate save is required, if the data should be persistent.
     * @param key Key in the config.
     * @param value Value for the given key.
     * @return Instance for chaining.
     */
    public FileLoader set(String key, Object value) {
        config.set(key, value);
        return this;
    }

    // ~~~~~~~~~~~~~~~~~~~~ Read values ~~~~~~~~~~~~~~~~~~~~

    /**
     * Read a configuration section.
     * @param path Key of the section.
     * @return ConfigurationSection, if exists.
     */
    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    /**
     * Read the keys of the config.
     * @param deep Also return underlying keys.
     * @return Set of found keys.
     */
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    /**
     * Read the keys of the config at a given section.
     * @param path Path to the section.
     * @param deep Also return underlying keys.
     * @return Set of found keys.
     */
    public Set<String> getKeys(String path, boolean deep) {
        if (!hasConfigurationSection(path))
            return Sets.newHashSet();

        return getConfigurationSection(path).getKeys(deep);
    }

    /**
     * Read a bool of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public Boolean getBoolean(String key, boolean def) {
        return config.getBoolean(key, def);
    }

    /**
     * Read a bool of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Read a String of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public String getString(String key, String def) {
        return config.getString(key, def);
    }

    /**
     * Read a String of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * Read a List of Strings of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public List<String> getStringList(String key, List<String> def) {
        // Abfrage, ob der Key existiert
        if (isSet(key))
            return config.getStringList(key);

        // Default-Wert zurückgeben, falls der Wert nicht existiert.
        return def;
    }

    /**
     * Read a List of Strings of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public List<String> getStringList(String key) {
        return getStringList(key, Lists.newArrayList());
    }

    /**
     * Read an int of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public int getInt(String key, int def) {
        return config.getInt(key, def);
    }

    /**
     * Read an int of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * Read a long of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public long getLong(String key, long def) {
        return config.getLong(key, def);
    }

    /**
     * Read a long of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public long getLong(String key) {
        return getLong(key, 0);
    }

    /**
     * Read a double of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public double getDouble(String key, double def) {
        return config.getDouble(key, def);
    }

    /**
     * Read a double of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    /**
     * Read an ItemStack of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public ItemStack getItemStack(String key, ItemStack def) {
        return config.getItemStack(key, def);
    }

    /**
     * Read an ItemStack of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public ItemStack getItemStack(String key) {
        return getItemStack(key, null);
    }

    /**
     * Read a Location of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public Location getLocation(String key, Location def) {
        return config.getLocation(key, def);
    }


    /**
     * Read a Location of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public Location getLocation(String key) {
        return getLocation(key, null);
    }

    /**
     * Read a serializable object of a config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Value of config or default value if it does not exist.
     */
    public <T extends ConfigurationSerializable> T getSerializable(String key, Class<T> clazz, T def) {
        return config.getSerializable(key, clazz, def);
    }

    /**
     * Read a serializable object of a config.
     * @param key Key in the config.
     * @return Value of config or default value if it does not exist.
     */
    public <T extends ConfigurationSerializable> T getSerializable(String key, Class<T> clazz) {
        return getSerializable(key, clazz, null);
    }

    // ~~~~~~~~~~~~~~~~~~~~ Getter & Setter ~~~~~~~~~~~~~~~~~~~~

    public File getDirectory() {
        return directory;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

}
