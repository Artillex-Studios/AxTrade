package com.artillexstudios.axtrade.commands;

import com.artillexstudios.axtrade.commands.subcommands.Accept;
import com.artillexstudios.axtrade.commands.subcommands.Deny;
import com.artillexstudios.axtrade.commands.subcommands.Force;
import com.artillexstudios.axtrade.commands.subcommands.Preview;
import com.artillexstudios.axtrade.commands.subcommands.Reload;
import com.artillexstudios.axtrade.commands.subcommands.Request;
import com.artillexstudios.axtrade.commands.subcommands.Toggle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;

@CommandPermission(value = "axtrade.trade")
public class Commands implements OrphanCommand {

    @DefaultFor("~")
    public void trade(Player sender, @Optional Player other) {
        Request.INSTANCE.execute(sender, other);
    }

    @Subcommand("accept")
    public void accept(Player sender, Player other) {
        Accept.INSTANCE.execute(sender, other);
    }

    @Subcommand("deny")
    public void deny(Player sender, Player other) {
        Deny.INSTANCE.execute(sender, other);
    }

    @CommandPermission("axtrade.toggle")
    @Subcommand("toggle")
    public void toggle(Player sender) {
        Toggle.INSTANCE.execute(sender);
    }

    @Subcommand("reload")
    @CommandPermission(value = "axtrade.admin")
    public void reload(CommandSender sender) {
        Reload.INSTANCE.execute(sender);
    }

    @Subcommand("force")
    @CommandPermission(value = "axtrade.admin")
    public void force(Player sender, Player other) {
        Force.INSTANCE.execute(sender, other);
    }

    @Subcommand("preview")
    @CommandPermission(value = "axtrade.admin")
    public void preview(Player sender) {
        Preview.INSTANCE.execute(sender);
    }
}
