package me.dragoneisbaer.minecraft.levelsystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DeleteLeaderBoard implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission("levelsystem.deleteleaderboard")) {
                if (strings.length == 1) {
                    double r = Double.parseDouble(strings[0])/2;
                    for (Entity entity : player.getLocation().getNearbyEntities(r,r,r)) {
                        if (entity instanceof ArmorStand) {
                            entity.remove();
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED+ "Gebe einen Radius an!");
                }
            }else {
                player.sendMessage(ChatColor.RED + "Dazu hast du keine Berechtigung");
            }
        }else {
            commandSender.sendMessage(ChatColor.RED + "Du musst ein Spieler sein.");
        }

        return true;
    }
}
