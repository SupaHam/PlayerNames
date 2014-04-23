package com.supaham.playernames.database.dao;

import com.supaham.playernames.database.Database;

import java.util.UUID;

/**
 * Represents a Player DAO (Data Access Object).
 */
public interface PlayerDAO extends Database {

    /**
     * Selects a {@link UUID}'s auto generated id in the database.
     *
     * @param uuid uuid to select
     * @return the id belonging to {@code uuid} if it exists, otherwise null
     */
    public Integer selectPlayerId(UUID uuid);

    /**
     * Inserts a {@link UUID} to the database.
     *
     * @param uuid uuid to insert
     * @return the auto generated id belonging to {@code uuid}
     */
    public Integer insertUUID(UUID uuid);
}
