package me.dragoneisbaer.minecraft.levelsystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class SetJumpStart implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            if (strings.length == 1 && player.hasPermission("levelsystem.jumpnrunstart")) {
                Location playerloc = player.getLocation();
                File f = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/JumpNRunLocations/" + strings[0] + ".yml");
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                cfg.set("Anfang.World", playerloc.getWorld().getUID().toString());
                cfg.set("Anfang.Y", playerloc.getY());
                cfg.set("Anfang.X", playerloc.getX());
                cfg.set("Anfang.Z", playerloc.getZ());
                try {
                    cfg.save(f);
                    player.sendMessage(ChatColor.GREEN + "JumpNRunStart Location gespeichert." + " Name: " + strings[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                player.sendMessage(ChatColor.RED + "Setze einen Namen fest!");
            }
        }else {
            commandSender.sendMessage(ChatColor.RED + "Du musst ein Spieler sein.");
        }

        return true;
    }
}
