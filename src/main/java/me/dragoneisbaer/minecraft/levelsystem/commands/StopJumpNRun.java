package me.dragoneisbaer.minecraft.levelsystem.commands;

import me.dragoneisbaer.minecraft.levelsystem.LevelSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class StopJumpNRun implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            if (player.hasPermission("levelsystem.stopjumpnrun")) {
                LevelSystem plugin = JavaPlugin.getPlugin(LevelSystem.class);
                if (plugin.getJumpPlayers().get(player)) {
                    plugin.getJumpPlayerTime().get(player).stop();
                    plugin.getAlreadyMessage().put(player, false);
                    plugin.getStartedMessage().put(player, false);
                    plugin.getJumpPlayers().put(player, false);
                    plugin.getJumpPlayerTime().get(player).reset();
                    final net.kyori.adventure.text.Component mainTitle = net.kyori.adventure.text.Component.text("JumpNRun: " + plugin.getJumpnnameplayer().get(player) + " abgebrochen!", NamedTextColor.DARK_RED);
                    final Title title = Title.title(mainTitle, Component.text(""));
                    player.showTitle(title);
                }else {
                    player.sendMessage(NamedTextColor.DARK_RED + "Du kannst kein JumpNRun beenden, wenn du angefangen hast!");
                }
            }else {
                player.sendMessage(NamedTextColor.DARK_RED + "Du hast dazu keine Berechtigung!");
            }
        }else {
            commandSender.sendMessage(NamedTextColor.DARK_RED + "Du hast musst ein Spieler sein.");
        }

        return true;
    }
}
