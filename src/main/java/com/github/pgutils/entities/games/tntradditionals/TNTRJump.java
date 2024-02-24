package com.github.pgutils.entities.games.tntradditionals;


import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.customitems.effects.HumanBombEffect;
import com.github.pgutils.customitems.effects.JumpFatigueEffect;
import com.github.pgutils.entities.helpfulutils.ClientboundArmorstand;
import com.github.pgutils.particles.EnhancedParticle;
import com.github.pgutils.particles.variants.HollowCircleParticle;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TNTRJump {

    private String uid;

    private Location pos;

    // Saved
    private double radius = 0.5;

    // Saved
    private double strength = 1;

    // Saved
    private int cooldown = 400;

    private boolean enabled = false;

    private Map<Player, ClientboundArmorstand> texts;

    private List<EnhancedParticle> particles;

    public TNTRJump(Location pos, double radius, double strength) {
        this.pos = pos;
        this.uid = GeneralUtils.generateUniqueID();
        this.radius = radius;
        this.strength = strength;
        this.texts = new HashMap<>();
        this.particles = new ArrayList<>();

        //HollowCircleParticle(Location location, double radius, Particle particle, int particleCount, double y_offset, double verticalSpeed)
        particles.add(new HollowCircleParticle(pos, radius, Particle.END_ROD, 30, 0, 0) {
            @Override
            public void onUpdate() {

            }
        });
        particles.add(new HollowCircleParticle(pos, radius, Particle.COMPOSTER, 30, 0, 0) {
            @Override
            public void onUpdate() {
                if (getTick() > 5) {
                    setY_offset(getY_offset() + GeneralUtils.speedFunc2(1, getY_offset(), 20));
                }
                setActive(false);
                if (getTick() % 2 == 0) {
                    setActive(true);
                }

            }
        });
        particles.get(particles.size() - 1).setOnTickReset(40);
    }
    public void reset() {
        texts.values().forEach(ClientboundArmorstand::remove);
        particles.forEach(EnhancedParticle::reset);
        texts.clear();
    }

    public void setupForPlayers(List<Player> players) {
        players.forEach(player -> {
            System.out.println("Setting up for player " + player.getName());
            ClientboundArmorstand text = new ClientboundArmorstand(player, pos, "§c§lWaiting to start!");
            texts.put(player,text);
        });
    }

    public void updateForPlayers() {
       // Update each text based on the player's timeout
        particles.forEach(EnhancedParticle::update);
        texts.forEach((player, text) -> {
            if (CustomEffect.hasEffect(player, JumpFatigueEffect.class)) {
                JumpFatigueEffect effect = (JumpFatigueEffect) CustomEffect.getEffect(player, JumpFatigueEffect.class);
                text.updateText("§c§l[ " + (effect.getTimeout() / 20) + " ]");
            }
            else if (CustomEffect.hasEffect(player, HumanBombEffect.class)) {
                text.updateText("§c§l[ X ]");
            }
            else {
                text.updateText("§a§l[ Ready for use! ]");
                checkIfPlayerIsInRadius(player);
            }
        });
    }

    public void playerUse(Player p) {
        p.setVelocity(pos.getDirection().multiply(strength));
        new JumpFatigueEffect(p, cooldown);
    }

    public void checkIfPlayerIsInRadius(Player p) {
        if (p.getLocation().distance(pos) < radius) {
            playerUse(p);
        }
    }

    public Location getPos() {
        return pos;
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }

    public String getID() {
        return uid;
    }

    public void setID(String uid) {
        this.uid = uid;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public double getStrength() {
        return strength;
    }
}
