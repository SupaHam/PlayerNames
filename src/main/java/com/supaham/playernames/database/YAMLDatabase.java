package com.supaham.playernames.database;

import com.supaham.playernames.PlayerNamesPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a YAML implementation of {@link Database}.
 */
public class YAMLDatabase implements Database {

    private PlayerNamesPlugin plugin;
    private File file;
    private FileConfiguration config;

    private BukkitTask autoSaveTask;

    protected YAMLDatabase(PlayerNamesPlugin instance, File file) throws FileNotFoundException {
        this.plugin = instance;

        this.file = file;
        create();
        config = new YamlConfiguration();

        long duration = plugin.getDatabaseConfiguration().getAutoSaveDuration() * 20;
        if (duration > 0) {
            autoSaveTask = new BukkitRunnable() {
                @Override
                public void run() {
                    save();
                }
            }.runTaskTimer(plugin, duration, duration);
        }
    }

    /**
     * Loads data from the database file.
     *
     * @throws FileNotFoundException thrown if the database file was not found and couldn't be created.
     */
    protected void load() throws FileNotFoundException {
        try {
            create();
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Error occurred while loading yaml database.");
            e.printStackTrace();
        }
    }

    /**
     * Saves this data to the database file.
     */
    public void save() {
        config.options().header("This file contains all player names belonging to a player.\n" +
                                "PLEASE DO NOT MODIFY THIS FILE DIRECTLY. \n" +
                                "If you decide to modify it anyways, please make sure player names are all lowercase.");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error occurred while saving yaml database.");
            e.printStackTrace();
        }
    }

    private void create() throws FileNotFoundException {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
        }
    }

    @Override
    public void insertName(UUID uuid, String name) {
        insertNames(uuid, new String[]{name});
    }

    @Override
    public void insertNames(UUID uuid, String... names) {
        String uuidString = uuid.toString();
        List<String> list = config.getStringList(uuidString);
        for (String name : names) {
            list.add(name.toLowerCase());
        }
        config.set(uuidString, list);
    }

    @Override
    public UUID getUUID(String name) {
        name = name.toLowerCase();
        for (String uuid : config.getKeys(false)) {
            for (String uuidName : config.getStringList(uuid)) {
                if (uuidName.equals(name)) {
                    return UUID.fromString(uuid);
                }
            }
        }
        return null;
    }

    @Override
    public List<String> getNames(UUID uuid) {
        return config.getStringList(uuid.toString());
    }

    @Override
    public boolean deleteName(String name) {
        return deleteNameAndReturn(name) != null;
    }

    @Override
    public boolean deleteName(UUID uuid, String name) {
        String uuidString = uuid.toString();
        List<String> list = config.getStringList(uuidString);
        if (list.isEmpty()) {
            return false;
        }
        boolean result = list.remove(name.toLowerCase());
        config.set(uuidString, list);
        return result;
    }

    @Override
    public Integer deleteNames(String... names) {
        return deleteNamesAndReturn(names).size();
    }

    @Override
    public UUID deleteNameAndReturn(String name) {
        name = name.toLowerCase();
        for (String uuid : config.getKeys(false)) {
            List<String> names = config.getStringList(uuid);
            for (String uuidName : names) {
                if (uuidName.equals(name)) {
                    names.remove(name);
                    config.set(uuid, names);
                    return UUID.fromString(uuid);
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, UUID> deleteNamesAndReturn(final String... names) {
        Map<String, UUID> map = new HashMap<>();

        List<String> lowercaseNames = new ArrayList<>();
        // Convert names to lowercase
        for (int i = 0; i < names.length; i++) {
            lowercaseNames.add(i, names[i].toLowerCase());
        }

        // Loop through all uuids
        for (String uuid : config.getKeys(false)) {
            boolean changes = false;
            List<String> list = config.getStringList(uuid);

            // Loop through all the names belonging to the current uuid
            for (int i = 0; i < list.size(); i++) {
                String uuidName = list.get(i);
                // Loop through all the names requested and remove both, from the lowercaseNames (since each name is 
                // unique) and from the list (names belonging to the uuid)
                for (int j = 0; j < lowercaseNames.size(); j++) {
                    String name = lowercaseNames.get(j);
                    if (uuidName.equals(name)) {
                        // TODO figure out an efficient way to get exact name (as provided in names) instead of 
                        // putting lowercase name
                        map.put(name, UUID.fromString(uuid));
                        list.remove(i);
                        lowercaseNames.remove(j);
                        changes = true;
                    }
                }
            }
            if (changes) {
                config.set(uuid, list);
            }
        }

        return map;
    }

    @Override
    public Integer clear(UUID uuid) {
        return clearAndReturn(uuid).size();
    }

    @Override
    public List<String> clearAndReturn(UUID uuid) {
        String uuidString = uuid.toString();
        List<String> names = config.getStringList(uuidString);
        config.set(uuidString, null);
        return names;
    }

    /**
     * Gets the {@link BukkitTask} that repeatedly saves this database.
     *
     * @return BukkitTask
     */
    public BukkitTask getAutoSaveTask() {
        return autoSaveTask;
    }
}
