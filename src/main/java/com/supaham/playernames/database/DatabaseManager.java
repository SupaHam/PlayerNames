package com.supaham.playernames.database;

import com.supaham.playernames.PlayerNamesDBConfiguration;
import com.supaham.playernames.PlayerNamesPlugin;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Represents a {@link Database} manager. This class contains the database instance.
 */
public class DatabaseManager {

    private final PlayerNamesPlugin plugin;
    private final Database database;

    public DatabaseManager(PlayerNamesPlugin instance, PlayerNamesDBConfiguration dbConfiguration)
            throws ClassNotFoundException, FileNotFoundException {
        this.plugin = instance;

        String dbType = dbConfiguration.getDatabaseType();

        switch (dbType) {
            case "mysql":
                this.database = new MySQLDatabase(plugin, dbConfiguration);
                ((MySQLDatabase) this.database).checkTables();
                break;
            case "yaml":
                this.database = new YAMLDatabase(instance, new File(instance.getDataFolder(), "uuids.yml"));
                ((YAMLDatabase) this.database).load();
                break;
            default:
                throw new UnsupportedOperationException("Database type '" + dbType + "' is not supported.");
        }
    }

    /**
     * Gets the {@link Database} instance.
     *
     * @return instance of Database
     */
    public Database getDatabase() {
        return database;
    }
}
