package com.github.pgutils.customitems;

import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Keys;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class CustomItemRepo {

    public final static Map<String, Supplier<ItemStack>> custom_item_name = new HashMap<>();

    static {
        custom_item_name.put("Party Stick", CustomItemRepo::createPartyStick);
        custom_item_name.put("Crown of the Fallen", CustomItemRepo::createCrownOfTheFallen);
        custom_item_name.put("Godless", CustomItemRepo::createGodless);
    }

    public static ItemStack createCustomItem(String name, Material material, CustomItemRarities rarity, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(rarity.getColor() + name);

        // Create a new ArrayList from the unmodifiable list
        List<String> formattedLore = new ArrayList<>(Arrays.asList(ChatColor.BLUE + "[Rarity: " + rarity.getColor() +"§l"+ rarity.name() + ChatColor.BLUE + "]"));
        formattedLore.addAll(lore);

        itemMeta.setLore(formattedLore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createPartyStick() {
        List<String> lore = Arrays.asList("Unleash the power of the party!", GeneralUtils.hexToMinecraftColor("#FFAA00") + "[Activation : Right Click!]");
        ItemStack itemStack = createCustomItem("Party Stick", Material.STICK, CustomItemRarities.LEGENDARY, lore);
        // Assuming that you have defined CustomItemRarities and Keys elsewhere in your code.
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.partyStick, PersistentDataType.BOOLEAN, true);
        itemMeta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createCrownOfTheFallen() {
        List<String> lore = Arrays.asList("The crown of the fallen king.", GeneralUtils.hexToMinecraftColor("#FFAA00") + "[Activation : Take damage!]");
        ItemStack itemStack = createCustomItem("Crown of the Fallen", Material.GOLDEN_HELMET, CustomItemRarities.EPIC, lore);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.crownOfTheFallen, PersistentDataType.BOOLEAN, true);
        itemMeta.setUnbreakable(true);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createGodless() {
        List<String> lore = Arrays.asList("Even gods shiver in it's presence", GeneralUtils.hexToMinecraftColor("#FFAA00") + "[Passive: Summons Godless!]");
        ItemStack itemStack = createCustomItem("Godless", Material.IRON_HELMET, CustomItemRarities.MYTHIC, lore);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.godLess, PersistentDataType.BOOLEAN, true);
        itemMeta.setUnbreakable(true);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
