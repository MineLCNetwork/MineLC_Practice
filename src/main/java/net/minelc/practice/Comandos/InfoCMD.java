package net.minelc.practice.Comandos;

import net.minecraft.server.v1_8_R3.PlayerList;
import net.minelc.practice.Escuchadores.PlayerListener;
import net.minelc.practice.Practice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InfoCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player p = (Player) commandSender;
        PlayerListener pl = new PlayerListener();
        List<String> msg = Practice.getInstance().getConfig().getStringList("mensajes.practice");

        if (msg.isEmpty()) {
            return false;
        }

        for (String t : msg) {
            p.sendMessage(pl.formatterGlobal(p, t));
        }
        return true;
    }
}
