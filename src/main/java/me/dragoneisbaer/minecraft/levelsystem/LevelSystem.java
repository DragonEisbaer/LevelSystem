package me.dragoneisbaer.minecraft.levelsystem;

import me.dragoneisbaer.minecraft.levelsystem.commands.*;
import me.dragoneisbaer.minecraft.levelsystem.data.PlayerMemory;
import me.dragoneisbaer.minecraft.levelsystem.events.DisplayLevel;
import me.dragoneisbaer.minecraft.levelsystem.events.JumpNRunStartTimer;
import me.dragoneisbaer.minecraft.levelsystem.events.LoadLevel;
import me.dragoneisbaer.minecraft.levelsystem.events.ShowTime;
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
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class LevelSystem extends JavaPlugin {

    private final HashMap<Location, String> jumpnrunloactions = new HashMap<>();
    private final HashMap<Player, Boolean> jumpplayers = new HashMap<>();
    private final HashMap<Player, String> jumpnnameplayer = new HashMap<>();
    private final HashMap<Player, StopWatch> playerjumptime = new HashMap<>();
    private final HashMap<Player, Boolean> startedMessage = new HashMap<>();
    private final HashMap<Player, Boolean> alreadymessage = new HashMap<>();
    private final HashMap<Player, Integer> jumpdifficulty = new HashMap<>();

    //Storing data: https://www.youtube.com/watch?v=NjfnfGghLuw
    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "[LevelSystem] started.");

        Bukkit.getPluginManager().registerEvents(new LoadLevel(), this);
        Bukkit.getPluginManager().registerEvents(new DisplayLevel(), this);
        Bukkit.getPluginManager().registerEvents(new JumpNRunStartTimer(), this);
        Bukkit.getPluginManager().registerEvents(new ShowTime(), this);

        getCommand("setlevel").setExecutor(new SetLevel());
        getCommand("stopjumpnrun").setExecutor(new StopJumpNRun());
        getCommand("reloadjumpnrun").setExecutor(new ReloadJumpNrun());
        getCommand("setjumpstart").setExecutor(new SetJumpStart());
        getCommand("setjumpende").setExecutor(new SetJumpEnd());

        loadPlayerData();
        loadJumpNRunLocations();
        setAllPlayerJumpNormal();

    }

    @Override
    public void onDisable(){
        getLogger().log(Level.INFO, "[LevelSystem] shutdown.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerMemory memory = PlayerUtility.getPlayerMemory(player);
            File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            cfg.set("stats.level", memory.getLevel());
            //cfg.set("stats.exp", memory.getExp());
            cfg.set("stats.name", memory.getName());
            try{cfg.save(f);
                getLogger().log(Level.INFO, "[LevelSystemData] saved.");}
            catch (IOException e){e.printStackTrace();}
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
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                cfg.set("stats.exp", 0);
                memory.setLevel(0);
                memory.setName(player.getName());
                try {
                    cfg.save(f);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            PlayerUtility.setPlayerMemory(player, memory);
            getLogger().log(Level.INFO, "[LevelSystemData] loaded.");
        }
    }
    public void loadJumpNRunLocations() {
        File folder = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/JumpNRunLocations/");
        List<File> files = new ArrayList<>();
        if (folder.exists()) {
            try {
                files = Files.list(Paths.get(folder.toURI()))
                        .map(Path::toFile)
                        .filter(File::isFile)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (File f : files) {
                getLogger().log(Level.INFO, "JumpNRun: " + f.getName().substring(0, f.getName().lastIndexOf(".")) + " geladen!");
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                Location anfanglocation = new Location(Bukkit.getWorld(cfg.getString("Anfang.World")), cfg.getDouble("Anfang.X"), cfg.getDouble("Anfang.Y"), cfg.getDouble("Anfang.Z"));
                Location endelocation = new Location(Bukkit.getWorld(cfg.getString("Ende.World")), cfg.getDouble("Ende.X"), cfg.getDouble("Ende.Y"), cfg.getDouble("Ende.Z"));
                jumpnrunloactions.put(anfanglocation, f.getName().substring(0, f.getName().lastIndexOf(".")) + "Anfang");
                jumpnrunloactions.put(endelocation, f.getName().substring(0, f.getName().lastIndexOf(".")) + "Ende");
            }
        }else {
            this.getLogger().log(Level.WARNING, "Es gibt noch keine JumpNRuns!");
        }
    }
    private void setAllPlayerJumpNormal() {
        jumpplayers.replaceAll((p, v) -> false);
        alreadymessage.replaceAll((p, v) -> false);
        startedMessage.replaceAll((p, v) -> false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            jumpplayers.put(player, false);
            alreadymessage.put(player, false);
            startedMessage.put(player, false);
            jumpdifficulty.put(player, 1);
        }
        jumpnnameplayer.replaceAll((p, v) -> "");
        playerjumptime.replaceAll((p, v) -> null);
        jumpdifficulty.replaceAll((p, v) -> 1);
    }

    public HashMap<Player,Boolean> getJumpPlayers() {
        return jumpplayers;
    }
    public HashMap<Player, StopWatch> getJumpPlayerTime() {
        return playerjumptime;
    }
    public HashMap<Player,String> getJumpnnameplayer(){
        return jumpnnameplayer;
    }
    public HashMap<Player, Boolean> getStartedMessage() {
        return startedMessage;
    }
    public HashMap<Player, Boolean> getAlreadyMessage() {
        return alreadymessage;
    }
    public HashMap<Player, Integer> getPlayerJumpDifficulty() {
        return jumpdifficulty;
    }
}