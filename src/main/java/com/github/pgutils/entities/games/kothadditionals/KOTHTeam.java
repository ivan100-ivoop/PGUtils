package com.github.pgutils.entities.games.kothadditionals;

import com.github.pgutils.customitems.CustomItemRepo;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
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

    private KOTHArena arena;
    private List<Player> players = new ArrayList<>();
    private Color color;
    private String colorString;
    private int points = 0;
    private Team team;
    private int id;

    public KOTHTeam(String color, int id, KOTHArena arena) {
        this.color = Color.fromRGB(Integer.parseInt(color.substring(0), 16));
        this.colorString = "#"+color;
        this.id = id;
        this.arena = arena;

        team = arena.getScoreboard().registerNewTeam("Team_" + id);
        arena.getSbManager().addTeam(id, colorString, arena.getID());
    }
    public void addPlayer(Player player) {
        players.add(player);
        giveItems(player);
        arena.getSbManager().createGameScoreboard(player, arena.getID());
        team.addEntry(player.getName());
        System.out.println("Added player "+player.getName()+" to team "+id);
        player.sendMessage(Messages.messageWithPrefix("game-join-team", "%color%Joined team %id%!").replace("%color%", GeneralUtils.hexToMinecraftColor(colorString)).replace("%id%", id+""));
    }

    public void giveItems(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(Messages.getMessage("game-tools-helmet", "%color%Party Hat", false).replace("%color%", GeneralUtils.hexToMinecraftColor(colorString)));
        meta.setDisplayName(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(colorString)+"Party Hat"));
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        helmet.setItemMeta(meta);
        player.getInventory().setHelmet(helmet);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta2.setColor(color);
        meta2.setDisplayName(Messages.getMessage("game-tools-chest-plate", "%color%Party Vest", false).replace("%color%", GeneralUtils.hexToMinecraftColor(colorString)));
        meta2.setDisplayName(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(colorString)+"Party Vest"));
        meta2.setUnbreakable(true);
        meta2.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        chestplate.setItemMeta(meta2);
        player.getInventory().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta meta3 = (LeatherArmorMeta) leggings.getItemMeta();
        meta3.setColor(color);
        meta3.setDisplayName(Messages.getMessage("game-tools-leggings", "%color%Party Pants", false).replace("%color%", GeneralUtils.hexToMinecraftColor(colorString)));
        meta3.setDisplayName(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(colorString)+"Party Pants"));
        meta3.setUnbreakable(true);
        meta3.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leggings.setItemMeta(meta3);
        player.getInventory().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
        meta4.setColor(color);
        meta4.setDisplayName(Messages.getMessage("game-tools-boots", "%color%Party Shoes", false).replace("%color%", GeneralUtils.hexToMinecraftColor(colorString)));
        meta4.setDisplayName(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(colorString)+"Party Shoes"));
        meta4.setUnbreakable(true);
        meta4.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta4.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        boots.setItemMeta(meta4);
        player.getInventory().setBoots(boots);

        player.getInventory().setItem(player.getInventory().firstEmpty(), CustomItemRepo.createPartyStick());

        System.out.println("Added player "+player.getName()+" to team "+id);
    }
    public void removePlayer(Player player) {
        players.remove(player);
        player.getInventory().clear();
        team.removeEntry(player.getName());
        arena.getSbManager().removeGameScore(arena.getID());
    }
    public void addPoint(int point) {
        points += point;
        arena.getSbManager().setTeamPoint(id, points, arena.getID());
    }
    public void removePoint(int point) {
        points -= point;
        arena.getSbManager().setTeamPoint(id, points, arena.getID());
    }
    public int getPoints() {
        return points;
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