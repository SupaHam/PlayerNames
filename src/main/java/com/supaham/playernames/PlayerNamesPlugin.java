package com.supaham.playernames;

import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.Console;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.SimpleInjector;
import com.sk89q.minecraft.util.commands.UnhandledCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import com.supaham.playernames.commands.GeneralCommands;
import com.supaham.playernames.database.Database;
import com.supaham.playernames.database.DatabaseManager;
import com.supaham.playernames.database.YAMLDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;

/**
 * PlayerNames's main class.
 */
public class PlayerNamesPlugin extends JavaPlugin {

    public static PlayerNamesPlugin instance;
    private CommandsManager<CommandSender> commandsManager;
    private PlayerNamesConfiguration configuration;
    private PlayerNamesDBConfiguration dbConfiguration;
    private DatabaseManager databaseManager;

    public PlayerNamesPlugin() {
        PlayerNamesPlugin.instance = this;
    }

    @Override
    public void onEnable() {
        try {
            this.configuration = new PlayerNamesConfiguration(this, new File(getDataFolder(), "config.yml"));
            this.configuration.load();
            this.dbConfiguration = new PlayerNamesDBConfiguration(this, new File(getDataFolder(), "database.yml"));
            this.dbConfiguration.load();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            this.databaseManager = new DatabaseManager(this, dbConfiguration);
        } catch (FileNotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.commandsManager = new BukkitCommandsManager() {
            @Override
            protected void checkPermission(CommandSender sender, Method method) throws CommandException {
                if (!(sender instanceof Player) && !method.isAnnotationPresent(Console.class)) {
                    throw new UnhandledCommandException();
                }

                super.checkPermission(sender, method);
            }
        };
        this.commandsManager.setInjector(new SimpleInjector(this));
        CommandsManagerRegistration reg = new CommandsManagerRegistration(this, commandsManager);
        reg.register(GeneralCommands.class);

        getServer().getPluginManager().registerEvents(new PlayerNamesListener(this), this);
    }

    @Override
    public void onDisable() {
        Database db = getDatabaseManager().getDatabase();
        if(db instanceof YAMLDatabase) ((YAMLDatabase) db).save();
        instance = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            this.commandsManager.execute(command.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + "Usage: " + e.getUsage());
        } catch (WrappedCommandException e) {
            sender.sendMessage(ChatColor.RED + "An unknown error has occurred. Please notify an administrator.");
            e.printStackTrace();
        } catch (UnhandledCommandException e) {
            sender.sendMessage(ChatColor.RED + "Command could not be handled; invalid sender!");
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    public File getPluginFile() {
        return getFile();
    }

    /**
     * Gets the instance of this class.
     *
     * @return instance of PlayerNamesPlugin
     */
    public static PlayerNamesPlugin getInstance() {
        return instance;
    }

    /**
     * Gets the instance of {@link PlayerNamesConfiguration}.
     *
     * @return instance of PlayerNamesConfiguration
     */
    public PlayerNamesConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the instance of {@link PlayerNamesDBConfiguration}.
     *
     * @return instance of PlayerNamesDBConfiguration
     */
    public PlayerNamesDBConfiguration getDatabaseConfiguration() {
        return dbConfiguration;
    }

    /**
     * Gets the instance {@link DatabaseManager}
     *
     * @return instance of DatabaseManager
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
