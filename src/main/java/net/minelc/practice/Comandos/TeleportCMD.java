package net.minelc.practice.Comandos;

import com.minelc.CORE.Controller.Jugador;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("¡Sólo jugadores pueden ejecutar este comando!");
            return true;
        }

        Player p = (Player) commandSender;
        Jugador j = Jugador.getJugador(p);

        if (!j.is_MODERADOR()) {
            p.sendMessage(ChatColor.RED + "Comando exclusivo para moderadores.");
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lMOD >&e Especifica un usuario"));
            return false;
        }

        Player pd = Bukkit.getPlayer(args[0]);

        if (pd == null) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lMOD >&c El usuario no se encuentra online."));
            return true;
        }

        // Location pdLocation = pd.getLocation();
        p.teleport(pd);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lMOD >&a Teletransportado a &e" + pd.getName()));
        return true;
    }
}

