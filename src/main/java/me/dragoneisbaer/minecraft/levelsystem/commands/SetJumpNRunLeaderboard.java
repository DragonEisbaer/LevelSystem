package me.dragoneisbaer.minecraft.levelsystem.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                        if (!cfg.getBoolean("leaderboard.exists")) {
                            String jumpnrunname = f.getName().substring(0, f.getName().lastIndexOf("."));

                            ArmorStand leaderboardhead = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, 0.2, 0), EntityType.ARMOR_STAND);
                            ArmorStand leaderboarddifficulty = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -0.1, 0), EntityType.ARMOR_STAND);
                            ArmorStand highscorestext = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -0.4, 0), EntityType.ARMOR_STAND);

                            ArmorStand highscore1 = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -0.7, 0), EntityType.ARMOR_STAND);
                            ArmorStand highscore2 = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -0.9, 0), EntityType.ARMOR_STAND);
                            ArmorStand highscore3 = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -1.1, 0), EntityType.ARMOR_STAND);

                            Component header = Component.text(ChatColor.DARK_GREEN + jumpnrunname);
                            Component highscoretext = Component.text("Best Highscores:");

                            Component highscore1c;
                            Component highscore2c;
                            Component highscore3c;

                            if (get3Best(jumpnrunname).get(0) == 0 && get3Best(jumpnrunname).get(1) == 0 && get3Best(jumpnrunname).get(2) == 0) {
                                highscore1c = Component.text(ChatColor.RED + "Keine bisherigen Daten");
                                highscore2c = highscore1c;
                                highscore3c = highscore1c;
                            }else if (get3Best(jumpnrunname).get(0) == 0 && get3Best(jumpnrunname).get(1) == 0 && get3Best(jumpnrunname).get(2) != 0){
                                highscore1c = Component.text(ChatColor.GOLD + "#1 " + FormatTime(get3Best(jumpnrunname).get(2)));
                                highscore2c = Component.text(ChatColor.RED + "Keine weiteren Runs");
                                highscore3c = highscore2c;
                            }else if (get3Best(jumpnrunname).get(0) == 0 && get3Best(jumpnrunname).get(1) != 0 && get3Best(jumpnrunname).get(2) != 0){
                                highscore1c = Component.text(ChatColor.GOLD + "#1 " + FormatTime(get3Best(jumpnrunname).get(1)));
                                highscore2c = Component.text(ChatColor.GRAY + "#2 " + FormatTime(get3Best(jumpnrunname).get(2)));
                                highscore3c = Component.text(ChatColor.RED + "Keine weiteren Runs");
                            }else if (get3Best(jumpnrunname).get(0) != 0 && get3Best(jumpnrunname).get(1) != 0 && get3Best(jumpnrunname).get(2) != 0) {
                                highscore1c = Component.text(ChatColor.GOLD + "#1 " + FormatTime(get3Best(jumpnrunname).get(0)));
                                highscore2c = Component.text(ChatColor.GRAY + "#2 " + FormatTime(get3Best(jumpnrunname).get(1)));
                                highscore3c = Component.text(ChatColor.WHITE + "#3 " + FormatTime(get3Best(jumpnrunname).get(2)));
                            }else {
                                highscore1c = Component.text(ChatColor.RED + "Komisch");
                                highscore2c = highscore1c;
                                highscore3c = highscore1c;
                            }
                            setArmorStand(highscoretext, highscorestext);
                            setArmorStand(getDifficulty(cfg.getInt("Schwierigkeitsgrad")), leaderboarddifficulty);
                            setArmorStand(header, leaderboardhead);
                            setArmorStand(highscore1c, highscore1);
                            setArmorStand(highscore2c, highscore2);
                            setArmorStand(highscore3c, highscore3);

                            cfg.set("leaderboard.exists", true);
                            cfg.set("leaderboard.location", leaderboardhead.getLocation());
                            cfg.set("leaderboard.highscore1", highscore1.getUniqueId().toString());
                            cfg.set("leaderboard.highscore2", highscore2.getUniqueId().toString());
                            cfg.set("leaderboard.highscore3", highscore3.getUniqueId().toString());
                            try {
                                cfg.save(f);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            player.sendMessage(ChatColor.DARK_GREEN + "Leaderboard wurde erfolgreich erstellt!");
                        }else {
                            player.sendMessage(ChatColor.DARK_RED + "Dieses Leaderboard existiert bereits!");
                        }
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
                    if (cfg.getLong("timers." + jumpnrunname + ".best-time-millis") != 0) {
                        allhighscores.add(cfg.getLong("timers." + jumpnrunname + ".best-time-millis"));
                    }
                }
            }
        }
        if (allhighscores.isEmpty()) {
            allhighscores.add(0L);
            allhighscores.add(0L);
            allhighscores.add(0L);
        } else if (allhighscores.size() < 2) {
            allhighscores.add(0L);
            allhighscores.add(0L);
        }else {
            allhighscores.add(0L);
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