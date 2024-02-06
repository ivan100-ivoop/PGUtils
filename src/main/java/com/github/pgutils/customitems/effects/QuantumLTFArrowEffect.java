package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.particles.EnhancedParticle;
import com.github.pgutils.particles.variants.HollowCircleDirParticle;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuantumLTFArrowEffect extends CustomEffect {

    List<EnhancedParticle> particles;

    private int arrowLifeTime = 50;

    private boolean inactive = false;

    private Player shooter;

    public QuantumLTFArrowEffect(Arrow effectedEntity) {
        super(effectedEntity);
        if (!(effectedEntity.getShooter() instanceof Player)) {
            CustomEffect.removeEffect(this);
            return;
        }
        particles = new ArrayList<>();
        shooter = (Player) effectedEntity.getShooter();
        particles.add(new HollowCircleDirParticle(getEffectedEntity().getLocation().clone().add(getEffectedEntity().getVelocity().normalize()), 0.2, Particle.CRIT, 5, 0, 0, getEffectedEntity().getVelocity().clone().normalize()) {
            @Override
            public void onUpdate() {

                setRadius(getRadius() + GeneralUtils.speedFunc2(4, getRadius(), 15));
                setParticleCount(getParticleCount() + (int) GeneralUtils.speedFunc2(110, getParticleCount(), 15));
                setLocation(getLocation().subtract(getDirection().multiply(0.2)));
            }
        });

        effectedEntity.setGravity(false);
        effectedEntity.getVelocity().normalize().multiply(4);

    }

    @Override
    public void onUpdate() {
        updateEffects();
        if (inactive && particles.size() == 0) {
            CustomEffect.removeEffect(this);
            return;
        }
        if (getEffectedEntity() == null) {
            activate();
            return;
        }
        if (arrowLifeTime < getTicks()) {
            activate();
        }
    }

    public void updateEffects() {
        getEffectedEntity().getLocation().getWorld().spawnParticle(Particle.CLOUD, getEffectedEntity().getLocation(), 1, 0, 0, 0, 0);
        if (getTicks() % 5 == 0 && !inactive) {
            particles.add(new HollowCircleDirParticle(getEffectedEntity().getLocation().clone(), 0.2, Particle.CRIT, 5, 0, 0, getEffectedEntity().getVelocity().clone().normalize()) {
                @Override
                public void onUpdate() {

                    setRadius(getRadius() + GeneralUtils.speedFunc2(5, getRadius(), 15));
                    setParticleCount(getParticleCount() + (int) GeneralUtils.speedFunc2(100, getParticleCount(), 15));
                }
            });
        }
        for (int i = particles.size() - 1; i >= 0; i--) {
            particles.get(i).update();
            if (particles.get(i).getTick() > 20) {
                particles.remove(i);
            }
        }
    }

    public void activate() {
        inactive = true;
        new BlackHoleEffect(shooter, getEffectedEntity().getLocation());
    }

    @Override
    public void onRemove() {

    }
}
