package me.dragoneisbaer.minecraft.levelsystem.events;

import com.google.common.base.Stopwatch;
import me.dragoneisbaer.minecraft.levelsystem.LevelSystem;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Random;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class JumpNRunStartTimer implements Listener {

    LevelSystem plugin = JavaPlugin.getPlugin(LevelSystem.class);
    //Video Tasks: https://www.youtube.com/watch?v=puMc0YviVkA
    @EventHandler
    public void PlayerJumpNRunStart(PlayerMoveEvent e) {
        HashMap<Location,String> jumpnrunloactions = plugin.getJumpnrunloactions();
        Player player = e.getPlayer();
        for (Location location : jumpnrunloactions.keySet()) {
            String name = jumpnrunloactions.get(location);
            if (location.getNearbyPlayers(0.1).contains(player))  {
                if (name.contains("Anfang")) {
                    String jumpnrunname = name.substring(0,name.length()-6);
                    if (!plugin.getJumpPlayers().get(player)) {
                        StopWatch timer = new StopWatch();
                        timer.start();
                        plugin.SetPlayerJumpTime(player, timer);
                        plugin.setJumpPlayerName(player, jumpnrunname);
                        plugin.setJumpplayers(player, true);
                        player.sendMessage(ChatColor.GREEN + "JumpNRun: " + jumpnrunname + " wurde gestartet!");
                    }else {
                        player.sendMessage(ChatColor.RED + "Du hast bereits ein JumpNRun gestartet.");
                    }
                }else if (name.contains("Ende") && name.substring(name.length()-4).equalsIgnoreCase("Ende")){
                    String jumpnrunname = name.substring(0,name.length()-4);
                    if (jumpnrunname.equalsIgnoreCase(plugin.getJumpnnameplayer().get(player))) {
                         if (plugin.getJumpPlayers().get(player)) {
                             plugin.getJumpPlayerTime().get(player).stop();
                             plugin.getJumpPlayers().put(player, false);
                             long time = plugin.getJumpPlayerTime().get(player).getTime();
                             player.sendMessage(ChatColor.GOLD + "Das ist das JumpNRun Ende von " + jumpnrunname + "!");
                             player.sendMessage(ChatColor.GOLD + "Zeit: " + FormatTime(time));
                         }
                    }else {
                        player.sendMessage(ChatColor.DARK_RED + "Das ist zwar ein JumpNRunEnde, aber nicht deins!");
                    }
                }else {
                    player.sendMessage("dsad");
                }
            }
        }
    }

    private String FormatTime(long time) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
}