package net.minelc.practice.Controladores;

import com.minelc.CORE.Controller.Database;
import com.minelc.CORE.Utils.IconMenu;
import com.minelc.CORE.Utils.ItemUtils;
import net.minelc.practice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Inventarios {
    private static IconMenu invStats_PRAC = null;
    private static IconMenu invStats_PRAC_kills = null;
    private static IconMenu invStats_PRAC_deaths = null;
    private static IconMenu invStats_PRAC_global_elo = null;

    // Asesinatos
    private IconMenu getInvStats_PRAC_kills() {
        if (invStats_PRAC_kills == null) {
            invStats_PRAC_kills = new IconMenu("TOP Asesinatos - Practice", 45, new IconMenu.OptionClickEventHandler() {
                public void onOptionClick(IconMenu.OptionClickEvent e) {
                    e.setWillClose(false);
                    e.setWillDestroy(false);
                    if (e.getPosition() == 31) {
                        getInvStats_PRAC().open(e.getPlayer());
                    }

                }
            }, Practice.getInstance());
            LinkedHashMap<String, Integer> top = Database.getTop(18, "username", "kills", "stats");
            int slot = 0;
            Iterator var3 = top.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, Integer> es = (Map.Entry)var3.next();
                invStats_PRAC_kills.setOption(slot++, new ItemUtils((String)es.getKey(), 1, "" + ChatColor.GOLD + ChatColor.BOLD + "#" + slot + ChatColor.DARK_GRAY + " - " + ChatColor.RED + (String)es.getKey(), "" + ChatColor.GRAY + es.getValue() + " asesinatos"));
            }

            invStats_PRAC_kills.setOption(31, new ItemStack(Material.MAP), "" + ChatColor.GRAY + ChatColor.BOLD + "Regresar", new String[0]);
        }

        return invStats_PRAC_kills;
    }

    private IconMenu invStats_PRAC_global_elo() {
        if (invStats_PRAC_global_elo == null) {
            invStats_PRAC_global_elo = new IconMenu("TOP ELO global - Practice", 45, new IconMenu.OptionClickEventHandler() {
                public void onOptionClick(IconMenu.OptionClickEvent e) {
                    e.setWillClose(false);
                    e.setWillDestroy(false);
                    if (e.getPosition() == 31) {
                        getInvStats_PRAC().open(e.getPlayer());
                    }

                }
            }, Practice.getInstance());
            LinkedHashMap<String, Integer> top = Database.getTop(18, "username", "global_elo", "stats");
            int slot = 0;
            Iterator var3 = top.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, Integer> es = (Map.Entry)var3.next();
                invStats_PRAC_global_elo.setOption(slot++, new ItemUtils((String)es.getKey(), 1, "" + ChatColor.GOLD + ChatColor.BOLD + "#" + slot + ChatColor.DARK_GRAY + " - " + ChatColor.RED + (String)es.getKey(),  ChatColor.GRAY + "ELO " + es.getValue()));
            }

            invStats_PRAC_global_elo.setOption(31, new ItemStack(Material.MAP), "" + ChatColor.GRAY + ChatColor.BOLD + "Regresar", new String[0]);
        }

        return invStats_PRAC_global_elo;
    }

    private IconMenu getInvStats_PRAC_deaths() {
        if (invStats_PRAC_deaths == null) {
            invStats_PRAC_deaths = new IconMenu("TOP Muertes - Practice", 45, new IconMenu.OptionClickEventHandler() {
                public void onOptionClick(IconMenu.OptionClickEvent e) {
                    e.setWillClose(false);
                    e.setWillDestroy(false);
                    if (e.getPosition() == 31) {
                        getInvStats_PRAC().open(e.getPlayer());
                    }

                }
            }, Practice.getInstance());
            LinkedHashMap<String, Integer> top = Database.getTop(18, "username", "deaths", "stats");
            int slot = 0;
            Iterator var3 = top.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, Integer> es = (Map.Entry)var3.next();
                invStats_PRAC_deaths.setOption(slot++, new ItemUtils((String)es.getKey(), 1, "" + ChatColor.GOLD + ChatColor.BOLD + "#" + slot + ChatColor.DARK_GRAY + " - " + ChatColor.RED + (String)es.getKey(), "" + ChatColor.GRAY + es.getValue() + " muertes"));
            }

            invStats_PRAC_deaths.setOption(31, new ItemStack(Material.MAP), "" + ChatColor.GRAY + ChatColor.BOLD + "Regresar", new String[0]);
        }

        return invStats_PRAC_deaths;
    }

    public IconMenu getInvStats_PRAC() {
        if (invStats_PRAC == null) {
            invStats_PRAC = new IconMenu("TOP Jugadores - Practice", 45, new IconMenu.OptionClickEventHandler() {
                public void onOptionClick(IconMenu.OptionClickEvent e) {
                    e.setWillDestroy(false);
                    switch(e.getPosition()) {
                        case 10:
                            e.setWillClose(false);
                            getInvStats_PRAC_kills().open(e.getPlayer());
                            break;
                        case 12:
                            e.setWillClose(false);
                            getInvStats_PRAC_deaths().open(e.getPlayer());
                            // LobbyController.getInvStats_EW_partidas_ganadas().open(e.getPlayer());
                            break;
                        case 14:
                            e.setWillClose(false);
                            invStats_PRAC_global_elo().open(e.getPlayer());
                            break;
                        case 16:
                            if (Practice.getInstance().getBooleanConfig("is-in-practice", false)) {
                                e.setWillClose(false);
                                e.getPlayer().performCommand("strikeleaderboards");
                            } else {
                                e.setWillClose(true);
                                e.getPlayer().sendMessage(ChatColor.YELLOW + "Ingresa a la modalidad para ver más estadísticas.");
                            }
                            // LobbyController.getInvStats_EW_deaths().open(e.getPlayer());
                            break;
                        case 31:
                            e.setWillClose(true);
                            // LobbyController.getInvStats_MAIN().open(e.getPlayer());
                    }

                }
            }, Practice.getInstance());
            invStats_PRAC.setOption(10, new ItemStack(Material.SIGN), "" + ChatColor.GREEN + ChatColor.BOLD + "Asesinatos", ChatColor.GRAY + "Click para mostrar a los usuarios con", ChatColor.GRAY + "más asesinatos");
            invStats_PRAC.setOption(12, new ItemStack(Material.SIGN), "" + ChatColor.GREEN + ChatColor.BOLD + "Muertes", new String[]{ChatColor.GRAY + "Click para mostrar a los usuarios con", ChatColor.GRAY + "más muertes"});
            invStats_PRAC.setOption(14, new ItemStack(Material.SIGN), "" + ChatColor.GREEN + ChatColor.BOLD + "ELO global", new String[]{ChatColor.GRAY + "Click para mostrar a los usuarios con", ChatColor.GRAY + "los mejores ELO globales"});
            invStats_PRAC.setOption(16, new ItemStack(Material.SIGN), "" + ChatColor.BLUE + ChatColor.BOLD + "Otras estadísticas", new String[]{ChatColor.GRAY + "Click para mostrar estadísticas", ChatColor.GRAY + "mas avanzadas"});
            invStats_PRAC.setOption(31, new ItemStack(Material.MAP), "" + ChatColor.GRAY + ChatColor.BOLD + "Regresar");
        }

        return invStats_PRAC;
    }
}
