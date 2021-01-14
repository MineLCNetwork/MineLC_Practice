package net.minelc.practice.Comandos;

import ga.strikepractice.StrikePractice;
import ga.strikepractice.StrikePracticeAPI;
import net.minelc.practice.Controladores.Inventarios;
import net.minelc.practice.Escuchadores.PlayerListener;
import net.minelc.practice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TopCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("COMANDO EXCLUSIVO PARA JUGADORES.");
            return false;
        }

        Player p = (Player) commandSender;
        if (StrikePracticeAPI.isInFight(p) || StrikePracticeAPI.isInFight(p)) {
            p.sendMessage(ChatColor.RED + "No puedes abrir el top en una partida o evento.");
            return false;
        }

        Inventarios inv = new Inventarios();
        inv.getInvStats_PRAC().open(p);
        return true;
    }
}
