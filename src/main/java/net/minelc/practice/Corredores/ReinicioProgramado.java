package net.minelc.practice.Corredores;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

public class ReinicioProgramado implements Runnable {
    int restartCount = 86400; // 24 hrs
    @Override
    public void run() {
        if(restartCount > 1 && restartCount <= 10) {
            Bukkit.broadcastMessage(ChatColor.RED+"¡El servidor será reiniciado en " + restartCount + " segundos!");
        } else if(restartCount == 1) {
            Bukkit.broadcastMessage(ChatColor.RED+"¡El servidor será reiniciado en " + restartCount + " segundo!");
            /* //clear entities
            for(World w : Bukkit.getWorlds()) {
                List<Entity> ents = w.getEntities();
                if(ents.size() > 250) {
                    for(Entity ent : w.getEntities()) {
                        if(ent.getType() != EntityType.PLAYER) {
                            ent.remove();
                        }
                    }
                }
            } */
        } else if(restartCount == 0) {
            Bukkit.shutdown();
        }
        restartCount--;
    }
}
