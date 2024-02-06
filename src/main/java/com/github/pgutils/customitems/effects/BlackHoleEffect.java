package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.particles.EnhancedParticle;
import com.github.pgutils.particles.variants.SphericalPointParticle;
import com.github.pgutils.particles.variants.SwirlingParticle;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlackHoleEffect extends CustomEffect {

    private int blackholelife = 80;

    Location location;
    Player user;

    double initialEffectRadius = 10;
    double effectRadius;

    private List<EnhancedParticle> particles;

    public BlackHoleEffect(Player player, Location location) {
        super(null);
        this.location = location;
        this.user = player;
        this.particles = new ArrayList<>();
        effectRadius = initialEffectRadius;

        particles.add(new SphericalPointParticle(location.clone(), 20, 2, 20, Particle.SMOKE_NORMAL, effectRadius * 0.7, -0.3));
        particles.add(new SphericalPointParticle(location.clone().add(0,0.3,0), 40, 1, 20, Particle.SQUID_INK, 0.2, 0));
    }

    @Override
    public void onUpdate() {
        if (blackholelife < getTicks()) {
            CustomEffect.removeEffect(this);
        }
        for (Entity entity : location.getWorld().getNearbyEntities(location, effectRadius, effectRadius, effectRadius)) {
            if (entity instanceof LivingEntity && !entity.equals(user)) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
                        continue;

                }
                LivingEntity player = (LivingEntity) entity;
                Vector direction = location.toVector().subtract(player.getLocation().toVector());
                double distance = direction.length();
                if (distance > 0) {
                    double force = (1 - (distance / effectRadius)) * 0.2; // 0.5 is the max force at the center
                    force = Math.max(force, 0.1); // Ensure a minimum force so that suction is not too weak at the edges
                    player.setVelocity(player.getVelocity().add(direction.normalize().multiply(force)));
                }
            }
        }
        if (getTicks() % 5 == 0) {
            particles.add(new SwirlingParticle(location, effectRadius * 0.8, 0.2, 10, 0.07, Particle.CRIT, 0) {
                @Override
                public void onUpdate() {
                    if (getTick() == 1)
                        setAngle(Math.random() * 3);
                    setRadius(getRadius() - effectRadius / 25);
                    if (getRadius() < 0.1)
                        setActive(false);
                }
            });

        }
        particles.forEach(EnhancedParticle::update);
        effectRadius -= initialEffectRadius / blackholelife * 0.8;

    }

    @Override
    public void onRemove() {
        // Create a non-destructive explosion
        location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 5, 2, 2, 2, 0);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 10);
        // Get all the living entities in the area and damage them
        for (Entity entity : location.getWorld().getNearbyEntities(location, 4, 4, 4)) {
            if (entity instanceof LivingEntity) {
                LivingEntity player = (LivingEntity) entity;
                player.damage(85, user);
            }
        }

    }
}
