package com.supaham.playernames.database.dao;

import com.google.common.base.Strings;
import com.supaham.playernames.PlayerNamesPlugin;
import com.supaham.playernames.database.Database;
import com.supaham.playernames.database.MySQLDatabase;
import org.apache.commons.lang.Validate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a MySQL implementation of the {@link PlayerDAO}.
 */
public class PlayerMySQLTemplate implements PlayerDAO {

    private final Connection connection;
    private final PlayerNamesPlugin plugin = PlayerNamesPlugin.getInstance();
    private String playersTable, playerNamesTable;

    public PlayerMySQLTemplate() {
        Database db = plugin.getDatabaseManager().getDatabase();
        if (!(db instanceof MySQLDatabase)) {
            throw new UnsupportedOperationException("database type is not MySQL.");
        }

        this.connection = ((MySQLDatabase) db).getConnection();
        this.playersTable = plugin.getDatabaseConfiguration().getPlayersTable();
        this.playerNamesTable = plugin.getDatabaseConfiguration().getPlayerNamesTable();
    }

    @Override
    public Integer selectPlayerId(UUID uuid) {
        String query = "SELECT `player_id` FROM `" + playersTable + "` WHERE `uuid` = ?";
        try (PreparedStatement insertStmt = this.connection.prepareStatement(query)) {
            insertStmt.setString(1, uuid.toString());
            ResultSet rs = insertStmt.executeQuery();
            if (rs.first()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer insertUUID(UUID uuid) {
        String query = "INSERT IGNORE INTO `" + playersTable + "` (`uuid`) VALUES (?)";
        try (PreparedStatement insertStmt = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, uuid.toString());
            insertStmt.executeUpdate();
            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.first()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertName(UUID uuid, String name) {
        insertNames(uuid, new String[]{name});
    }

    @Override
    public void insertNames(UUID uuid, String... names) {
        if (names == null || names.length == 0) {
            throw new IllegalArgumentException("names must have at least one entry");
        }

        Integer playerId = null;
        String query;

        if (names.length == 1) {
            query = "INSERT INTO `" + playerNamesTable + "` (`player_id`, `name`) " +
                    "SELECT `p`.`player_id`, ? FROM `" + playersTable + "` AS `p` WHERE `p`.`uuid` = ? " +
                    "ON DUPLICATE KEY UPDATE `player_id` = `p`.`player_id`";
        } else {
            playerId = selectPlayerId(uuid);
            if (playerId == null) {
                playerId = insertUUID(uuid);
            }
            query = "INSERT INTO `" + playerNamesTable + "` (`player_id`, `name`)";
            query += Strings.repeat(" VALUES(?,?)", names.length);
            query += " ON DUPLICATE KEY UPDATE `player_id` = ?";
        }

        try (PreparedStatement insertStmt = this.connection.prepareStatement(query)) {
            if (names.length == 1) {
                insertStmt.setString(1, names[0]);
                insertStmt.setString(2, uuid.toString());
                insertStmt.executeUpdate();
            } else {
                for (String name : names) {
                    insertStmt.setInt(1, playerId);
                    insertStmt.setString(2, name);
                    insertStmt.setInt(3, playerId);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UUID getUUID(String name) {
        Validate.notNull(name, "name cannot be null.");

        String query = "SELECT `p`.`uuid` FROM `" + playersTable + "` AS `p` " +
                       "INNER JOIN `" + playerNamesTable + "` AS `pn` " +
                       "  ON `p`.`player_id` = `pn`.`player_id` " +
                       "WHERE `pn`.`name` = ?";
        try (PreparedStatement insertStmt = this.connection.prepareStatement(query)) {
            insertStmt.setString(1, name);
            ResultSet rs = insertStmt.executeQuery();
            if (rs.first()) {
                return UUID.fromString(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getNames(UUID uuid) {
        Validate.notNull(uuid, "uuid cannot be null.");

        String query = "SELECT `pn`.`name` FROM `" + playerNamesTable + "` AS `pn` " +
                       "INNER JOIN `" + playersTable + "` AS `p` " +
                       "  ON `pn`.`player_id` = `p`.`player_id` " +
                       "WHERE `p`.`uuid` = ?";
        try (PreparedStatement insertStmt = this.connection.prepareStatement(query)) {
            insertStmt.setString(1, uuid.toString());
            ResultSet rs = insertStmt.executeQuery();
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString(1));
            }
            return names;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteName(String name) {
        Validate.notNull(name, "name cannot be null.");

        String query = "DELETE FROM `" + playerNamesTable + "` WHERE `name` = ?";
        try (PreparedStatement insertStmt = this.connection.prepareStatement(query)) {
            insertStmt.setString(1, name);
            return insertStmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteName(UUID uuid, String name) {
        Validate.notNull(name, "name cannot be null.");
        // I believe the query would be faster if not the same speed without checking the uuid, 
        // names are unique in the mysql tables
        return deleteNames(name) == 1;
    }

    @Override
    public Integer deleteNames(String... names) {
        if (names == null || names.length == 0) {
            throw new IllegalArgumentException("names must have at least one entry");
        }

        String query = "DELETE FROM `" + playerNamesTable + "` WHERE `name` ";
        if (names.length == 1) {
            query += "= ?";
        } else {
            // IN ('supaham', 'thatkid', 'thatotherkid')
            query += "IN (";
            for (String name : names) {
                query += "'" + name + "',";
            }
            query = query.substring(0, query.length() - 1) + ")";
        }

        try (PreparedStatement insertStmt = this.connection.prepareStatement(query)) {
            if (names.length == 1) {
                insertStmt.setString(1, names[0]);
            }
            return insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public UUID deleteNameAndReturn(String name) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, UUID> deleteNamesAndReturn(String... names) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer clear(UUID uuid) {

        String query = "DELETE FROM `" + playerNamesTable + "` WHERE `player_id` IN (" +
                       "SELECT `player_id` FROM `" + playersTable + "` AS `p` WHERE `p`.`uuid` = ?)";

        try (PreparedStatement deleteStmt = this.connection.prepareStatement(query)) {
            deleteStmt.setString(1, uuid.toString());
            return deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> clearAndReturn(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
