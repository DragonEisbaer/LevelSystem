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
                if (strings.length == 2) {
                    File f = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/JumpNRunLocations/" + strings[0] + ".yml");
                    if (f.exists()) {
                        double r = Double.parseDouble(strings[1])/2;
                        for (Entity entity : player.getLocation().getNearbyEntities(r,r,r)) {
                            if (entity instanceof ArmorStand) {
                                entity.remove();
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Gebe einen gültigen Namen an.");
                    }
                } else if (strings.length == 1) {
                    player.sendMessage(ChatColor.RED + "Gebe einen Radius an.");
                } else {
                    player.sendMessage(ChatColor.RED+ "Gebe einen Namen an!");
                }
            }
        }else {
            commandSender.sendMessage(ChatColor.RED + "Du musst ein Spieler sein.");
        }

        return true;
    }
}
