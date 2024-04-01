package me.dragoneisbaer.minecraft.levelsystem.utility;

import me.dragoneisbaer.minecraft.levelsystem.data.PlayerMemory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerUtility {
    private static final Map<String, PlayerMemory> playerMemory = new HashMap<>();
    public static PlayerMemory getPlayerMemory(Player player) {
        if (!playerMemory.containsKey(player.getUniqueId().toString())) {
            PlayerMemory m = new PlayerMemory();
            playerMemory.put(player.getUniqueId().toString(), m);
            return m;
        }
        return playerMemory.get(player.getUniqueId().toString());
    }

    public static void setPlayerMemory(Player player, PlayerMemory memory) {
        if (memory == null) playerMemory.remove(player.getUniqueId().toString());
        else playerMemory.put(player.getUniqueId().toString(), memory);
    }

    public static String getFolderPath(Player player) {
        return Bukkit.getPluginsFolder().getAbsolutePath() + "/Spielerdaten/" + player.getUniqueId();
    }
}
