package com.github.pgutils.customitems;

import com.github.pgutils.PGUtils;
import com.github.pgutils.customitems.effects.CrownOfTheFallenEffect;
import com.github.pgutils.customitems.effects.GodlessEffect;
import com.github.pgutils.customitems.effects.PartyEffect;
import com.github.pgutils.customitems.effects.PartyEffectCooldown;
import com.github.pgutils.utils.Keys;
import com.github.pgutils.utils.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CustomItemLibrary implements Listener {

    @EventHandler
    public static void onPlayerItemHeld(PlayerItemHeldEvent event) {
        checkIfCustomItemInMainHand(event.getPlayer(), event);
    }

    public static void checkIfCustomItemInMainHand(Player player, Event event) {
        ItemStack item = player.getInventory().getItemInMainHand();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            checkIfCustomItemInMainHandUsedRight(player, item, event);
        }
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            checkIfCustomItemInMainHandUsedLeft(player, item, event);
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (PlayerManager.cannotDamage.contains(event.getDamager())) {
                event.setCancelled(true);
                return;
            }
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            checkIfCustomItemInArmor(victim, event);
            checkIfCustomItemInMainHand(victim, event);
            checkIfCustomItemInOffHand(victim, event);
            checkIfCustomItemInInventory(victim, event);

        }
        if (event.getDamager() instanceof Firework) {
            Firework fw = (Firework) event.getDamager();
            if (fw.hasMetadata("nodamage")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        Entity e = event.getEntity();

        if (e instanceof Player && CustomEffect.hasEffect((Player) e, CrownOfTheFallenEffect.class)){
            CrownOfTheFallenEffect effect = (CrownOfTheFallenEffect) CustomEffect.getEffect((Player) e, CrownOfTheFallenEffect.class);
            effect.handleGeneralDamage(event);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (event.getClickedInventory() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        new BukkitRunnable() {
            @Override
            public void run() {
                checkIfCustomItemInArmor(player, event);
                checkIfCustomItemInMainHand(player, event);
                checkIfCustomItemInOffHand(player, event);
            }
        }.runTaskLater(PGUtils.instance, 1);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        checkIfCustomItemInArmor(player, event);
        checkIfCustomItemInMainHand(player, event);
        checkIfCustomItemInOffHand(player, event);
        checkIfCustomItemInInventory(player, event);
    }

    public static void onStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkIfCustomItemInArmor(player, null);
            checkIfCustomItemInMainHand(player, null);
            checkIfCustomItemInOffHand(player, null);
            checkIfCustomItemInInventory(player, null);
        }
    }

    public static void checkIfCustomItemInMainHandUsedRight(Player player, ItemStack item, Event event) {
        if (item == null)
            return;
        if (item.getItemMeta() == null)
            return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(Keys.partyStick, PersistentDataType.BOOLEAN)) {
            // Find if the player has the cooldown effect
            if(!CustomEffect.hasEffect(player, PartyEffectCooldown.class)) {
                new PartyEffect(player);

                // Get the direction the player is looking at
                Vector direction = player.getLocation().getDirection().clone();
                direction.normalize(); // Normalize the direction vector
                direction.multiply(1.5); // Set the desired speed (magnitude of the velocity)

                // Set the player's velocity to the calculated direction
                player.setVelocity(direction);

                // Add cooldown effect
                new PartyEffectCooldown(player);

                // Spawn particles
                player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 50, 0.5, 0.2, 0.5, 0.01);
            }
        }
    }

    public static void checkIfCustomItemInMainHandUsedLeft(Player player, ItemStack item, Event event) {

    }


    public static void checkIfCustomItemInOffHand(Player player, Event event) {
        ItemStack item = player.getInventory().getItemInOffHand();

    }

    public static void checkIfCustomItemInArmor(Player player, Event event) {
        ItemStack[] armor = player.getInventory().getArmorContents();

        if (armor[3] == null){
            if (CustomEffect.hasEffect(player, CrownOfTheFallenEffect.class)) {
                CustomEffect.removeEffect(CustomEffect.getEffect(player, CrownOfTheFallenEffect.class));
            }

            if (CustomEffect.hasEffect(player, GodlessEffect.class)) {
                CustomEffect.removeEffect(CustomEffect.getEffect(player, GodlessEffect.class));
            }
        }

        if (armor[3] != null) {
            if (armor[3].getItemMeta() != null) {
                ItemMeta meta = armor[3].getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();

                if (container.has(Keys.crownOfTheFallen, PersistentDataType.BOOLEAN)) {
                    if (!CustomEffect.hasEffect(player, CrownOfTheFallenEffect.class)) {
                        new CrownOfTheFallenEffect(player);
                    }
                    else if (event instanceof EntityDamageByEntityEvent) {
                        if (CustomEffect.hasEffect(player, CrownOfTheFallenEffect.class)) {
                            CrownOfTheFallenEffect effect = (CrownOfTheFallenEffect) CustomEffect.getEffect(player, CrownOfTheFallenEffect.class);
                            effect.handlePlayerDamage((EntityDamageByEntityEvent) event);
                        }
                    }

                }

                if (container.has(Keys.godLess, PersistentDataType.BOOLEAN)) {
                    if (!CustomEffect.hasEffect(player, GodlessEffect.class)) {
                        new GodlessEffect(player);
                    }
                    else if (event instanceof EntityDamageByEntityEvent) {
                        if (CustomEffect.hasEffect(player, GodlessEffect.class)) {
                            GodlessEffect effect = (GodlessEffect) CustomEffect.getEffect(player, GodlessEffect.class);
                            effect.handlePlayerDamage((EntityDamageByEntityEvent) event);
                        }
                    }
                }

            }
        }
    }

    public static void checkIfCustomItemInInventory(Player player, Event event) {
        ItemStack[] inventory = player.getInventory().getContents();


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CustomEffect.removeAllEffects(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        CustomEffect.removeAllEffects(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        checkIfCustomItemInArmor(player, event);
        checkIfCustomItemInMainHand(player, event);
        checkIfCustomItemInOffHand(player, event);
        checkIfCustomItemInInventory(player, event);
    }


}
