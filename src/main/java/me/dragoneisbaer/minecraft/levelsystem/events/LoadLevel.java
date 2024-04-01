package me.dragoneisbaer.minecraft.levelsystem.events;

import me.dragoneisbaer.minecraft.levelsystem.data.PlayerMemory;
import me.dragoneisbaer.minecraft.levelsystem.utility.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadLevel implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        File f = new File(PlayerUtility.getFolderPath(e.getPlayer()) + "/general.yml");
        PlayerMemory memory = PlayerUtility.getPlayerMemory(e.getPlayer());
        if (f.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            memory.setLevel(cfg.getInt("stats.level"));
            memory.setName(cfg.getString("stats.name"));
            e.getPlayer().sendMessage(ChatColor.GREEN + "Memory geladen! Level: " + memory.getLevel());
        }else {
            memory.setLevel(0);
            memory.setName(e.getPlayer().getName());
        }
        PlayerUtility.setPlayerMemory(e.getPlayer(), memory);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        PlayerMemory memory = PlayerUtility.getPlayerMemory(event.getPlayer());
        File f = new File(PlayerUtility.getFolderPath(event.getPlayer()) + "/general.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set("stats.level", memory.getLevel());
        cfg.set("stats.name", memory.getName());
        for (Team team : event.getPlayer().getScoreboard().getTeams()) {
            team.removeEntries(event.getPlayer().getName());
        }
        boolean playerisinteam;
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            int del = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerisinteam = team.getEntries().contains(player.getName());
                if (playerisinteam) {
                    del++;
                }
            }
            if (del == 0) {
                Bukkit.getLogger().log(Level.INFO, "Team gelöscht: " + team.getName());
                team.unregister();
            }else {
                System.out.println(del);
            }
        }
        try{cfg.save(f);}catch (IOException e) {e.printStackTrace();}
        PlayerUtility.setPlayerMemory(event.getPlayer(), null);
    }
}
