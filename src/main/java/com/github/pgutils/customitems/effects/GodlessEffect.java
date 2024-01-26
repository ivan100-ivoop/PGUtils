package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.particles.variants.RandomisedDirCylinderParticle;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import com.github.pgutils.utils.Keys;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GodlessEffect extends CustomEffect {

    protected Random random = new Random();

    protected int summonCooldown = 0;

    protected int summonCooldownTime = 20;

    protected int attackCooldown = 0;

    protected int attackCooldownTime = 10;

    protected double distanceBehind = 0.5;

    protected int hoverCooldown = 0;

    protected int hoverCooldownTime = 30;

    protected boolean hoverDir = false;

    protected double current_y_offset = 1.5;

    protected ArmorStand armorStand = null;

    protected double attackRange = 7;

    protected double attackDamage = 10;

    protected double attackKnockback = 0.5;

    protected double disappearTime = 10;

    protected double disappear = 0;

    protected boolean attacking = false;

    protected int attackAnimationTime = 15;

    protected int attackAnimation = 0;

    protected Entity target = null;

    protected List<Player> hostilePlayers;

    protected ArmorStand attackArmorStand = null;

    protected Vector attackOffset = null;

    protected int animationIndex = 1;

    Player effectedPlayer;

    public GodlessEffect(Player effectedPlayer) {
        super(effectedPlayer);
        hostilePlayers = new ArrayList<>();
        this.effectedPlayer = effectedPlayer;
    }

    @Override
    public void onUpdate() {

        ItemStack item = getEffectedPlayer().getInventory().getHelmet();
        if (item != null) {
            if (item.getItemMeta() != null) {
                PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                if (!container.has(Keys.godLess, PersistentDataType.BOOLEAN)) {
                    CustomEffect.removeEffect(this);
                    return;
                }
            }
        }
        if (summonCooldown < summonCooldownTime) {
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

                Vector rightOffset = direction.clone().crossProduct(new Vector(0, -1, 0)).normalize().multiply(0.5);
                Vector leftOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.5);

                direction.add(rightOffset);

                Location newLocation = playerLocation.add(direction);
                newLocation.setYaw(playerLocation.getYaw() + 90);
                newLocation.setPitch(0);

                armorStand.teleport(newLocation);
                hoverAnimation();
                RandomisedDirCylinderParticle.objectLessParticle(armorStand.getLocation().clone().add(leftOffset).add(0, 0.5, 0), 0.5, 1, 1, Particle.ELECTRIC_SPARK, 0.4);
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
        Vector leftOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.5);

        armorStand.getWorld().spawnParticle(Particle.SMOKE_NORMAL, armorStand.getLocation().clone().add(leftOffset).add(0, 0.5, 0).add(0,0.5,0), 20, 0.2, 0.5, 0.2, 0.03);
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

        Vector rightOffset = direction.clone().crossProduct(new Vector(0, -1, 0)).normalize().multiply(0.5);
        Vector leftOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.5);

        direction.add(rightOffset);

        Location newLocation = playerLocation.add(direction);

        newLocation.setYaw(playerLocation.getYaw() + 90);
        newLocation.setPitch(0);
        newLocation.add(0,  current_y_offset, 0);

        // Spawn the armor stand at the calculated location
        ArmorStand armorStand = getEffectedPlayer().getWorld().spawn(newLocation, ArmorStand.class);

        // Set armor stand properties
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.getPersistentDataContainer().set(Keys.godLess, PersistentDataType.BOOLEAN, true);
        armorStand.getPersistentDataContainer().set(Keys.noSteal, PersistentDataType.BOOLEAN, true);
        armorStand.getPersistentDataContainer().set(Keys.dynamicObject, PersistentDataType.BOOLEAN, true);

        // Set right arm pose
        armorStand.setRightArmPose(new EulerAngle(Math.toRadians(80), 0, 0)); // Adjust angle as needed

        // Set item in main hand
        armorStand.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        // Summon particles

        armorStand.getWorld().spawnParticle(Particle.FLASH, armorStand.getLocation().clone().add(leftOffset).add(0, 0.5, 0).add(0,0.5,0), 1, 0, 0, 0, 0.01);

        this.armorStand = armorStand;
    }

    public void hoverAnimation() {
        if (armorStand == null) {
            return;
        }

        if (hoverCooldown == hoverCooldownTime) {
            hoverDir = !hoverDir;
            hoverCooldown = 0;
        }
        current_y_offset += GeneralUtils.speedFunc(0, hoverCooldownTime, hoverCooldown) * 0.02 * (hoverDir ? 1 : -1);
        if (hoverCooldown < hoverCooldownTime) {
            armorStand.teleport(armorStand.getLocation().add(0, current_y_offset, 0));
            hoverCooldown++;
        }
    }

    protected void checkForTargets() {
        if (attackCooldown < attackCooldownTime) {
            attackCooldown++;
        }
        if (attackCooldown == attackCooldownTime) {
            attackCooldown = 0;
            List<Entity> nearbyEntities = armorStand.getNearbyEntities(attackRange, attackRange, attackRange);
            for (Entity entity : nearbyEntities) {
                if (entity.isDead())
                    continue;
                if (entity instanceof Player) {

                    Player player = (Player) entity;
                    if (player.equals(getEffectedPlayer())) {
                        continue;
                    }
                    if (hostilePlayers.contains(player)) {
                        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                            startAttack(entity);
                        }
                        else {
                            hostilePlayers.remove(player);
                        }
                    }
                }
                if (entity instanceof Monster) {
                    startAttack(entity);
                }
            }
        }
    }

    private void startAttack(Entity entity) {
        attacking = true;
        target = entity;
    }

    protected void attack(Entity entity) {
        // Spawn attack armor stand in front of the entity
        Location entityLocation = entity.getLocation();

        // The dir should be a vector pointing from the entity to the player
        Vector relative = getEffectedPlayer().getLocation().toVector().subtract(entityLocation.toVector()).normalize().setY(0).multiply(1.5);

        Location attackLocation = entityLocation.clone().add(relative);
        // The yaw should be an angle from the player to the entity
        attackLocation.setYaw(GeneralUtils.getAngleFromTo(getEffectedPlayer().getLocation(), entityLocation));
        attackArmorStand = entity.getWorld().spawn(attackLocation, ArmorStand.class);
        attackArmorStand.setInvisible(true);
        attackArmorStand.setInvulnerable(true);
        attackArmorStand.setGravity(false);
        attackArmorStand.setArms(true);
        attackArmorStand.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        attackArmorStand.getPersistentDataContainer().set(Keys.noSteal, PersistentDataType.BOOLEAN, true);
        attackArmorStand.getPersistentDataContainer().set(Keys.dynamicObject, PersistentDataType.BOOLEAN, true);

        animationIndex = random.nextInt(3);

    }

    public void handlePlayerDamage(EntityDamageByEntityEvent event) {
        if (hostilePlayers.contains(event.getDamager())) {
            return;
        }
        hostilePlayers.add((Player) event.getDamager());
    }

    private void damage(Entity entity) {
        Damageable damageable = (Damageable) entity;
        damageable.damage(attackDamage, getEffectedPlayer());
        Vector knockbackVelocity = entity.getLocation().toVector().subtract(getEffectedPlayer().getLocation().toVector()).normalize().setY(0).multiply(attackKnockback);
        entity.setVelocity(knockbackVelocity);
    }

    public void attackAnimations(){
        if (attackArmorStand == null) {
            return;
        }

        if (animationIndex < 0 || animationIndex > 3) {
            animationIndex = 0;
        }
        Location entityLocation = target.getLocation();
        Vector relative = getEffectedPlayer().getLocation().clone().toVector().subtract(entityLocation.toVector()).normalize().setY(0).multiply(1.5);

        switch (animationIndex) {
            case 0:
                if (attackAnimation == 1) {
                    attackArmorStand.setRightArmPose(new EulerAngle(Math.toRadians(220), 0, Math.toRadians(70)));
                }
                else if (attackAnimation <= attackAnimationTime / 3){
                    double windAngle = GeneralUtils.speedFunc2(Math.toRadians(190), attackArmorStand.getRightArmPose().getX(), 3);
                    EulerAngle newAngle = new EulerAngle(windAngle + attackArmorStand.getRightArmPose().getX(), 0, Math.toRadians(70));
                    attackArmorStand.setRightArmPose(newAngle);
                }
                else {
                    double windAngle = GeneralUtils.speedFunc2(Math.toRadians(360), attackArmorStand.getRightArmPose().getX(), 2);
                    EulerAngle newAngle = new EulerAngle(windAngle + attackArmorStand.getRightArmPose().getX(), 0, Math.toRadians(70));
                    attackArmorStand.setRightArmPose(newAngle);
                }
                break;
            case 1:
                if (attackAnimation == 1) {
                    attackArmorStand.setRightArmPose(new EulerAngle(0, 0,Math.toRadians(320) ));
                    attackOffset = new Vector(0, 0, 0);
                }
                else if (attackAnimation <= attackAnimationTime / 3){
                    double windAngle = GeneralUtils.speedFunc2(1, attackOffset.getX(), 6);
                    double windAngle2 = GeneralUtils.speedFunc2(Math.toRadians(30),attackArmorStand.getRightArmPose().getX() , 4);
                    EulerAngle newAngle = new EulerAngle(windAngle2 + attackArmorStand.getRightArmPose().getX(), 0, Math.toRadians(320));
                    attackOffset.setX(windAngle + attackOffset.getX());
                    attackArmorStand.setRightArmPose(newAngle);
                }
                else {
                    double windAngle = GeneralUtils.speedFunc2(-4, attackOffset.getX(), 2);
                    double windAngle2 = GeneralUtils.speedFunc2(0,attackArmorStand.getRightArmPose().getX() , 4);
                    attackOffset.setX(windAngle + attackOffset.getX());
                    EulerAngle newAngle = new EulerAngle(windAngle2 + attackArmorStand.getRightArmPose().getX(), 0, Math.toRadians(320));
                    attackArmorStand.setRightArmPose(newAngle);
                }
                if (attackOffset != null) {
                    Vector product = relative.clone().normalize().multiply(attackOffset.getX());
                    relative.add(product);
                }
                break;
            case 2: // Overhead slash attack
                if (attackAnimation == 1) {
                    attackArmorStand.setRightArmPose(new EulerAngle(Math.toRadians(220), 0, 0));
                    attackOffset = relative.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.5);
                }
                else if (attackAnimation <= attackAnimationTime / 3){
                    double windAngle = GeneralUtils.speedFunc2(Math.toRadians(200), attackArmorStand.getRightArmPose().getX(), 3);
                    EulerAngle newAngle = new EulerAngle(windAngle + attackArmorStand.getRightArmPose().getX(), 0, 0);
                    attackArmorStand.setRightArmPose(newAngle);
                }
                else {
                    double windAngle = GeneralUtils.speedFunc2(Math.toRadians(350), attackArmorStand.getRightArmPose().getX(), 2);
                    EulerAngle newAngle = new EulerAngle(windAngle + attackArmorStand.getRightArmPose().getX(), 0, 0);
                    attackArmorStand.setRightArmPose(newAngle);
                }
                if (attackOffset != null) {
                    relative.add(attackOffset);
                }
                break;
        }
        Location attackLocation = entityLocation.clone().add(relative);
        // The yaw should be an angle from the player to the entity
        attackLocation.setYaw(GeneralUtils.getAngleFromTo(getEffectedPlayer().getLocation(), entityLocation));
        attackArmorStand.teleport(attackLocation);

        if (attackAnimation == attackAnimationTime / 2) {
            damage(target);
        }
        if (attackAnimation == attackAnimationTime) {
            attackOffset = null;
        }
    }


    @Override
    public void onRemove() {
        deactivate();
        if (attackArmorStand != null) {
            attackArmorStand.remove();
        }
    }

    public Player getEffectedPlayer() {
        return effectedPlayer;
    }
}
