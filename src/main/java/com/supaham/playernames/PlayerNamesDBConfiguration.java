package com.supaham.playernames;

import com.supaham.playernames.util.ConfigUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Represents {@link PlayerNamesPlugin}'s database configuration class.
 */
public final class PlayerNamesDBConfiguration {

    private PlayerNamesPlugin plugin;
    private File file;
    private FileConfiguration config;

    private String databaseType;

    // MySQL
    private String ip;
    private String port;
    private String database;
    private String username;
    private String password;

    private String playersTable;
    private String playerNamesTable;

    //YAML
    private int autoSaveDuration;

    protected PlayerNamesDBConfiguration(PlayerNamesPlugin instance, File file) throws FileNotFoundException {
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
            plugin.getLogger().severe("Error occurred while loading yaml database.");
            e.printStackTrace();
        }

        this.databaseType = config.getString("database-type", "mysql");

        // TODO make this class abstract to avoid all the useless variables being in one class 
        switch (this.databaseType) {
            case "mysql":
                this.ip = config.getString("mysql.ip", "localhost");
                this.port = config.getString("mysql.port", "3306");
                this.database = config.getString("mysql.database", "test");
                this.username = config.getString("mysql.username", "root");
                this.password = config.getString("mysql.password", "");

                this.playersTable = config.getString("mysql.tables.players", "pn_players");
                this.playerNamesTable = config.getString("mysql.tables.player-names", "pn_player_names");
                break;
            case "yaml":
                this.autoSaveDuration = config.getInt("yaml.auto-save", 600);
                break;
        }
    }

    /**
     * Saves this data to the configuration file.
     */
    protected void save() {

        config.set("database-type", this.databaseType);
        config.options().header("This configuration file contains all database info for the PlayerNames \n" +
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
            } catch (IOException e) {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
        }
    }

    /**
     * Gets the database type name.
     *
     * @return database type
     */
    public String getDatabaseType() {
        return databaseType;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPlayersTable() {
        return playersTable;
    }

    public String getPlayerNamesTable() {
        return playerNamesTable;
    }

    public int getAutoSaveDuration() {
        return autoSaveDuration;
    }
}
