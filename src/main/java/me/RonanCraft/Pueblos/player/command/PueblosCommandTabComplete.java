package me.RonanCraft.Pueblos.player.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface PueblosCommandTabComplete {

    List<String> tabComplete(CommandSender sendi, String[] args);
}
