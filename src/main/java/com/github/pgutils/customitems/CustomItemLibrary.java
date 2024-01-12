package com.github.pgutils.customitems;

import com.github.pgutils.PGUtils;
import com.github.pgutils.customitems.effects.PartyEffect;
import com.github.pgutils.customitems.effects.PartyEffectCooldown;
import com.github.pgutils.utils.Keys;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class CustomItemLibrary implements Listener {

    @EventHandler
    public static void onPlayerItemHeld(PlayerItemHeldEvent player) {
        checkIfCustomItemInMainHand(player.getPlayer());
    }

    public static void checkIfCustomItemInMainHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.getItemMeta() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            checkIfCustomItemInMainHandUsedRight(player, item);
        }
        if (item != null && item.getItemMeta() != null && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            checkIfCustomItemInMainHandUsedLeft(player, item);
        }


    }

    public static void checkIfCustomItemInMainHandUsedRight(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(Keys.partyStick, PersistentDataType.BOOLEAN)) {
            // Find if the player has the cooldown effect
            if(CustomEffect.customEffects.stream().anyMatch(customEffect -> customEffect instanceof PartyEffectCooldown && customEffect.getEffectedPlayer().equals(player))) {
            } else {
                new PartyEffect(player);
                Vector direction = player.getLocation().getDirection();
                direction.multiply(1.5);
                player.setVelocity(player.getVelocity().add(direction));
                new PartyEffectCooldown(player);
                player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 15, 0.01, 0, 0.01);
            }
        }


    }

    public static void checkIfCustomItemInMainHandUsedLeft(Player player, ItemStack item) {

    }


    public static void checkIfCustomItemInOffHand(Player player) {
        ItemStack item = player.getInventory().getItemInOffHand();

    }

    public static void checkIfCustomItemInArmor(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();

    }

    public static void checkIfCustomItemInInventory(Player player) {
        ItemStack[] inventory = player.getInventory().getContents();

    }

}
