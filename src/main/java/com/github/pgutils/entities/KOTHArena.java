package com.github.pgutils.entities;

import java.util.*;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.interfaces.EvenDependent;

import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.interfaces.EvenIndependent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.*;

import java.awt.*;
import java.util.List;

public class KOTHArena extends PlaySpace implements EvenIndependent {
    // Convert all ChatColor colors to Color
    private static final List<String> colors = Arrays.asList("000000", "0000AA", "00AA00", "00AAAA", "AA0000", "AA00AA", "FFAA00", "AAAAAA", "555555", "5555FF", "55FF55", "55FFFF", "FF5555", "FF55FF", "FFFF55", "FFFFFF");
    private static final List<String> colorGarbage = Arrays.asList("§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7","§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f");
    List<Location> spawns = new ArrayList<>();

    List<KOTHPoint> points = new ArrayList<>();

    private int startingTime = 60;

    private int startingTick = -1;

    private int testMessageTime = 10;

    private int testMessageTick = 0;

    private List<KOTHTeam> teams = new ArrayList<>();

    private int teamsAmount = 2;

    private ScoreboardManager manager;

    private Scoreboard board;

    private Objective objective;

    public KOTHArena() {
        super();
        type = "KOTH";
    }

    @Override
    public void start() {
        System.out.println("Starting game " + getID() + " of type " + getType() + " with " + players.size() + " players!");

        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        objective = board.registerNewObjective("kothScore"+getID(), "dummy", "Team Scores : ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Player player : players) {
            player.teleport(spawns.get((int) (Math.random() * spawns.size())));
        }
        Collections.shuffle(players);

        List<String> availableColors = new ArrayList<>(colors);
        for (int i = 0; i < teamsAmount; i++) {
            String color = availableColors.get((int) (Math.random() * availableColors.size()));
            availableColors.remove(color);
            teams.add(new KOTHTeam(color, i));
        }

        for (int i = 0; i < players.size(); i++) {
            teams.get(i % teamsAmount).addPlayer(players.get(i));
        }
    }

    @Override
    public void onUpdate() {
        System.out.printf("Game %d of type %s is in status %s %d\n", getID(), getType(), status.toString(), tick);
        if (status == GameStatus.STARTING) {
            if (tick > 30)
                startingTick++;
            if (startingTick % 20 == 0 && startingTick != 0 && startingTick != startingTime) {
                players.forEach(player -> player.sendTitle((startingTime / 20 - startingTick / 20) + "", "", 0, 20, 0));
            }
            else if (startingTick >= startingTime) {
                status = GameStatus.IN_PROGRESS;
                players.forEach(player -> player.sendTitle("GO!", "", 0, 20, 0));
            }

        } else if (status == GameStatus.IN_PROGRESS) {
            testMessageTick++;
            if (testMessageTick >= testMessageTime) {
                testMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eGame is in progress!"))));
            }
            if (tick >= 300) {
                end();
            }
        }
    }

    @Override
    public void endProcedure() {
        teams.stream().forEach(team -> team.deleteTeam());
        teams.clear();
        startingTick = 0;
        testMessageTick = 0;
        board.clearSlot(DisplaySlot.SIDEBAR);
        objective.unregister();
        board = null;
        objective = null;
    }

    @Override
    public void setPos(Location pos) {
        super.setPos(pos);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
        teams.stream().forEach(team -> team.removePlayer(player));
    }

    public int addSpawnLocation(Location location) {
        spawns.add(location);
        return spawns.size() - 1;
    }

    public int removeSpawnLocation(int id) {
        spawns.remove(id);
        return id;
    }

    class KOTHTeam {
        List<Player> players = new ArrayList<>();
        Color color;
        int points = 0;
        int id;
        Team team;

        Score score;
        public KOTHTeam(String color, int id) {
            System.out.println("HERE" + Integer.parseInt(color.substring(0), 16));
            this.color = Color.fromRGB(Integer.parseInt(color.substring(0), 16));
            this.id = id;
            team = board.registerNewTeam("Team_" + id);
            team.setAllowFriendlyFire(false);
            score = objective.getScore(GeneralUtils.fixColors(KOTHArena.colorGarbage.get(KOTHArena.colors.indexOf(color))+"Team "+id+":"));
            score.setScore(0);
        }
        public void addPlayer(Player player) {
            players.add(player);
            giveItems(player);
            team.addEntry(player.getName());
            player.setScoreboard(board);
            player.sendMessage(GeneralUtils.fixColors("&aYou have joined team " + id + "!"));
        }

        public void giveItems(Player player) {
            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
            meta.setColor(color);
            meta.setDisplayName(GeneralUtils.fixColors("&e&lTeam : " + id));
            helmet.setItemMeta(meta);
            player.getInventory().setHelmet(helmet);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
            meta2.setColor(color);
            meta2.setDisplayName(GeneralUtils.fixColors("&e&lTeam : " + id));
            chestplate.setItemMeta(meta2);
            player.getInventory().setChestplate(chestplate);

            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta meta3 = (LeatherArmorMeta) leggings.getItemMeta();
            meta3.setColor(color);
            meta3.setDisplayName(GeneralUtils.fixColors("&e&lTeam : " + id));
            leggings.setItemMeta(meta3);
            player.getInventory().setLeggings(leggings);

            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
            meta4.setColor(color);
            meta4.setDisplayName(GeneralUtils.fixColors("&e&lTeam : " + id));
            boots.setItemMeta(meta4);
            player.getInventory().setBoots(boots);

            ItemStack party_stick = new ItemStack(Material.STICK);
            ItemMeta meta5 = party_stick.getItemMeta();
            meta5.setDisplayName(GeneralUtils.fixColors("&e&lParty Stick"));
            // Enchant the party stick with knockback 3
            meta5.addEnchant(Enchantment.KNOCKBACK, 3, true);
            party_stick.setItemMeta(meta5);
            player.getInventory().setItem(player.getInventory().firstEmpty(), party_stick);

            System.out.println("Added player "+player.getName()+" to team "+id);
        }
        public void removePlayer(Player player) {
            players.remove(player);
            team.removeEntry(player.getName());
            player.getInventory().clear();
            player.setScoreboard(manager.getNewScoreboard());
        }
        public void addPoint() {
            points++;
            score.setScore(points);
        }
        public void removePoint() {

            points--;
            score.setScore(points);
        }
        public int getPoints() {
            return points;
        }
        public void setTeam(Team team) {
            this.team = team;
        }
        public Team getTeam() {
            return team;
        }
        public Color getColor() {
            return color;
        }
        public int getID() {
            return id;
        }

        public void deleteTeam() {
            for (int i = players.size() - 1; i >= 0; i--) {
                removePlayer(players.get(i));
            }
            team.unregister();
        }
    }

}
