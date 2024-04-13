package me.dragoneisbaer.minecraft.levelsystem.events;

import com.google.common.base.Stopwatch;
import me.dragoneisbaer.minecraft.levelsystem.LevelSystem;
import me.dragoneisbaer.minecraft.levelsystem.data.PlayerMemory;
import me.dragoneisbaer.minecraft.levelsystem.utility.PlayerUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Random;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class JumpNRunStartTimer implements Listener {

    LevelSystem plugin = JavaPlugin.getPlugin(LevelSystem.class);
    //Video Tasks: https://www.youtube.com/watch?v=puMc0YviVkA
    @EventHandler
    public void PlayerJumpNRunStart(PlayerMoveEvent e) {
        HashMap<Location,String> jumpnrunloactions = plugin.getJumpnrunloactions();
        Player player = e.getPlayer();
        for (Location location : jumpnrunloactions.keySet()) {
            String name = jumpnrunloactions.get(location);
            File f1 = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/JumpNRunLocations/" + "1" + ".yml");
            FileConfiguration cfg1 = YamlConfiguration.loadConfiguration(f1);
            System.out.println(cfg1.getString("Anfang.World"));
            System.out.println(Bukkit.getWorld(cfg1.getString("Anfang.World")));
            System.out.println(location);
            if (location.getNearbyPlayers(0.1).contains(player))  {
                if (name.contains("Anfang")) {
                    String jumpnrunname = name.substring(0,name.length()-6);
                    if (!plugin.getJumpPlayers().get(player)) {
                        StopWatch timer = new StopWatch();
                        timer.start();
                        plugin.getJumpPlayerTime().put(player, timer);
                        plugin.getJumpnnameplayer().put(player, jumpnrunname);
                        plugin.getJumpPlayers().put(player, true);
                        File f = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/JumpNRunLocations/" + jumpnrunname + ".yml");
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                        plugin.getPlayerJumpDifficulty().put(player, cfg.getInt("Schwierigkeitsgrad"));
                        player.sendMessage(ChatColor.GREEN + "JumpNRun: " + jumpnrunname + " wurde gestartet!");
                    }else {
                        if (!plugin.getStartedMessage().get(player)) {
                            player.sendMessage(ChatColor.RED + "Du hast bereits ein JumpNRun gestartet.");
                            plugin.getStartedMessage().put(player, true);
                        }
                    }
                }else if (name.contains("Ende") && name.substring(name.length()-4).equalsIgnoreCase("Ende")){
                    String jumpnrunname = name.substring(0,name.length()-4);
                    if (jumpnrunname.equalsIgnoreCase(plugin.getJumpnnameplayer().get(player))) {
                         if (plugin.getJumpPlayers().get(player)) {
                             plugin.getJumpPlayerTime().get(player).stop();
                             plugin.getAlreadyMessage().put(player, false);
                             plugin.getStartedMessage().put(player, false);
                             plugin.getJumpPlayers().put(player, false);
                             long time = plugin.getJumpPlayerTime().get(player).getTime();
                             plugin.getJumpPlayerTime().get(player).reset();
                             final Component mainTitle = Component.text("JumpNRun: " + jumpnrunname + " abgeschlossen!", NamedTextColor.GREEN);
                             final Component subtitle = Component.text("Zeit: " + FormatTime(time), NamedTextColor.GOLD);
                             final Title title = Title.title(mainTitle, subtitle);
                             player.showTitle(title);

                             File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");
                             FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                             int exp = cfg.getInt("stats.exp");
                             switch (plugin.getPlayerJumpDifficulty().get(player)) {
                                 case 1:
                                     player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 5));
                                     exp = exp + 10;
                                     break;
                                 case 2:
                                     player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 10));
                                     exp = exp + 20;
                                     break;
                                 case 3:
                                     player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 20));
                                     exp = exp + 30;
                                     break;
                                 default:
                                     plugin.getLogger().log(Level.SEVERE, "Level konnte nicht geladen werden. " + "Player: " + player);
                             }
                             cfg.set("stats.exp", exp);
                             CheckForLevelUP(exp, cfg, f, player);
                             plugin.getPlayerJumpDifficulty().put(player, 1);
                             if (time < cfg.getLong("timers." + jumpnrunname + ".best-time-millis")) {
                                 cfg.set("timers." + jumpnrunname + ".best-time-millis", time);
                                 cfg.set("timers." + jumpnrunname + ".best-time-formated", FormatTime(time));
                             }
                             try{cfg.save(f);}catch (IOException ee){ee.printStackTrace();}
                         }
                    }else {
                        if (!plugin.getAlreadyMessage().get(player)) {
                            player.sendMessage(ChatColor.DARK_RED + "Das ist zwar ein JumpNRunEnde, aber nicht deins!");
                            plugin.getAlreadyMessage().put(player, true);
                        }
                    }
                }
            }
        }
    }

    private String FormatTime(long time) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
    private void CheckForLevelUP(int exp, FileConfiguration cfg, File file, Player player){
        if (exp >= GetRequiredXP(cfg)) {
            if (cfg.getInt("stats.level") <= 999) {
                cfg.set("stats.level", cfg.getInt("stats.level") + 1);
                cfg.set("stats.exp", cfg.getInt("stats.exp") - 100);
                PlayerMemory memory = PlayerUtility.getPlayerMemory(player);
                memory.setLevel(cfg.getInt("stats.level"));
                PlayerUtility.setPlayerMemory(player, memory);
                try {
                    cfg.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int GetRequiredXP(FileConfiguration cfg) {
        int level = cfg.getInt("stats.level");
        if (level <= 49) {
            return 100;
        } else if (level <= 99) {
            return 150;
        } else if (level <= 299) {
            return 200;
        } else if (level <= 499) {
            return 250;
        } else if (level <= 999) {
            return 300;
        } else if (level == 1000) {
            return 350;
        }else {
            return (-1);
        }
    }
}