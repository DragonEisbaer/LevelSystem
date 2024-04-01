package me.dragoneisbaer.minecraft.levelsystem.events;

import me.dragoneisbaer.minecraft.levelsystem.utility.PlayerUtility;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Team;

import java.io.File;

public class DisplayLevel implements Listener {

    @EventHandler
    public void DisplayLevel(PlayerMoveEvent e){
        Player player =  e.getPlayer();

        File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        String prefix = "";
        if (cfg.getInt("stats.level") <= 49) {
            prefix = ChatColor.GRAY + "[" + ChatColor.WHITE + cfg.getInt("stats.level") + ChatColor.GRAY + "] ";
        } else if (cfg.getInt("stats.level") <= 99) {
            prefix = ChatColor.GRAY + "[" + ChatColor.GREEN + cfg.getInt("stats.level") + ChatColor.GRAY + "] ";
        } else if (cfg.getInt("stats.level") <= 299) {
            prefix = ChatColor.GRAY + "[" + ChatColor.BLUE + cfg.getInt("stats.level") + ChatColor.GRAY + "] ";
        } else if (cfg.getInt("stats.level") <= 499) {
            prefix = ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + org.bukkit.ChatColor.MAGIC + "1" + ChatColor.DARK_PURPLE + cfg.getInt("stats.level") + org.bukkit.ChatColor.MAGIC + "1" + ChatColor.GRAY + "] ";
        } else if (cfg.getInt("stats.level") <= 999) {
            prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[" + ChatColor.RED + org.bukkit.ChatColor.MAGIC + "1" + ChatColor.ITALIC + ChatColor.RED + ChatColor.ITALIC + cfg.getInt("stats.level") + ChatColor.ITALIC + org.bukkit.ChatColor.MAGIC + "1" + ChatColor.GRAY + ChatColor.ITALIC + "] ";
        } else if (cfg.getInt("stats.level") == 1000) {
            prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[" + ChatColor.YELLOW + org.bukkit.ChatColor.MAGIC + "1" + ChatColor.YELLOW + ChatColor.ITALIC + cfg.getInt("stats.level") + ChatColor.ITALIC + org.bukkit.ChatColor.MAGIC + "1" + ChatColor.GRAY + ChatColor.ITALIC + "] ";
        }else {
            player.sendMessage(org.bukkit.ChatColor.RED + "Dieses Level ist nicht zulässig!");
        }

        if (!player.getScoreboard().getTeams().contains(player.getScoreboard().getTeam("Level" + cfg.getInt("stats.level")))) {
            player.getScoreboard().registerNewTeam("Level"+ cfg.getInt("stats.level"));
            Team team = player.getScoreboard().getTeam("Level" + cfg.getInt("stats.level"));
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.setPrefix(prefix);
            team.addPlayer(player);
        }else {
            Team team = player.getScoreboard().getTeam("Level" + cfg.getInt("stats.level"));
            team.setPrefix(prefix);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.addPlayer(player);
        }
    }
}
