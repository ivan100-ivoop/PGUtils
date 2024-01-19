package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.particles.variants.SwirlingParticle;
import com.github.pgutils.utils.Keys;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Random;

public class CrownOfTheFallenEffect extends CustomEffect {

    public int cooldown = 0;

    public int cooldownTime = 200;

    private Random random = new Random();

    public CrownOfTheFallenEffect(Player effectedPlayer) {
        super(effectedPlayer);
    }
    @Override
    public void onUpdate() {

        ItemStack item = getEffectedPlayer().getInventory().getHelmet();
        if (item != null) {
            if (item.getItemMeta() != null) {
                PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                if (!container.has(Keys.crownOfTheFallen, PersistentDataType.BOOLEAN))
                    CustomEffect.removeEffect(this);
            }

        }

        if (cooldown < cooldownTime) {
            cooldown++;
        }
        if (cooldown == cooldownTime) {
            if (getTicks() % 3 == 0) {
                double angle =  ((double)getTicks() / 20 * 2 * Math.PI) / 3.0;
                SwirlingParticle.objectLessParticle(getEffectedPlayer().getLocation(), 0.35, 2, 2, Particle.FLAME, 0.05, angle);

            }
        }

    }

    @Override
    public void onRemove() {

    }

    public void handleGeneralDamage(EntityDamageEvent event) {
        // Check if the damage is sufficient to trigger the effect
        if (event.getDamage() < 3) {
            return;
        }

        if (cooldown == cooldownTime) {
            event.setCancelled(true);
            teleportRandomly(getEffectedPlayer());
            cooldown = 0;
        }
    }



    public void teleportRandomly(Player player) {
        Location originalLocation = player.getLocation();
        World world = originalLocation.getWorld();
        Location randomLocation = null;

        for (int i = 0; i < 1000; i++) {
            int x = originalLocation.getBlockX() + random.nextInt(32) - 16;
            int y = originalLocation.getBlockY() - 15 + random.nextInt(30);
            int z = originalLocation.getBlockZ() + random.nextInt(32) - 16;

            randomLocation = new Location(world, x + 0.5, y, z + 0.5);

            if (isSafeLocation(randomLocation)) {
                world.spawnParticle(Particle.CLOUD, originalLocation, 100, 0.5, 0.2, 0.5, 0.01);
                world.playSound(originalLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                player.teleport(randomLocation);
                return; // Exit the method after successful teleportation
            }
        }
        // Spawn particles at the original location if no safe location was found
        world.spawnParticle(Particle.CLOUD, originalLocation, 100, 0.5, 0.2, 0.5, 0.01);
    }

    private boolean isSafeLocation(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return world.getBlockAt(x, y - 1, z).getType().isSolid() // Check if the block below is solid
                && world.getBlockAt(x, y, z).getType().isTransparent() // Check if the block at the player's feet is not solid
                && world.getBlockAt(x, y + 1, z).getType().isTransparent(); // Check if the block at the player's head is not solid
    }

    public void teleportBehindPlayer(Player player, Player damager) {
        Location playerLocation = damager.getLocation();
        Vector direction = playerLocation.getDirection().clone().setY(0).normalize().multiply(-2);
        Location NewLocation = playerLocation.add(direction);
        if(isSafeLocation(NewLocation))
        {
            player.teleport(NewLocation);
            player.getWorld().spawnParticle(Particle.CLOUD, playerLocation, 100, 0.5, 0.2, 0.5, 0.01);
            player.getWorld().playSound(playerLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        }
        else teleportRandomly(player);

    }

    public void handlePlayerDamage(EntityDamageByEntityEvent event) {
        if (cooldown == cooldownTime) {
            event.setCancelled(true);
            teleportBehindPlayer(getEffectedPlayer(), (Player) event.getDamager());
            cooldown = 0;
        }

    }
}
