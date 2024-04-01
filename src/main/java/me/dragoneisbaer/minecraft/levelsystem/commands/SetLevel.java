package me.dragoneisbaer.minecraft.levelsystem.commands;

import me.dragoneisbaer.minecraft.levelsystem.data.PlayerMemory;
import me.dragoneisbaer.minecraft.levelsystem.utility.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;

public class SetLevel implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            if (player.hasPermission("levelsystem.setlevel")) {
                if (strings.length == 1) {
                    PlayerMemory memory = PlayerUtility.getPlayerMemory(player);
                    File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");
                    boolean numeric;
                    try {
                        Integer.parseInt(strings[0]);
                        numeric = true;
                    } catch (NumberFormatException e) {
                        numeric = false;
                    }
                    if (numeric) {
                        memory.setLevel(Integer.parseInt(strings[0]));
                        PlayerUtility.setPlayerMemory(player, memory);
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                        cfg.set("stats.level", memory.getLevel());
                        try {
                            cfg.save(f);
                            player.sendMessage(ChatColor.GREEN + "Level gespeichert! Level: " + cfg.getInt("stats.level"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + strings[0] + " ist kein zulässiges Level!");
                    }
                }else {
                    player.sendMessage(ChatColor.RED + "Gebe ein Level an!");
                }
            }else {
                player.sendMessage(ChatColor.RED + "Unzureichende Berechtigung.");
            }
        }else if (commandSender instanceof ConsoleCommandSender) {
            if (strings.length <= 1) {
                commandSender.sendMessage(ChatColor.RED + "Setze einen Spieler und das Level fest.");
            }else if (strings.length == 2){
                boolean numeric;
                try {
                    Integer.parseInt(strings[0]);
                    numeric =  true;
                } catch(NumberFormatException e){
                    numeric = false;
                }
                if (numeric && Integer.parseInt(strings[0]) >= 0) {
                    Player player = Bukkit.getPlayerExact(strings[1]);
                    if (player != null) {
                        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);
                        File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");
                        memory.setLevel(Integer.parseInt(strings[0]));
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                        cfg.set("stats.level", memory.getLevel());
                        try {
                            cfg.save(f);
                            commandSender.sendMessage(ChatColor.GREEN + "Level gespeichert! Level: " + memory.getLevel());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        commandSender.sendMessage(ChatColor.RED + "Das ist keine Spieler!");
                    }
                }else {
                    commandSender.sendMessage(ChatColor.RED + strings[0] + " ist kein zulässiges Level!");
                }
            }
        }
        return true;
    }
}
