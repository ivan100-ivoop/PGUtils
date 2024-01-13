package com.github.pgutils.customitems;

import com.github.pgutils.utils.Keys;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.awt.*;

public class CustomItemRepo {

    public static ItemStack createCustomItem(String name, Material material, CustomItemRarities rarity, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(rarity.getColor() + name);

        // Create a new ArrayList from the unmodifiable list
        List<String> formattedLore = new ArrayList<>(Arrays.asList(ChatColor.BLUE + "[Rarity: " + rarity.getColor() + rarity.name() + ChatColor.BLUE + "]"));
        formattedLore.addAll(lore);

        itemMeta.setLore(formattedLore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createPartyStick() {
        List<String> lore = Arrays.asList("Unleash the power of the party!", "Right-click to use!");
        ItemStack itemStack = createCustomItem("Party Stick", Material.STICK, CustomItemRarities.LEGENDARY, lore);
        // Assuming that you have defined CustomItemRarities and Keys elsewhere in your code.
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.partyStick, PersistentDataType.BOOLEAN, true);
        itemMeta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
