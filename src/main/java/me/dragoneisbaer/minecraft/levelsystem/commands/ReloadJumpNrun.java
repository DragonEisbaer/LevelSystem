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

            plugin.loadJumpNRunLocations();
            if (commandSender instanceof Player) {
                ((Player) commandSender).getPlayer().sendMessage(ChatColor.DARK_GREEN + "JumpNRuns geladen!");
            }
        }else {
            commandSender.sendMessage(NamedTextColor.DARK_RED + "Du hast keine Berechtigung dafür!");
        }

        return true;
    }
}
