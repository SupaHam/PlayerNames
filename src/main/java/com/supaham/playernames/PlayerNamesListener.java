package com.supaham.playernames;

import com.supaham.playernames.database.Database;
import com.supaham.playernames.database.MySQLDatabase;
import com.supaham.playernames.database.YAMLDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerNamesListener implements Listener {

    private PlayerNamesPlugin plugin;

    public PlayerNamesListener(PlayerNamesPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final String name = player.getName();

        final Database database = plugin.getDatabaseManager().getDatabase();

        if (database instanceof YAMLDatabase) {
            UUID foundUUID = database.getUUID(name);

            // This player joined with their name for the first time
            if (!uuid.equals(foundUUID)) {
                if (foundUUID != null) { // Someone owned this name before
                    database.deleteName(foundUUID, name);
                }
                database.insertName(uuid, name);
            }
        } else if (database instanceof MySQLDatabase) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ((MySQLDatabase) database).insertUUID(uuid);
                    database.insertName(uuid, name);
                }
            }.runTaskAsynchronously(plugin);
        } else {
            throw new UnsupportedOperationException("Oh noes, the database implementation isn't supported!");
        }
    }
}
