package com.supaham.playernames;

import com.supaham.playernames.database.Database;
import com.supaham.playernames.database.DatabaseManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the API class for accessing the {@link PlayerNamesPlugin}.
 */
public class PlayerNamesAPI {

    /**
     * Gets the instance of {@link PlayerNamesPlugin}.
     *
     * @return instance of PlayerNamesPlugin class
     */
    public static PlayerNamesPlugin getPluginInstance() {
        return PlayerNamesPlugin.getInstance();
    }

    public static DatabaseManager getDatabaseManager() {
        return getPluginInstance().getDatabaseManager();
    }

    public static Database getDatabase() {
        return getDatabaseManager().getDatabase();
    }

    /**
     * @see Database#insertName(UUID, String)
     */
    public static void insertName(UUID uuid, String name) {
        getDatabase().insertName(uuid, name);
    }

    /**
     * @see Database#insertNames(UUID, String...)
     */
    public static void insertNames(UUID uuid, String... names) {
        getDatabase().insertNames(uuid, names);
    }

    /**
     * @see Database#getUUID(String)
     */
    public static UUID getUUID(String name) {
        return getDatabase().getUUID(name);
    }

    /**
     * @see Database#getNames(UUID)
     */
    public static List<String> getNames(UUID uuid) {
        return getDatabase().getNames(uuid);
    }

    /**
     * @see Database#deleteName(String)
     */
    public static boolean deleteName(String name) {
        return getDatabase().deleteName(name);
    }

    /**
     * @see Database#deleteName(UUID, String)
     */
    public static boolean deleteName(UUID uuid, String name) {
        return getDatabase().deleteName(uuid, name);
    }

    /**
     * @see Database#deleteNames(String...)
     */
    public static Integer deleteNames(String... names) {
        return getDatabase().deleteNames(names);
    }

    /**
     * @see Database#deleteNameAndReturn(String)
     */
    public static UUID deleteNameAndReturn(String name) throws UnsupportedOperationException {
        return getDatabase().deleteNameAndReturn(name);
    }

    /**
     * @see Database#deleteNames(String...)
     */
    public static Map<String, UUID> deleteNamesAndReturn(String... names) throws UnsupportedOperationException {
        return getDatabase().deleteNamesAndReturn(names);
    }

    /**
     * @see Database#clear(UUID)
     */
    public static Integer clear(UUID uuid) {
        return getDatabase().clear(uuid);
    }

    /**
     * @see Database#clearAndReturn(UUID)
     */
    public static List<String> clearAndReturn(UUID uuid) throws UnsupportedOperationException {
        return getDatabase().clearAndReturn(uuid);
    }
}
