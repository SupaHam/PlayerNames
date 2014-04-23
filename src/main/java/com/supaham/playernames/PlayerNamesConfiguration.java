package com.supaham.playernames;

import com.supaham.playernames.util.ConfigUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Represents {@link PlayerNamesPlugin}'s configuration class.
 */
public final class PlayerNamesConfiguration {

    private PlayerNamesPlugin plugin;
    private File file;
    private FileConfiguration config;
    private boolean firstRun;

    protected PlayerNamesConfiguration(PlayerNamesPlugin instance, File file) throws FileNotFoundException {
        this.plugin = instance;

        this.file = file;
        create();
        config = new YamlConfiguration();
    }

    /**
     * Loads data from the configuration file.
     *
     * @throws FileNotFoundException thrown if the config file was not found and couldn't be created.
     */
    protected void load() throws FileNotFoundException {
        try {
            create();
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Error occurred while loading " + file.getName() + ".");
            e.printStackTrace();
        }

        if (firstRun) save();
    }

    /**
     * Saves this data to the configuration file.
     */
    protected void save() {

        config.options().header("This configuration file contains all configurable options for the PlayerNames \n" +
                                "plugin created by SupaHam.\n");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error occurred while saving " + file.getName());
            e.printStackTrace();
        }
    }

    private void create() throws FileNotFoundException {
        ConfigUtil.createDefaultConfiguration(file, file.getName(), false);
        if (!file.exists()) {
            try {
                file.createNewFile();
                firstRun = true;
            } catch (IOException e) {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
        }
    }

}
