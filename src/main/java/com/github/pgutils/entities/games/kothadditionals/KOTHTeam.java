package com.github.pgutils.entities.games.kothadditionals;

import com.github.pgutils.customitems.CustomItemRepository;
import com.github.pgutils.entities.Team;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class KOTHTeam extends Team {
    private KOTHArena arena;
    private List<Player> players = new ArrayList<>();
    private Color color;
    private String colorString;
    private int points = 0;
    private org.bukkit.scoreboard.Team team;
    private int id;

    public KOTHTeam(String color, int id, KOTHArena arena) {
        super(color, id, arena);
        this.arena = arena;
        this.colorString = "#" + color;
    }

    public void addPlayer(Player player) {
        super.addPlayer(player);
        giveItems(player);
    }

    public void giveItems(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(getColor());
        meta.setDisplayName(Messages.getMessage("game-tools-helmet", "%color%Party Hat", false).replace("%color%", GeneralUtils.hexToMinecraftColor(getColorString())));
        meta.setDisplayName(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(getColorString())+"Party Hat"));
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        helmet.setItemMeta(meta);
        player.getInventory().setHelmet(helmet);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta2.setColor(getColor());
        meta2.setDisplayName(Messages.getMessage("game-tools-chest-plate", "%color%Party Vest", false).replace("%color%", GeneralUtils.hexToMinecraftColor(getColorString())));
        meta2.setDisplayName(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(getColorString())+"Party Vest"));
        meta2.setUnbreakable(true);
        meta2.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        chestplate.setItemMeta(meta2);
        player.getInventory().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta meta3 = (LeatherArmorMeta) leggings.getItemMeta();
        meta3.setColor(getColor());
        meta3.setDisplayName(Messages.getMessage("game-tools-leggings", "%color%Party Pants", false).replace("%color%", GeneralUtils.hexToMinecraftColor(getColorString())));
        meta3.setDisplayName(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(getColorString())+"Party Pants"));
        meta3.setUnbreakable(true);
        meta3.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leggings.setItemMeta(meta3);
        player.getInventory().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
        meta4.setColor(getColor());
        meta4.setDisplayName(Messages.getMessage("game-tools-boots", "%color%Party Shoes", false).replace("%color%", GeneralUtils.hexToMinecraftColor(getColorString())));
        meta4.setDisplayName(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(getColorString())+"Party Shoes"));
        meta4.setUnbreakable(true);
        meta4.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta4.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        boots.setItemMeta(meta4);
        player.getInventory().setBoots(boots);

        player.getInventory().setItem(player.getInventory().firstEmpty(), CustomItemRepository.createPartyStick());

        System.out.println("Added player "+player.getName()+" to team "+getID());
    }


}