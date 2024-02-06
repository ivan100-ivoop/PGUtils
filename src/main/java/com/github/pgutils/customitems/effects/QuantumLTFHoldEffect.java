package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.particles.EnhancedParticle;
import com.github.pgutils.particles.variants.SphericalPointParticle;
import com.github.pgutils.particles.variants.SwirlingParticle;
import com.github.pgutils.particles.variants.UpwardsPointsParticle;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Keys;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class QuantumLTFHoldEffect extends CustomEffect {
    private Player effectedPlayer;

    private List<EnhancedParticle> particles;

    private int chargeTime =  200;

    private int chargeTick = 0;


    private boolean holding = false;

    public QuantumLTFHoldEffect(Player effectedEntity) {
        super(effectedEntity);
        System.out.println("QuantumLTFHoldEffect");
        this.effectedPlayer = effectedEntity;
        this.particles = new ArrayList<>();
        particles.add(new UpwardsPointsParticle(effectedEntity.getLocation(), 20, 7, 10, new Vector(0,0.15,0), Particle.CRIT_MAGIC));
        particles.add(new SwirlingParticle(effectedEntity.getLocation(), 0.6, 0.2, 1, 0.2, Particle.SOUL_FIRE_FLAME, 0) {
            @Override
            public void onUpdate() {

            }
        });
        particles.add(new SwirlingParticle(effectedEntity.getLocation(), 1.5, 0.2, 4, 0.2, Particle.SOUL_FIRE_FLAME, 0) {
            @Override
            public void onUpdate() {
                if (getTick() == 1)
                    setAngle(Math.random() * 3);
                setRadius(getRadius() - 0.1);
            }
        });
        particles.get(particles.size() - 1).setOnTickReset(15);
        particles.get(particles.size() - 1).setActive(false);
        particles.add(new SphericalPointParticle(effectedEntity.getLocation().clone().add(0,1.5,0), 20, 2, 8, Particle.SOUL_FIRE_FLAME, 1, 0.2));
    }

    @Override
    public void onUpdate() {
        if (effectedPlayer == null) {
            CustomEffect.removeEffect(this);
            return;
        }

        ItemStack quantum = effectedPlayer.getInventory().getItemInMainHand();
        if (quantum == null) {
            CustomEffect.removeEffect(this);
            return;
        }
        if (quantum.getItemMeta() == null) {
            CustomEffect.removeEffect(this);
            return;
        }
        if (!quantum.getItemMeta().getPersistentDataContainer().has(Keys.quantumLTF, PersistentDataType.BOOLEAN)) {
            CustomEffect.removeEffect(this);
            return;
        }

        if (holding) {
            if (!effectedPlayer.isSneaking())
                holding = false;
            if(chargeTick < chargeTime)
                chargeTick++;
            particles.get(particles.size() - 2).setActive(true);
            particles.get(particles.size() - 3).setActive(false);
            if (chargeTime == chargeTick) {
                particles.get(particles.size() - 1).setActive(true);
            }
        }
        else {
            particles.get(particles.size() - 1).setActive(false);
            particles.get(particles.size() - 2).setActive(false);
            particles.get(particles.size() - 3).setActive(true);
        }

        effectedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(quantumLoadingBar()));
        if (((chargeTick % (chargeTime / 4)) == 1 || chargeTick == chargeTime - 1) && chargeTick != 1)
            effectedPlayer.playSound(effectedPlayer.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.5f, 1f);



        for (EnhancedParticle particle : particles) {
            particle.setLocation(effectedPlayer.getLocation());
            particle.update();
        }
    }

    @Override
    public void onRemove() {

    }

    public void setChargeTick(int chargeTick) {
        this.chargeTick = chargeTick;
    }

    public int getChargeTick() {
        return chargeTick;
    }

    public void setEffectedPlayer(Player effectedPlayer) {
        this.effectedPlayer = effectedPlayer;
    }

    public Player getEffectedPlayer() {
        return effectedPlayer;
    }

    public void holding() {
        holding = true;
        chargeTick = 0;
    }

    public void notHolding() {
        holding = false;
        chargeTick = 0;
    }

    public void shoot(EntityShootBowEvent shootEvent) {
        if (chargeTick < chargeTime) {
            shootEvent.setCancelled(true);
            return;
        }
        new QuantumLTFArrowEffect((Arrow) shootEvent.getProjectile());
        holding = false;
        chargeTick = 0;
    }
    // IF the bar is fully charged it turns green

    public String quantumLoadingBar() {
        String bar = "";
        int charge = chargeTick / (chargeTime / 4);
        for (int i = 0; i < 4; i++) {
            if (i < charge) {
                bar += "§e➤  ";
            }
            else {
                bar += "§7➤  ";
            }
        }
        // Replace the other colors with green if the bar is fully charged
        if (charge == 4) {
            if (getTicks() % 10 < 5)
                bar = bar.replace("§e", "§a");
            else
                bar = bar.replace("§e", "§2");
        }
        return bar;


    }
}
