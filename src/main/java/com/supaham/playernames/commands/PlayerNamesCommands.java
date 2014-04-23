package com.supaham.playernames.commands;

import com.google.common.base.Joiner;
import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.Console;
import com.supaham.playernames.PlayerNamesPlugin;
import com.supaham.playernames.database.Database;
import com.supaham.playernames.database.MySQLDatabase;
import com.supaham.playernames.database.YAMLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class PlayerNamesCommands {

    private PlayerNamesPlugin plugin;

    public PlayerNamesCommands(PlayerNamesPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Command(aliases = {"names"},
             desc = "Gets names belonging to a uuid or player name.",
             usage = "<uuid/name>",
             min = 1,
             max = 1)
    @Console
    @CommandPermissions("playernames.cmd.names")
    public void names(CommandContext args, final CommandSender sender) throws CommandException {
        final String target = args.getString(0);
        final boolean isPlayer = target.length() <= 16;

        if (!isPlayer && target.length() != 36) {
            throw new CommandException("Please specify a player name (16 characters) or uuid (36 characters).");
        }

        final Player targetPlayer = isPlayer ? Bukkit.getPlayerExact(target) : null;
        final Database database = plugin.getDatabaseManager().getDatabase();

        if (database instanceof YAMLDatabase) {
            UUID uuid = !isPlayer ? UUID.fromString(target) :
                        targetPlayer != null ? targetPlayer.getUniqueId() :
                        // Not sure if I want to risk the mojang servers being down and the plugin breaking...
                        // Performance should be poop once there's a couple thousand entries
                        database.getUUID(target);
            if (uuid == null) {
                throw new CommandException("Could not find the specified " + (isPlayer ? "player" : "uuid") +
                                           " in the database.");
            }

            displayNames(sender, target, database.getNames(uuid));
        } else if (database instanceof MySQLDatabase) {
            new BukkitRunnable() {
                UUID uuid = !isPlayer ? UUID.fromString(target) :
                            targetPlayer != null ? targetPlayer.getUniqueId() : database.getUUID(target);

                @Override
                public void run() {
                    if (uuid == null) {
                        sender.sendMessage(ChatColor.RED + "Could not find the specified " +
                                           (isPlayer ? "player" : "uuid") + " in the database.");
                        return;
                    }
                    displayNames(sender, target, database.getNames(uuid));
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private void displayNames(CommandSender sender, String target, List<String> names) {
        if (names.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "That player has no names.");
            plugin.getLogger().warning(sender.getName() + " requested names belonging to " + target + " but there " +
                                       "were no results. This shouldn't be happening, please report this.");
            return;
        }
        String output = Joiner.on(ChatColor.GREEN + ", " + ChatColor.AQUA).join(names);
        sender.sendMessage(ChatColor.YELLOW + target + "'s names: " + ChatColor.AQUA + output);
    }
}
