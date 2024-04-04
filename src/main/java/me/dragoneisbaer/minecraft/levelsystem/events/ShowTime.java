package me.dragoneisbaer.minecraft.levelsystem.events;

import me.dragoneisbaer.minecraft.levelsystem.LevelSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
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
            final net.kyori.adventure.text.Component mainTitle = net.kyori.adventure.text.Component.text("Zeit: " + FormatTime(plugin.getJumpPlayerTime().get(player).getTime()), NamedTextColor.GOLD);
            final Title title = Title.title(mainTitle, Component.text(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(1000L), Duration.ZERO));
            player.showTitle(title);
        }
    }
    private String FormatTime(long time) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
}
