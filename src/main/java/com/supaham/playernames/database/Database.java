package com.supaham.playernames.database;

import com.supaham.playernames.PlayerNamesPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the Database interface for the {@link PlayerNamesPlugin}.
 */
public interface Database {

    /**
     * Inserts a name to a {@link UUID}.
     *
     * @param uuid uuid to insert name to
     * @param name name to assign to {@code uuid}
     */
    public void insertName(UUID uuid, String name);

    /**
     * Inserts a List of names to a {@link UUID}.
     *
     * @param uuid  uuid to insert name to
     * @param names names to assign to {@code uuid}
     */
    public void insertNames(UUID uuid, String... names);

    /**
     * Gets a {@link UUID} belonging to a name.
     *
     * @param name name to get
     * @return uuid
     */
    public UUID getUUID(String name);

    /**
     * Gets a List of names belonging to a {@link UUID}.
     *
     * @param uuid uuid to get
     * @return List of names belonging to {@code uuid}
     */
    public List<String> getNames(UUID uuid);

    /**
     * Deletes a name.
     *
     * @param name name to delete
     */
    public boolean deleteName(String name);

    /**
     * Deletes a name belonging to a {@link UUID}. <p/>
     * <strong>This method should really be used when possible for the {@link YAMLDatabase} to improve
     * performance</strong>
     *
     * @param uuid the uuid that owns the {@code name}
     * @param name name to delete
     * @return whether the name was deleted
     */
    public boolean deleteName(UUID uuid, String name);

    /**
     * Deletes an Array of names.
     *
     * @param names names to delete
     * @return the amount of names that were deleted
     */
    public Integer deleteNames(String... names);

    /**
     * Deletes a name.
     *
     * @param name name to delete
     * @return the {@link UUID} that owned the {@code name} if it was deleted, otherwise null.
     * @throws UnsupportedOperationException thrown if the database does not support this operation
     * @see #deleteName(String) 
     */
    public UUID deleteNameAndReturn(String name) throws UnsupportedOperationException;

    /**
     * Deletes an Array of names.
     *
     * @param names names to delete
     * @return a Map of names and the uuid that owned them
     * @throws UnsupportedOperationException thrown if the database does not support this operation
     * @see #deleteNames(String...) 
     */
    public Map<String, UUID> deleteNamesAndReturn(String... names) throws UnsupportedOperationException;

    /**
     * Deletes all names belonging to a {@link UUID}.
     *
     * @param uuid the uuid to clear
     * @return the amount of names that belonged to the {@code uuid}
     */
    public Integer clear(UUID uuid);

    /**
     * Deletes all names belonging to a UUID.
     *
     * @param uuid uuid to clear
     * @return the names that belonged to {@code uuid}
     * @throws UnsupportedOperationException thrown if the database does not support this operation
     * @see #clear(UUID) 
     */
    public List<String> clearAndReturn(UUID uuid) throws UnsupportedOperationException;
}
