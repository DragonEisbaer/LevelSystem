package me.dragoneisbaer.minecraft.levelsystem.commands;

import me.dragoneisbaer.minecraft.levelsystem.LevelSystem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ReloadJumpNrun implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender.hasPermission("levelsystem.reloadlocations")) {

            LevelSystem plugin = JavaPlugin.getPlugin(LevelSystem.class);

            File folder = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/JumpNRunLocations/");
            if (folder.exists()) {
                List<File> files = new ArrayList<>();
                try {
                    files = Files.list(Paths.get(folder.toURI()))
                            .map(Path::toFile)
                            .filter(File::isFile)
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (File f : files) {
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    Location anfanglocation = new Location(Bukkit.getWorld(UUID.fromString(cfg.getString("Anfang.World"))), cfg.getDouble("Anfang.X"), cfg.getDouble("Anfang.Y"), cfg.getDouble("Anfang.Z"));
                    Location endelocation = new Location(Bukkit.getWorld(UUID.fromString(cfg.getString("Ende.World"))), cfg.getDouble("Ende.X"), cfg.getDouble("Ende.Y"), cfg.getDouble("Ende.Z"));
                    plugin.getJumpnrunloactions().put(anfanglocation, f.getName().substring(0, f.getName().lastIndexOf(".")) + "Anfang");
                    plugin.getJumpnrunloactions().put(endelocation, f.getName().substring(0, f.getName().lastIndexOf(".")) + "Ende");
                    Bukkit.getLogger().log(Level.INFO, "JumpNRun: " + f.getName().substring(0, f.getName().lastIndexOf(".")) + " geladen!");
                    if (commandSender instanceof Player) {
                        ((Player) commandSender).getPlayer().sendMessage(ChatColor.DARK_GREEN + ("JumpNRun: " + f.getName().substring(0, f.getName().lastIndexOf(".")) + " geladen!"));
                    }
                }
            }else {
                commandSender.sendMessage(ChatColor.RED + "Es gibt noch keine JumpNRuns!");
            }
        }else {
            commandSender.sendMessage(NamedTextColor.DARK_RED + "Du hast keine Berechtigung dafür!");
        }

        return true;
    }
}
