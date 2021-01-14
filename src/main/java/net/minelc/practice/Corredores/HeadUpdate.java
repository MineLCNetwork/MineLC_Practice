package net.minelc.practice.Corredores;

import ga.strikepractice.stats.Stats;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minelc.practice.Practice;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

import java.util.TimerTask;

public class HeadUpdate implements Runnable {
    Block Head1 = Bukkit.getWorld(Practice.getInstance().getStringConfig("headsloc.top1.world", "spawn")).getBlockAt(Practice.getInstance().getIntConfig("headsloc.top1.x", 10), Practice.getInstance().getIntConfig("headsloc.top1.y", 60), Practice.getInstance().getIntConfig("headsloc.top1.z", 12));
    Block Head2 = Bukkit.getWorld(Practice.getInstance().getStringConfig("headsloc.top2.world", "spawn")).getBlockAt(Practice.getInstance().getIntConfig("headsloc.top2.x", 12), Practice.getInstance().getIntConfig("headsloc.top2.y", 59), Practice.getInstance().getIntConfig("headsloc.top2.z", 11));
    Block Head3 = Bukkit.getWorld(Practice.getInstance().getStringConfig("headsloc.top3.world", "spawn")).getBlockAt(Practice.getInstance().getIntConfig("headsloc.top3.x", 8), Practice.getInstance().getIntConfig("headsloc.top3.y", 59), Practice.getInstance().getIntConfig("headsloc.top3.z", 11));

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        if (Practice.getInstance().getBooleanConfig("headsloc.top1.enabled", false)) {
            Head1.setType(Material.SKULL);
            Head1.setData((byte) 0x1);
            BlockState state1 = Head1.getState();
            if (state1 instanceof Skull) {
                OfflinePlayer fakeplayer1 = Bukkit.getServer().getOfflinePlayer("ElBuenAnvita");
                Skull skull1 = (Skull) state1;
                skull1.setRotation(BlockFace.NORTH);
                skull1.setSkullType(SkullType.PLAYER);
                String top1 = PlaceholderAPI.setPlaceholders(fakeplayer1, Practice.getInstance().getStringConfig("headsloc.top1.placeholder", "%strikepractice_top_global_elo1%"));
                skull1.setOwner(top1);
                skull1.update();
            }
        }

        if (Practice.getInstance().getBooleanConfig("headsloc.top2.enabled", false)) {
            Head2.setType(Material.SKULL);
            Head2.setData((byte) 0x1);
            BlockState state2 = Head2.getState();
            if (state2 instanceof Skull) {
                OfflinePlayer fakeplayer1 = Bukkit.getServer().getOfflinePlayer("ElBuenAnvita");
                Skull skull2 = (Skull) state2;
                skull2.setRotation(BlockFace.NORTH);
                skull2.setSkullType(SkullType.PLAYER);
                String top2 = PlaceholderAPI.setPlaceholders(fakeplayer1, Practice.getInstance().getStringConfig("headsloc.top2.placeholder", "%strikepractice_top_global_elo2%"));
                skull2.setOwner(top2);
                skull2.update();
            }
        }

        if (Practice.getInstance().getBooleanConfig("headsloc.top3.enabled", false)) {
            Head3.setType(Material.SKULL);
            Head3.setData((byte) 0x1);
            BlockState state3 = Head3.getState();
            if (state3 instanceof Skull) {
                OfflinePlayer fakeplayer1 = Bukkit.getServer().getOfflinePlayer("ElBuenAnvita");
                Skull skull3 = (Skull) state3;
                skull3.setRotation(BlockFace.NORTH);
                skull3.setSkullType(SkullType.PLAYER);
                String top3 = PlaceholderAPI.setPlaceholders(fakeplayer1, Practice.getInstance().getStringConfig("headsloc.top3.placeholder", "%strikepractice_top_global_elo3%"));
                skull3.setOwner(top3);
                skull3.update();
            }
        }
    }
}
