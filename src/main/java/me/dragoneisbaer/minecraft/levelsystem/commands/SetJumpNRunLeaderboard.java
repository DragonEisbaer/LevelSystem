package me.dragoneisbaer.minecraft.levelsystem.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SetJumpNRunLeaderboard implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender);
            if (player.hasPermission("levelsystem.createleaderboard")) {
                if (strings.length == 1) {
                    File f = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/JumpNRunLocations/" + strings[0] + ".yml");
                    if (f.exists()) {
                        String jumpnrunname = f.getName().substring(0, f.getName().lastIndexOf("."));
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

                        ArmorStand leaderboardhead = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, 0.2, 0), EntityType.ARMOR_STAND);
                        ArmorStand leaderboarddifficulty = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -0.1, 0), EntityType.ARMOR_STAND);
                        ArmorStand highscorestext = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -0.4, 0), EntityType.ARMOR_STAND);

                        ArmorStand highscore1 = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -0.7, 0), EntityType.ARMOR_STAND);
                        ArmorStand highscore2 = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -0.9, 0), EntityType.ARMOR_STAND);
                        ArmorStand highscore3 = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -1.1, 0), EntityType.ARMOR_STAND);

                        Component header = Component.text(ChatColor.DARK_GREEN + jumpnrunname);
                        Component highscoretext = Component.text("Best Highscores:");

                        Component highscore1c = Component.text(ChatColor.GOLD + "#1 " + FormatTime(get3Best(jumpnrunname).get(0)));
                        Component highscore2c = Component.text(ChatColor.GRAY + "#2 " + FormatTime(get3Best(jumpnrunname).get(1)));
                        Component highscore3c = Component.text(ChatColor.WHITE + "#3 " + FormatTime(get3Best(jumpnrunname).get(2)));

                        setArmorStand(highscoretext, highscorestext);
                        setArmorStand(getDifficulty(cfg.getInt("Schwierigkeitsgrad")), leaderboarddifficulty);
                        setArmorStand(header, leaderboardhead);
                        setArmorStand(highscore1c, highscore1);
                        setArmorStand(highscore2c, highscore2);
                        setArmorStand(highscore3c, highscore3);
                    } else {
                        player.sendMessage(ChatColor.RED + "Du musst einen gültigen JumpNRun Namen angeben!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Du musst einen JumpNRun Namen angeben!");
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Du hast keine Berechtigung dafür!");
            }
        } else {
            commandSender.sendMessage("Du musst ein Spieler sein!");
        }
        return true;
    }

    private Component getDifficulty(int i) {
        Component difficulty;
        switch (i) {
            case 1:
                difficulty = Component.text(ChatColor.GREEN + "Easy");
                break;
            case 2:
                difficulty = Component.text(ChatColor.YELLOW + "Medium");
                break;
            case 3:
                difficulty = Component.text(ChatColor.DARK_RED + "Hard");
                break;
            default:
                difficulty = Component.text(ChatColor.DARK_RED + "Level konnte nicht geladen werden!");
        }
        return difficulty;
    }
    private ArrayList<Long> get3Best(String jumpnrunname) {
        ArrayList<Long> allhighscores = new ArrayList<>();
        File folder = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/Spielerdaten/");
        if (folder.exists() && folder.listFiles() != null) {
            for (File f : folder.listFiles()) {
                if (f.exists()) {
                    File file = new File(f + "/general.yml");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                    allhighscores.add(cfg.getLong("timers." + jumpnrunname + ".best-time-millis"));
                }
            }
        }
        Collections.sort(allhighscores);
        while (allhighscores.size() > 3) {
            allhighscores.remove(allhighscores.size() - 1);
        }
        return allhighscores;
    }
    private void setArmorStand(Component name, ArmorStand armorStand) {
        armorStand.customName(name);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
    }
    private String FormatTime(long time) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
}