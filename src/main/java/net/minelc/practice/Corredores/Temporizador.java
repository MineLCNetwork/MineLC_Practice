package net.minelc.practice.Corredores;

import com.minelc.CORE.Controller.Jugador;
import ga.strikepractice.StrikePractice;
import ga.strikepractice.StrikePracticeAPI;
import net.minelc.practice.Escuchadores.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.TimerTask;

public class Temporizador extends TimerTask {
    PlayerListener playerListener = new PlayerListener();

    @Override
    public void run() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Jugador j = Jugador.getJugador(p);

            /* if (!StrikePracticeAPI.isInEvent(p) || !StrikePracticeAPI.isInFight(p)) {
                playerListener.setScoreboard(j);
            } */
            playerListener.setScoreboard(j);
        }
        // Bukkit.getLogger().info("[Debug] Scoreboard updateada at " + System.currentTimeMillis());
    }
}
