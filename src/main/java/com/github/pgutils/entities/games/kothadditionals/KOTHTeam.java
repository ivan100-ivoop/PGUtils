package com.github.pgutils.entities.games.kothadditionals;

import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KOTHTeam {

    public static final List<String> colors = Arrays.asList("000000", "0000AA", "00AA00", "00AAAA", "AA0000", "AA00AA", "FFAA00", "AAAAAA", "555555", "5555FF", "55FF55", "55FFFF", "FF5555", "FF55FF", "FFFF55");
    public static final List<String> colorGarbage = Arrays.asList("§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7","§8", "§9", "§a", "§b", "§c", "§d", "§e");

    KOTHArena arena;
    List<Player> players = new ArrayList<>();
    Color color;
    String colorString;
    int points = 0;
    int id;
    Team team;
    Score score;
    public KOTHTeam(String color, int id, KOTHArena arena) {
        this.color = Color.fromRGB(Integer.parseInt(color.substring(0), 16));
        this.colorString = color;
        this.id = id;
        this.arena = arena;
        team = arena.getBoard().registerNewTeam("Team_" + id);
        team.setAllowFriendlyFire(false);
        score = arena.getObjective().getScore(GeneralUtils.fixColors(colorGarbage.get(colors.indexOf(colorString))+"Team "+id+":"));
        score.setScore(0);
    }
    public void addPlayer(Player player) {
        players.add(player);
        giveItems(player);
        team.addEntry(player.getName());
        player.setScoreboard(arena.getBoard());
        System.out.println("Added player "+player.getName()+" to team "+id);
        player.sendMessage(GeneralUtils.fixColors(colorGarbage.get(colors.indexOf(colorString))+"You have joined team " + id + "!"));
    }

    public void giveItems(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(GeneralUtils.fixColors(colorGarbage.get(colors.indexOf(colorString))+"Party Hat"));
        helmet.setItemMeta(meta);
        player.getInventory().setHelmet(helmet);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta2.setColor(color);
        meta2.setDisplayName(GeneralUtils.fixColors(colorGarbage.get(colors.indexOf(colorString))+"Party Vest"));
        chestplate.setItemMeta(meta2);
        player.getInventory().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta meta3 = (LeatherArmorMeta) leggings.getItemMeta();
        meta3.setColor(color);
        meta3.setDisplayName(GeneralUtils.fixColors(colorGarbage.get(colors.indexOf(colorString))+"Party Pants"));
        leggings.setItemMeta(meta3);
        player.getInventory().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
        meta4.setColor(color);
        meta4.setDisplayName(GeneralUtils.fixColors(colorGarbage.get(colors.indexOf(colorString))+"Party Shoes"));
        boots.setItemMeta(meta4);
        player.getInventory().setBoots(boots);

        ItemStack party_stick = new ItemStack(Material.STICK);
        ItemMeta meta5 = party_stick.getItemMeta();
        meta5.setDisplayName(GeneralUtils.fixColors(colorGarbage.get(colors.indexOf(colorString))+"Party Stick"));
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
        player.setScoreboard(arena.getManager().getNewScoreboard());
    }
    public void addPoint(int point) {
        points += point;
        score.setScore(points);
    }
    public void removePoint(int point) {
        points -= point;
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

    public String getColorString() {
        return colorString;
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

    public List<Player> getPlayers() {
        return players;
    }
}