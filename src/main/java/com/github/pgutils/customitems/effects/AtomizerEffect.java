package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.particles.variants.RandomisedDirCylinderParticle;
import com.github.pgutils.utils.Keys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class AtomizerEffect extends GodlessEffect{
    public AtomizerEffect(Player effectedPlayer) {
        super(effectedPlayer);
        attackDamage = 30;
        attackKnockback = 3;
        current_y_offset = 1;
        attackRange = 12;
        attackCooldownTime = 1;
        summonCooldownTime = 1;

    }

    @Override
    public void onUpdate() {

        ItemStack item = getEffectedPlayer().getInventory().getHelmet();
        if (item != null) {
            if (item.getItemMeta() != null) {
                PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                if (!container.has(Keys.atomizer, PersistentDataType.BOOLEAN)) {
                    CustomEffect.removeEffect(this);
                    return;
                }
            }
        }
        if (summonCooldown < summonCooldownTime && attackAnimation == 0) {
            summonCooldown++;
        }
        if (summonCooldown == summonCooldownTime) {
            if (armorStand == null) {
                summonGodless();
            }
        }
        if (armorStand != null){
            if (!attacking) {
                Location playerLocation = getEffectedPlayer().getLocation();
                Vector direction = playerLocation.getDirection().clone().setY(0).normalize().multiply(-distanceBehind);
                // Offset the location a little to the right because the sword is not centered

                Vector rightOffset = direction.clone().crossProduct(new Vector(0, -1, 0)).normalize().multiply(1);
                Vector leftOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(1);

                direction.add(leftOffset);

                Location newLocation = playerLocation.add(direction);
                newLocation.setYaw(playerLocation.getYaw());
                newLocation.setPitch(0);

                armorStand.teleport(newLocation);
                hoverAnimation();
                if (getTicks() % 3 == 0)
                    RandomisedDirCylinderParticle.objectLessParticle(armorStand.getLocation().clone().add(rightOffset).add(0, 1, 0), 0.5, 1, 1, Particle.SOUL, 0.04);
                checkForTargets();
            }
            else {
                if (disappear < disappearTime) {
                    disappear++;
                }
                else {
                    deactivate();
                    if (target != null) {
                        attack(target);
                    }
                }
            }
        }
        if (attacking && attackArmorStand != null) {
            if (attackAnimation < attackAnimationTime) {
                attackAnimation++;
                attackAnimations();
            }
            else {
                attackAnimation = 0;
                attacking = false;
                attackArmorStand.remove();
                attackArmorStand = null;
                target = null;
            }
        }
    }

    protected void deactivate() {
        if (armorStand == null) {
            return;
        }
        Location playerLocation = getEffectedPlayer().getLocation();
        Vector direction = playerLocation.getDirection().clone().setY(0).normalize().multiply(-distanceBehind);
        // Offset the location a little to the right because the sword is not centered
        Vector rightOffset = direction.clone().crossProduct(new Vector(0, -1, 0)).normalize().multiply(1);

        armorStand.getWorld().spawnParticle(Particle.SMOKE_NORMAL, armorStand.getLocation().clone().add(rightOffset).add(0, 1.3, 0), 20, 0.2, 0.5, 0.2, 0.03);
        armorStand.remove();
        armorStand = null;
        summonCooldown = 0;
        attackCooldown = 0;
        disappear = 0;
    }

    protected void summonGodless() {
        Location playerLocation = getEffectedPlayer().getLocation();
        Vector direction = playerLocation.getDirection().clone().setY(0).normalize().multiply(-distanceBehind);
        // Offset the location a little to the right because the sword is not centered

        Vector rightOffset = direction.clone().crossProduct(new Vector(0, -1, 0)).normalize().multiply(1);
        Vector leftOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(1);

        direction.add(leftOffset);

        Location newLocation = playerLocation.add(direction);

        newLocation.setYaw(playerLocation.getYaw());
        newLocation.setPitch(0);
        newLocation.add(0,  current_y_offset, 0);

        // Spawn the armor stand at the calculated location
        ArmorStand armorStand = getEffectedPlayer().getWorld().spawn(newLocation, ArmorStand.class);

        // Set armor stand properties
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.getPersistentDataContainer().set(Keys.atomizer, PersistentDataType.BOOLEAN, true);
        armorStand.getPersistentDataContainer().set(Keys.noSteal, PersistentDataType.BOOLEAN, true);
        armorStand.getPersistentDataContainer().set(Keys.dynamicObject, PersistentDataType.BOOLEAN, true);
        armorStand.setRemoveWhenFarAway(false);
        new DynamicEffect(armorStand);
        // Set right arm pose
        armorStand.setRightArmPose(new EulerAngle(0, Math.toRadians(270), Math.toRadians(120))); // Adjust angle as needed

        // Set item in main hand
        armorStand.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        // Summon particles

        armorStand.getWorld().spawnParticle(Particle.FLASH, armorStand.getLocation().clone().add(rightOffset).add(0, 1, 0), 1, 0, 0, 0, 0.01);

        this.armorStand = armorStand;
    }

    private void startAttack(Entity entity) {
        attacking = true;
        target = entity;
    }

    protected void attack(Entity entity) {
        super.attack(entity);
        attackArmorStand.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));


    }

    public void handlePlayerDamage(EntityDamageByEntityEvent event) {
        if (hostilePlayers.contains(event.getDamager())) {
            return;
        }
        hostilePlayers.add((Player) event.getDamager());
    }

}
