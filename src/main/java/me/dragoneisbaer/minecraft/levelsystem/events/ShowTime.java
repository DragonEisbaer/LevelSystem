package me.dragoneisbaer.minecraft.levelsystem.events;

import me.dragoneisbaer.minecraft.levelsystem.LevelSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ShowTime implements Listener {
    LevelSystem plugin = JavaPlugin.getPlugin(LevelSystem.class);
    @EventHandler
    public void ShowTitle(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (plugin.getJumpPlayers().get(player) != null && plugin.getJumpPlayers().get(player)) {
            player.sendActionBar(ChatColor.GOLD + FormatTime(plugin.getJumpPlayerTime().get(player).getTime()));
        }
    }
    private String FormatTime(long time) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
}
