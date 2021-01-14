package net.minelc.practice;

import net.minecraft.server.v1_8_R3.PlayerList;
import net.minelc.practice.Comandos.InfoCMD;
import net.minelc.practice.Comandos.TopCMD;
import net.minelc.practice.Corredores.HeadUpdate;
import net.minelc.practice.Corredores.ReinicioProgramado;
import net.minelc.practice.Corredores.Temporizador;
import net.minelc.practice.Escuchadores.PlayerListener;
import net.minelc.practice.Escuchadores.WorldListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public final class Practice extends JavaPlugin {
    public static Practice instance;

    @Override
    public void onEnable() {
        Practice.instance = this;
        // Plugin startup logic
        Bukkit.getLogger().info(ChatColor.GREEN + "Cargando el plugin MineLC_Practice...");
        getCommand("practice").setExecutor(new InfoCMD());
        getCommand("top").setExecutor(new TopCMD());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ReinicioProgramado(), 20L, 20L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new HeadUpdate(), 20L, 600L);

        this.saveDefaultConfig();
        this.loadConfigInUTF();

//        this.getConfig().options().copyDefaults(true);
//        this.getConfig().options().copyHeader(true);

//        this.saveConfig();
//        this.loadConfigInUTF();
        Timer timer = new Timer();
        timer.schedule(new Temporizador(), 0, 5000); // 5 segundos
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info(ChatColor.RED + "Deshabilitando el plugin MineLC_Practice...");
    }

    public void loadConfigInUTF() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
            this.getConfig().load(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getIntConfig(String key, int defaultInt) {
        FileConfiguration config = this.getConfig();
        if(config.contains(key)) {
            if(config.isInt(key)) {
                return config.getInt(key);
            }
        }

        return defaultInt;
    }

    public String getStringConfig(String key, String defaultString) {
        FileConfiguration config = this.getConfig();
        if(config.contains(key)) {
            if(config.isString(key)) {
                return config.getString(key);
            }
        }

        return defaultString;
    }

    public boolean getBooleanConfig(String key, boolean defaultBool) {
        FileConfiguration config = this.getConfig();
        if(config.contains(key)) {
            if(config.isBoolean(key)) {
                return config.getBoolean(key);
            }
        }

        return defaultBool;
    }

    public static Practice getInstance() {
        return Practice.instance;
    }
}
