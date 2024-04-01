package me.dragoneisbaer.minecraft.levelsystem;

import me.dragoneisbaer.minecraft.levelsystem.commands.SetJumpEnd;
import me.dragoneisbaer.minecraft.levelsystem.commands.SetJumpStart;
import me.dragoneisbaer.minecraft.levelsystem.commands.SetLevel;
import me.dragoneisbaer.minecraft.levelsystem.data.PlayerMemory;
import me.dragoneisbaer.minecraft.levelsystem.events.DisplayLevel;
import me.dragoneisbaer.minecraft.levelsystem.events.JumpNRunStartTimer;
import me.dragoneisbaer.minecraft.levelsystem.events.LoadLevel;
import me.dragoneisbaer.minecraft.levelsystem.utility.PlayerUtility;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class LevelSystem extends JavaPlugin {

    HashMap<Location, String> jumpnrunloactions = new HashMap<>();
    HashMap<Player, Boolean> jumpplayers = new HashMap<>();
    HashMap<Player, String> jumpnnameplayer = new HashMap<>();
    HashMap<Player, StopWatch> playerjumptime = new HashMap<>();

    //Storing data: https://www.youtube.com/watch?v=NjfnfGghLuw
    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "[LevelSystem] started.");

        Bukkit.getPluginManager().registerEvents(new LoadLevel(), this);
        Bukkit.getPluginManager().registerEvents(new DisplayLevel(), this);
        Bukkit.getPluginManager().registerEvents(new JumpNRunStartTimer(), this);

        getCommand("setlevel").setExecutor(new SetLevel());
        getCommand("setjumpstart").setExecutor(new SetJumpStart());
        getCommand("setjumpende").setExecutor(new SetJumpEnd());

        loadPlayerData();
        loadJumpNRunLocations();
        setAllPlayerJumpNull();

    }

    @Override
    public void onDisable(){
        // Plugin shutdown logic
        getLogger().log(Level.INFO, "[LevelSystem] shutdown.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerMemory memory = PlayerUtility.getPlayerMemory(player);
            File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            cfg.set("stats.level", memory.getLevel());
            cfg.set("stats.name", memory.getName());
            try{cfg.save(f);
                getLogger().log(Level.INFO, "[LevelSystemData] saved.");}
            catch (IOException e){e.printStackTrace();};
        }
    }

    public HashMap<Location,String> getJumpnrunloactions() {
        return jumpnrunloactions;
    }

    private void loadPlayerData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");
            PlayerMemory memory = PlayerUtility.getPlayerMemory(player);
            if (f.exists()) {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                memory.setLevel(cfg.getInt("stats.level"));
                memory.setName(cfg.getString("stats.name"));
                player.sendMessage(ChatColor.GREEN + "Memory geladen! Level: " + memory.getLevel());
            }else {
                memory.setLevel(0);
                memory.setName(player.getName());
            }
            PlayerUtility.setPlayerMemory(player, memory);
            getLogger().log(Level.INFO, "[LevelSystemData] loaded.");
        }
    }
    private void loadJumpNRunLocations() {
        File folder = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/JumpNRunLocations/");
        List<File> files = new ArrayList<>();
        try {
            files = Files.list(Paths.get(folder.toURI()))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File f : files) {
            getLogger().log(Level.INFO, "JumpNRun: " + f.getName().substring(0,f.getName().lastIndexOf(".")) + " geladen!");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            Location anfanglocation = new Location(Bukkit.getWorld(cfg.getString("Anfang.World")), cfg.getDouble("Anfang.X"), cfg.getDouble("Anfang.Y"), cfg.getDouble("Anfang.Z"));
            Location endelocation = new Location(Bukkit.getWorld(cfg.getString("Ende.World")), cfg.getDouble("Ende.X"), cfg.getDouble("Ende.Y"), cfg.getDouble("Ende.Z"));
            jumpnrunloactions.put(anfanglocation, f.getName().substring(0,f.getName().lastIndexOf(".")) + "Anfang");
            jumpnrunloactions.put(endelocation, f.getName().substring(0,f.getName().lastIndexOf(".")) + "Ende");
        }
    }

    public HashMap<Player,Boolean> getJumpPlayers() {
        return jumpplayers;
    }
    public void setJumpplayers(Player player, Boolean bool) {
        jumpplayers.put(player, bool);
    }
    public HashMap<Player, StopWatch> getJumpPlayerTime() {
        return playerjumptime;
    }
    private void setAllPlayerJumpNull() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            jumpplayers.put(player, false);
            jumpnnameplayer.put(player, null);
            playerjumptime.put(player, null);
        }
    }
    public void SetPlayerJumpTime (Player player, StopWatch time) {
        playerjumptime.put(player, time);
    }
    public HashMap<Player,String> getJumpnnameplayer(){
        return jumpnnameplayer;
    }

    public void setJumpPlayerName(Player player, String jumpnrunname) {
        jumpnnameplayer.put(player, jumpnrunname);
    }
}