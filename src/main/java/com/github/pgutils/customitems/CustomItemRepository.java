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

import java.util.List;
import java.util.function.Supplier;

public class CustomItemRepository {

    public final static Map<String, Supplier<ItemStack>> custom_item_name = new HashMap<>();

    static {
        custom_item_name.put("Party Stick", CustomItemRepository::createPartyStick);
        custom_item_name.put("Crown of the Fallen", CustomItemRepository::createCrownOfTheFallen);
        custom_item_name.put("Godless", CustomItemRepository::createGodless);
        custom_item_name.put("Atomizer", CustomItemRepository::createAtomizer);
        custom_item_name.put("The Golden Harp", CustomItemRepository::createGoldenHarp);
        custom_item_name.put("Fist full of bomb", CustomItemRepository::createBombhead);
        custom_item_name.put("Quantum LTF-337", CustomItemRepository::createQuantumLTF);
        custom_item_name.put("Mini Beacon", CustomItemRepository::createMiniBeacon);
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
        itemMeta.getPersistentDataContainer().set(Keys.undroppable, PersistentDataType.BOOLEAN, true);
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


    public static ItemStack createAtomizer() {
        List<String> lore = Arrays.asList("Destruction on atmoic level", GeneralUtils.hexToMinecraftColor("#FFAA00") + "[Passive: Summons Atomizer!]");
        ItemStack itemStack = createCustomItem("Atomizer", Material.NETHERITE_HELMET, CustomItemRarities.DEVELOPER, lore);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.atomizer, PersistentDataType.BOOLEAN, true);
        itemMeta.setUnbreakable(true);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createGoldenHarp() {
        List<String> lore = Arrays.asList("The harp of the gods", GeneralUtils.hexToMinecraftColor("#FFAA00") + "[Passive: Rains arrows from the heavens!]");
        ItemStack itemStack = createCustomItem("The Golden Harp", Material.BOW, CustomItemRarities.LEGENDARY, lore);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.goldenHarp, PersistentDataType.BOOLEAN, true);
        itemMeta.setUnbreakable(true);

        itemStack.setItemMeta(itemMeta);

        return itemStack;

    }

    public static ItemStack createBombhead() {
        List<String> lore = Arrays.asList("HIT SOMEONE TO GIVE THEM THE BOMB", GeneralUtils.hexToMinecraftColor("#FFAA00") + "[Active: Hit someone to pass the bomb!]");
        ItemStack itemStack = createCustomItem("Fist full of bomb", Material.TNT, CustomItemRarities.LEGENDARY, lore);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.bombHead, PersistentDataType.BOOLEAN, true);
        itemMeta.getPersistentDataContainer().set(Keys.undroppable, PersistentDataType.BOOLEAN, true);
        itemMeta.getPersistentDataContainer().set(Keys.unplaceable, PersistentDataType.BOOLEAN, true);
        itemMeta.setUnbreakable(true);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createQuantumLTF() {
        List<String> lore = Arrays.asList("The greatest creation of Penchev", GeneralUtils.hexToMinecraftColor("#FFAA00") + "[Passive: Unleash Destruction!]");
        ItemStack itemStack = createCustomItem("Quantum LTF-337", Material.BOW, CustomItemRarities.MYTHIC, lore);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.quantumLTF, PersistentDataType.BOOLEAN, true);
        itemMeta.setUnbreakable(true);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createMiniBeacon() {
        List<String> lore = Arrays.asList("The beacon of hope", GeneralUtils.hexToMinecraftColor("#FFAA00") + "[Active: Choose the effect!]");
        ItemStack itemStack = createCustomItem("Mini Beacon", Material.BEACON, CustomItemRarities.RARE, lore);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(Keys.miniBeacon, PersistentDataType.BOOLEAN, true);
        itemMeta.getPersistentDataContainer().set(Keys.unplaceable, PersistentDataType.BOOLEAN, true);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
