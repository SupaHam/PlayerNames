package com.supaham.playernames.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.Console;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.supaham.playernames.PlayerNamesPlugin;
import org.bukkit.command.CommandSender;

public class GeneralCommands {

    private PlayerNamesPlugin plugin;

    public GeneralCommands(PlayerNamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = {"playernames", "pn", "playern", "pnames"},
             desc = "Main command for the PlayerNames plugin")
    @Console
    @CommandPermissions("playernames")
    @NestedCommand(PlayerNamesCommands.class)
    public void playerNames(CommandContext args, CommandSender sender) throws CommandException {
    }
}
