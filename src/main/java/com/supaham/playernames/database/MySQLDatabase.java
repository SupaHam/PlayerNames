package com.supaham.playernames.database;

import com.supaham.playernames.PlayerNamesDBConfiguration;
import com.supaham.playernames.PlayerNamesPlugin;
import com.supaham.playernames.database.dao.PlayerDAO;
import com.supaham.playernames.database.dao.PlayerMySQLTemplate;
import com.supaham.playernames.util.IOUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a MySQL implementation of {@link Database}.
 */
public class MySQLDatabase implements Database, PlayerDAO {

    private PlayerNamesPlugin plugin;
    private PlayerNamesDBConfiguration dbConfiguration;
    private Connection connection;

    public MySQLDatabase(PlayerNamesPlugin instance, PlayerNamesDBConfiguration dbConfiguration) throws
                                                                                                 ClassNotFoundException {
        plugin = instance;
        this.dbConfiguration = dbConfiguration;

        Class.forName("com.mysql.jdbc.Driver");

    }

    public void checkTables() {

        try (Statement create = getConnection().createStatement()) {
            TableSchemas schemas = new TableSchemas(dbConfiguration);
            create.execute(schemas.getPlayers());
            create.execute(schemas.getPlayerNames());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Selects a {@link UUID}'s auto generated id in the database.
     *
     * @param uuid uuid to select
     * @return the id belonging to {@code uuid} if it exists, otherwise null
     */
    @Override
    public Integer selectPlayerId(UUID uuid) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.selectPlayerId(uuid);
    }

    /**
     * Inserts a {@link UUID} to the database.
     *
     * @param uuid uuid to insert
     * @return the auto generated id belonging to {@code uuid}
     */
    @Override
    public Integer insertUUID(UUID uuid) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.insertUUID(uuid);
    }

    @Override
    public void insertName(UUID uuid, String name) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        dao.insertName(uuid, name);
    }

    @Override
    public void insertNames(UUID uuid, String... names) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        dao.insertNames(uuid, names);
    }

    @Override
    public UUID getUUID(String name) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.getUUID(name);
    }

    @Override
    public List<String> getNames(UUID uuid) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.getNames(uuid);
    }

    @Override
    public boolean deleteName(String name) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.deleteName(name);
    }

    @Override
    public boolean deleteName(UUID uuid, String name) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.deleteName(uuid, name);
    }

    @Override
    public Integer deleteNames(String... names) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.deleteNames(names);
    }

    @Override
    public UUID deleteNameAndReturn(String name) throws UnsupportedOperationException {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.deleteNameAndReturn(name);
    }

    @Override
    public Map<String, UUID> deleteNamesAndReturn(String... names) throws UnsupportedOperationException {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.deleteNamesAndReturn(names);
    }

    @Override
    public Integer clear(UUID uuid) {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.clear(uuid);
    }

    @Override
    public List<String> clearAndReturn(UUID uuid) throws UnsupportedOperationException {
        PlayerDAO dao = new PlayerMySQLTemplate();
        return dao.clearAndReturn(uuid);
    }

    /**
     * Gets the {@link Connection} for database access.
     *
     * @return Connection
     */
    public Connection getConnection() {
        try {
            if (connection == null || !connection.isValid(3)) {
                String ip = dbConfiguration.getIp();
                String port = dbConfiguration.getPort();
                String db = dbConfiguration.getDatabase();
                String user = dbConfiguration.getUsername();
                String pass = dbConfiguration.getPassword();
                // TODO not sure if connection pool is worth
                this.connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db, user, pass);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error occurred while getting connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    private class TableSchemas {

        // I mainly created this class just because the place holders in the actual schema files.
        // and having to call replaceAll seems way too expensive, so I only have to call it every time the database 
        // yaml file is loaded

        private String players;
        private String playerNames;

        public TableSchemas(PlayerNamesDBConfiguration configuration) {
            loadSchemas(configuration);
        }

        public void loadSchemas(PlayerNamesDBConfiguration configuration) {

            players = IOUtil.resourceToString("mysql/players-schema.sql")
                    .replaceAll("<players>", configuration.getPlayersTable());
            playerNames = IOUtil.resourceToString("mysql/player-names-schema.sql")
                    .replaceAll("<playerNames>", configuration.getPlayerNamesTable())
                    .replaceAll("<players>", configuration.getPlayersTable());
        }

        public String getPlayers() {
            return players;
        }

        public String getPlayerNames() {
            return playerNames;
        }
    }
}
