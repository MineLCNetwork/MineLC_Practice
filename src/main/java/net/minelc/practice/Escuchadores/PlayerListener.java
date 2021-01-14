package net.minelc.practice.Escuchadores;

import com.minelc.CORE.Controller.Database;
import com.minelc.CORE.Controller.Jugador;
import com.minelc.CORE.Controller.Ranks;
import com.minelc.CORE.CoreMain;
import com.minelc.CORE.Utils.Util;
import com.mojang.authlib.BaseUserAuthentication;
import ga.strikepractice.StrikePracticeAPI;
import ga.strikepractice.events.DuelEndEvent;
import ga.strikepractice.npc.CitizensNPC;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.npc.NPC;
import net.minelc.practice.Corredores.ReinicioProgramado;
import net.minelc.practice.Manejadores.UtilidadPlayerList;
import net.minelc.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class PlayerListener implements Listener {
    public Scoreboard scoreboard;

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage().toLowerCase();
        Player p = e.getPlayer();

        if (msg.equals("/rank")) {
            Bukkit.getScheduler().runTaskLater(Practice.getInstance(), new Runnable() {
                @Override
                public void run() {
                    p.sendMessage(formatterGlobal(p, "&9&lPractice &8> &aEl jugador que $requisito$&a ganará $premio$"));
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onLogin(final AsyncPlayerPreLoginEvent e) {
        String p = e.getName();
        Jugador j = Jugador.getJugador(p);

        Database.loadPlayerSV_PVPGAMES_SYNC(j);
        Database.loadPlayerRank_SYNC(j);
        Database.loadPlayerCoins_SYNC(j);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player p = e.getPlayer();
        Jugador j = Jugador.getJugador(p);

        StrikePracticeAPI.setLanguage(p, "spanish", false);
        j.setBukkitPlayer(p);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Practice.getInstance(), new Runnable() {
            @Override
            public void run() {
                enviarTitulosIniciales(e.getPlayer());
                loadPlayerPermission(p);
                // setScoreboard(j);
                Bukkit.getLogger().info("[Debug] A este punto el usuario " + p.getName() + " debió haberse cargado.");
            }
        }, 1L);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Practice.getInstance(), new Runnable() {
            @Override
            public void run() {
                List<String> msg = Practice.getInstance().getConfig().getStringList("mensajes.welcome");
                if (!msg.isEmpty()) {
                    for (String s : msg) {
                        p.sendMessage(formatterGlobal(p, s));
                    }
                }
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.5F, 0.5F);
                setFuterHeader(p);
            }
        }, 2L);

        /* Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Practice.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    Jugador jugall = Jugador.getJugador(all);
                    // setScoreboard(jugall);
                }
            }
        }, 20L); */
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");

        for (Player all : Bukkit.getOnlinePlayers()) {
            Jugador jugall = Jugador.getJugador(all);
            // setScoreboard(jugall);
        }
    }

    @EventHandler
    public void onDuelEndEvent(DuelEndEvent e) {
        Player pl = e.getLoser();
        Player pw = e.getWinner();
        Jugador jl = Jugador.getJugador(pl);
        Jugador jw = Jugador.getJugador(pw);

        addBalance(jw, Practice.getInstance().getIntConfig("lcoins.duel-win", 5));

        for (Player all : Bukkit.getOnlinePlayers()) {
            Jugador jugall = Jugador.getJugador(all);
            // setScoreboard(jugall);
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Practice.getInstance(), new Runnable() {
            @Override
            public void run() {
                removeEntriesAndTeam(pl);
                removeEntriesAndTeam(pw);
                Bukkit.getLogger().info("[Debug] Se cargaron los jugadores " + pl.getName() + " y " + pw.getName() + " debido a que acabó un duelo.");
            }
        }, 20L);
    }

    @EventHandler
    public void onBotDuelEndEvent(ga.strikepractice.events.BotDuelEndEvent e) {
        Player pw = e.getPlayer();
        Jugador jw = Jugador.getJugador(pw);

        if (e.getWinner().equalsIgnoreCase(e.getPlayer().getName())) {
            switch (e.getFight().getDifficulty()) {
                case lH:
                    // Easy
                    addBalance(jw, Practice.getInstance().getIntConfig("lcoins.botduel-win.easy", 1));
                    break;
                case lI:
                    // Normal
                    addBalance(jw, Practice.getInstance().getIntConfig("lcoins.botduel-win.normal", 2));
                    break;
                case lJ:
                    // Hard
                    addBalance(jw, Practice.getInstance().getIntConfig("lcoins.botduel-win.hard", 4));
                    break;
                case lK:
                    // Hacker
                    addBalance(jw, Practice.getInstance().getIntConfig("lcoins.botduel-win.hacker", 6));
                    break;
            }
        };

        for (Player all : Bukkit.getOnlinePlayers()) {
            Jugador jugall = Jugador.getJugador(all);
            // setScoreboard(jugall);
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Practice.getInstance(), new Runnable() {
            @Override
            public void run() {
                // setScoreboard(jw);
                Bukkit.getLogger().info("[Debug] Se cargaron el jugador " + pw.getName() + " debido a que acabó un duelovsbots.");
            }
        }, 20L);
    }

    @EventHandler
    public void onPartyFFAEndEvent(ga.strikepractice.events.PartyFFAEndEvent e) {
        List<Player> plist = e.getParty().getPlayers();
        Player pw = e.getWinner();
        Jugador jw = Jugador.getJugador(pw);

        addBalance(jw, Practice.getInstance().getIntConfig("lcoins.partyffa-win", 10));

        for (Player all : Bukkit.getOnlinePlayers()) {
            Jugador jugall = Jugador.getJugador(all);
            // setScoreboard(jugall);
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Practice.getInstance(), new Runnable() {
            @Override
            public void run() {
                // setScoreboard(jw);
                Bukkit.getLogger().info("[Debug] Cargado jugadr " + pw.getName() + " debido a que acabó un partyffa.");
                removeEntriesAndTeam(pw);

                for (Player plu : plist) {
                    removeEntriesAndTeam(plu);
                    Bukkit.getLogger().info("[Debug Cargado player " + plu.getName() + " debid0 a que andaba en la party de la partyffa");
                }
            }
        }, 20L);
    }

    @EventHandler
    public void onPartyVsPartyEndEvent(ga.strikepractice.events.PartyVsPartyEndEvent e) {
        List<Player> pl = e.getLoser().getPlayers();
        List<Player> pw = e.getWinner().getPlayers();

        for (Player all : Bukkit.getOnlinePlayers()) {
            Jugador jugall = Jugador.getJugador(all);
            // setScoreboard(jugall);
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Practice.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player plu : pl) {
                    removeEntriesAndTeam(plu);
                    // setScoreboard(Jugador.getJugador(plu));
                    Bukkit.getLogger().info("[Debug Cargado player " + plu.getName() + " debid0 a que andaba en la party perdedra");
                }
                for (Player pwu : pw) {
                    removeEntriesAndTeam(pwu);
                    addBalance(Jugador.getJugador(pwu), Practice.getInstance().getIntConfig("lcoins.partyvsparty-win", 5));
                    Bukkit.getLogger().info("[Debug Cargado player " + pwu.getName() + " debid0 a que andaba en la party ganadra");
                }
            }
        }, 20L);
    }

    public void enviarTitulosIniciales(Player p) {
        String titulos = Practice.getInstance().getStringConfig("mensajes.titles-title", "&aPractice");
        titulos = ChatColor.translateAlternateColorCodes('&', titulos);

        String subtitulos = Practice.getInstance().getStringConfig("mensajes.titles-subtitle", "&6www.minelc.net");
        subtitulos = ChatColor.translateAlternateColorCodes('&', subtitulos);
        // p.sendTitle(titulos, subtitulos, 20, 60, 20);
        Util.sendTitle(p, 20, 60, 20, titulos, subtitulos);
    }

    public void removeEntriesAndTeam(Player p) {
        for (Team t : p.getScoreboard().getTeams()) {
            if (!t.getName().startsWith("sprac"))
            t.unregister();
        }
    }

    public void setOnePlayerScoreboard(Jugador j) {
        Player p = j.getBukkitPlayer();
        Scoreboard sb = p.getScoreboard();

        try {
            Team tm = sb.getTeam(p.getName());

            if(tm == null) {
                return;
            }

            tm = sb.registerNewTeam(p.getName());

            if (j.isHideRank()) {
                tm.setPrefix(ChatColor.GRAY + "");
            } else if (j.is_Owner()) {
                tm.setPrefix(ChatColor.DARK_RED+""+ChatColor.BOLD+Ranks.OWNER.name()+" "+j.getNameTagColor());
            } else if (j.is_Admin()) {
                tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + Ranks.ADMIN.name() + " " + j.getNameTagColor());
            } else if (j.is_MODERADOR()) {
                tm.setPrefix(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + Ranks.MOD.name() + " " + j.getNameTagColor());
            } else if (j.is_AYUDANTE()) {
                tm.setPrefix(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + Ranks.AYUDANTE.name() + " " + j.getNameTagColor());
            } else if (j.is_YOUTUBER()) {
                tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "YouTuber " + j.getNameTagColor());
            } else if (j.is_BUILDER()) {
                tm.setPrefix(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+Ranks.BUILDER.name()+" "+j.getNameTagColor());
            } else if (j.is_RUBY()) {
                tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + Ranks.RUBY.name() + " " + j.getNameTagColor());
            } else if (j.is_ELITE()) {
                tm.setPrefix(ChatColor.GOLD + "" + ChatColor.BOLD + Ranks.ELITE.name() + " " + j.getNameTagColor());
            } else if (j.is_SVIP()) {
                tm.setPrefix(ChatColor.GREEN + "" + ChatColor.BOLD + Ranks.SVIP.name() + " " + j.getNameTagColor());
            } else if (j.is_VIP()) {
                tm.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + Ranks.VIP.name() + " " + j.getNameTagColor());
            } else if (j.is_Premium()) {
                tm.setPrefix(ChatColor.YELLOW + "");
            } else {
                tm.setPrefix(ChatColor.GRAY + "");
            }

            tm.addEntry(p.getName());
            p.setScoreboard(sb);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void setScoreboard(Jugador j) {
        Player p = j.getBukkitPlayer();
        Scoreboard sb = p.getScoreboard(); // Bukkit.getScoreboardManager().getNewScoreboard();
        // jugOnline.getBukkitPlayer().setScoreboard(sb);

        Objective objGame = sb.getObjective("Creativo");

        for (Player tmOnline : Bukkit.getOnlinePlayers()) {
            Jugador jugTM = Jugador.getJugador(tmOnline);

            try {
                Team tm = sb.getTeam(jugTM.getBukkitPlayer().getName());

                if(tm != null) {
                    continue;
                }

                tm = sb.registerNewTeam(jugTM.getBukkitPlayer().getName());

                if (jugTM.isHideRank()) {
                    tm.setPrefix(ChatColor.GRAY + "");
                } else if (jugTM.is_Owner()) {
                    tm.setPrefix(ChatColor.DARK_RED+""+ChatColor.BOLD+Ranks.OWNER.name()+" "+jugTM.getNameTagColor());
                } else if (jugTM.is_Admin()) {
                    tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + Ranks.ADMIN.name() + " " + jugTM.getNameTagColor());
                } else if (jugTM.is_MODERADOR()) {
                    tm.setPrefix(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + Ranks.MOD.name() + " " + jugTM.getNameTagColor());
                } else if (jugTM.is_AYUDANTE()) {
                    tm.setPrefix(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + Ranks.AYUDANTE.name() + " " + jugTM.getNameTagColor());
                } else if (jugTM.is_YOUTUBER()) {
                    tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "YouTuber " + jugTM.getNameTagColor());
                } else if (jugTM.is_MiniYT()) {
                    tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "MiniYT " + jugTM.getNameTagColor());
                } else if (jugTM.is_BUILDER()) {
                    tm.setPrefix(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+Ranks.BUILDER.name()+" "+jugTM.getNameTagColor());
                } else if (jugTM.is_RUBY()) {
                    tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + Ranks.RUBY.name() + " " + jugTM.getNameTagColor());
                } else if (jugTM.is_ELITE()) {
                    tm.setPrefix(ChatColor.GOLD + "" + ChatColor.BOLD + Ranks.ELITE.name() + " " + jugTM.getNameTagColor());
                } else if (jugTM.is_SVIP()) {
                    tm.setPrefix(ChatColor.GREEN + "" + ChatColor.BOLD + Ranks.SVIP.name() + " " + jugTM.getNameTagColor());
                } else if (jugTM.is_VIP()) {
                    tm.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + Ranks.VIP.name() + " " + jugTM.getNameTagColor());
                } else if (jugTM.is_Premium()) {
                    tm.setPrefix(ChatColor.YELLOW + "");
                } else {
                    tm.setPrefix(ChatColor.GRAY + "");
                }

                tm.addPlayer(tmOnline.getPlayer());
                tmOnline.setScoreboard(sb);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        // /*
        for (Player tmOnline : Bukkit.getOnlinePlayers()) {
            Scoreboard sbTM = tmOnline.getScoreboard();
            try {

                if(sbTM == null) {
                    continue;
                }
                Team tm = sbTM.getTeam(j.getBukkitPlayer().getName());

                if(tm != null) {
                    continue;
                }

                tm = sbTM.registerNewTeam(j.getBukkitPlayer().getName());

                if (j.isHideRank()) {
                    tm.setPrefix(ChatColor.GRAY + "");
                } else if (j.is_Owner()) {
                    tm.setPrefix(ChatColor.DARK_RED+""+ChatColor.BOLD+Ranks.OWNER.name()+" "+j.getNameTagColor());
                } else if (j.is_Admin()) {
                    tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + Ranks.ADMIN.name() + " " + j.getNameTagColor());
                } else if (j.is_MODERADOR()) {
                    tm.setPrefix(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + Ranks.MOD.name() + " " + j.getNameTagColor());
                } else if (j.is_AYUDANTE()) {
                    tm.setPrefix(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + Ranks.AYUDANTE.name() + " " + j.getNameTagColor());
                } else if (j.is_YOUTUBER()) {
                    tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "YouTuber " + j.getNameTagColor());
                } else if (j.is_MiniYT()) {
                    tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "MiniYT " + j.getNameTagColor());
                } else if (j.is_BUILDER()) {
                    tm.setPrefix(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+Ranks.BUILDER.name()+" "+j.getNameTagColor());
                } else if (j.is_RUBY()) {
                    tm.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + Ranks.RUBY.name() + " " + j.getNameTagColor());
                } else if (j.is_ELITE()) {
                    tm.setPrefix(ChatColor.GOLD + "" + ChatColor.BOLD + Ranks.ELITE.name() + " " + j.getNameTagColor());
                } else if (j.is_SVIP()) {
                    tm.setPrefix(ChatColor.GREEN + "" + ChatColor.BOLD + Ranks.SVIP.name() + " " + j.getNameTagColor());
                } else if (j.is_VIP()) {
                    tm.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + Ranks.VIP.name() + " " + j.getNameTagColor());
                } else if (j.is_Premium()) {
                    tm.setPrefix(ChatColor.YELLOW + "");
                } else {
                    tm.setPrefix(ChatColor.GRAY + "");
                }

                tm.addPlayer(j.getBukkitPlayer().getPlayer());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        } // */
        p.setScoreboard(sb);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Jugador j = Jugador.getJugador(p);

        String pname = p.getName();
        String msg = e.getMessage();
        msg = msg.replaceAll("%", "%%");

        e.setFormat(formatChat(p, msg));
    }

    private String formatChat(Player p, String msg) {
        Jugador j = Jugador.getJugador(p);
        String formato = null;

        if(j.isHideRank()) {
            formato = Practice.getInstance().getStringConfig("formatos.premium", "§9&lPREMIUM §e$player$§8 » §7$msg$");
            formato = formato.replace("$player$", p.getName());
            formato = formato.replace("$color$", j.getNameTagColor() + "");
            formato = formato.replace("$msg$", msg);
        } else {
            switch (j.getRank()) {
                case PREMIUM:
                    formato = Practice.getInstance().getStringConfig("formatos.premium", "§9&lPREMIUM §e$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    break;
                case VIP:
                    formato = Practice.getInstance().getStringConfig("formatos.vip", "§b§lVIP $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case SVIP:
                    formato = Practice.getInstance().getStringConfig("formatos.svip", "§a§lSVIP $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case ELITE:
                    formato = Practice.getInstance().getStringConfig("formatos.elite", "§6§lELITE $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case MINIYT:
                    formato = Practice.getInstance().getStringConfig("formatos.miniyt", "§f§lMini§c§lYT $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case RUBY:
                    formato = Practice.getInstance().getStringConfig("formatos.ruby", "§c§lRUBY $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case BUILDER:
                    formato = Practice.getInstance().getStringConfig("formatos.builder", "§d§lBUILDER $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case YOUTUBER:
                    formato = Practice.getInstance().getStringConfig("formatos.youtuber", "§c§lYouTuber $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case AYUDANTE:
                    formato = Practice.getInstance().getStringConfig("formatos.ayudante", "§5§lAYUDANTE $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case MOD:
                    formato = Practice.getInstance().getStringConfig("formatos.mod", "§5§lMOD $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case ADMIN:
                    formato = Practice.getInstance().getStringConfig("formatos.admin", "§c§lADMIN $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                case OWNER:
                    formato = Practice.getInstance().getStringConfig("formatos.owner", "§4§lOWNER $color$$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    formato = ChatColor.translateAlternateColorCodes('&', formato);
                    break;
                default:
                    formato = Practice.getInstance().getStringConfig("formatos.default", "§e$player$§8 » §7$msg$");
                    formato = formato.replace("$player$", p.getName());
                    formato = formato.replace("$color$", j.getNameTagColor() + "");
                    formato = formato.replace("$msg$", msg);
                    break;
            }
        }

        return formato;
    }

    private void loadPlayerPermission(Player p) {
        Jugador j = Jugador.getJugador(p);

        switch (j.getRank()) {
            case VIP:
                addPermission("VIP", p);
                break;
            case SVIP:
                addPermission("SVIP", p);
                break;
            case ELITE:
                addPermission("ELITE", p);
                break;
            case RUBY:
            case YOUTUBER:
            case MINIYT:
            case BUILDER:
                addPermission("RUBY", p);
                break;
            case AYUDANTE:
                addPermission("AYUDANTE", p);
                break;
            case MOD:
                addPermission("MOD", p);
                break;
            case ADMIN:
                addPermission("ADMIN", p);
                break;
            case OWNER:
                addPermission("OWNER", p);
                break;
            default:
                addPermission("DEFAULT", p);
                break;
        }
    }

    public void addPermission(String s, Player p) {
        if (s.equalsIgnoreCase("default") || s.equalsIgnoreCase("premium")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.default");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        } else if (s.equalsIgnoreCase("vip")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.vip");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        } else if (s.equalsIgnoreCase("svip")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.svip");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        } else if (s.equalsIgnoreCase("elite")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.elite");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        } else if (s.equalsIgnoreCase("ruby")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.ruby");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        } else if (s.equalsIgnoreCase("ayudante")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.ayudante");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        } else if (s.equalsIgnoreCase("mod")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.mod");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        } else if (s.equalsIgnoreCase("admin")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.admin");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        } else if (s.equalsIgnoreCase("owner")) {
            List<String> rows = Practice.getInstance().getConfig().getStringList("permisos.owner");
            int numRows = rows.size();
            if (rows.isEmpty()) {
                return;
            }
            for (String row : rows) {
                if(row.startsWith("-")) {
                    StringBuilder sb = new StringBuilder(row);
                    String rowdos = sb.deleteCharAt(0).toString();
                    p.addAttachment(CoreMain.getInstance(), rowdos, false);
                } else {
                    p.addAttachment(CoreMain.getInstance(), row, true);
                }
            }
        }
    }

    private void setFuterHeader(Player p) {
        UtilidadPlayerList upl = new UtilidadPlayerList();
        String header = Practice.getInstance().getStringConfig("mensajes.header", "&7 \n&9&lPractice\n&8  ");
        header = ChatColor.translateAlternateColorCodes('&', header);

        String futer = Practice.getInstance().getStringConfig("mensajes.footer", "&7 \n&9&lWWW.MINELC.NET\n&a&lJugando en &e&lPLAY.MINELC.NET\n&8  ");
        futer = ChatColor.translateAlternateColorCodes('&', futer);

        upl.sendTablist(p, header, futer);
    }

    public String formatterGlobal(Player p, String s) {
        s = s.replace("$user$", p.getName());
        s = s.replace("$proxrestart$", Practice.getInstance().getStringConfig("elo-restart-info.proxrestart", "1 de Febrero de 2020"));
        s = s.replace("$season$", Practice.getInstance().getStringConfig("elo-restart-info.season", "§f(§bTemporada 1§f)§r"));
        s = s.replace("$premio$", Practice.getInstance().getStringConfig("elo-restart-info.premio", "§c§lRUBY§8 - §715 días"));
        s = s.replace("$requisito$", Practice.getInstance().getStringConfig("elo-restart-info.requisito", "§aobtenga el mayor ELO global"));
        s = s.replace("$requisitoExplicito$", Practice.getInstance().getStringConfig("elo-restart-info.requisito-explicito", "§aObtener el primer puesto en el TOP de ELO global"));
        s = ChatColor.translateAlternateColorCodes('&', s);
        s = PlaceholderAPI.setPlaceholders(p, s);

        return s;
    }

    private void addBalance(Jugador jug, int x) {
        jug.getBukkitPlayer().playSound(jug.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 1f, 1.3f);
        jug.addLcoins(x);
        jug.getBukkitPlayer().sendMessage(ChatColor.GOLD+"+"+x+" LCoins");
        Database.savePlayerCoins(jug);

    }
}
