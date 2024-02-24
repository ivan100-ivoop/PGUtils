package com.github.pgutils.customitems.effects;

import com.github.pgutils.PGUtils;
import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.particles.variants.TargetParticle;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class GoldenHarpTargetEffect extends CustomEffect {

    private int targetTime = 100;

    private int targetTick = 0;

    private int targetExpandTime = 15;

    private Location portalLocation;

    private Entity targetEntity;

    private double peffectRadius;

    private double peffectLineOffset;

    private double peffectLinelength;



    private List<Arrow> arrows = new ArrayList<>();
    public GoldenHarpTargetEffect(Entity entity, Location spawnlocation) {
        super(null);

        this.portalLocation = spawnlocation.clone();

        this.targetEntity = entity;

        peffectRadius = 5;
        peffectLineOffset = 3;
        peffectLinelength = 3;

    }


    @Override
    public void onUpdate() {
        if (getTicks() > targetExpandTime) {
            peffectRadius +=  GeneralUtils.speedFunc2(2.5, peffectRadius, 10);
            peffectLineOffset +=  GeneralUtils.speedFunc2(1.75, peffectLineOffset, 10);
            peffectLinelength +=  GeneralUtils.speedFunc2(1.5, peffectLinelength, 10);
        }
        else if (getTicks() > 1) {
            peffectRadius += GeneralUtils.speedFunc2(1.5, peffectRadius, 5);
            peffectLineOffset += GeneralUtils.speedFunc2(1.25, peffectLineOffset, 5);
            peffectLinelength +=  GeneralUtils.speedFunc2(1.25, peffectLinelength, 5);
        }

        targetTick++;
        TargetParticle.objectlessParticle(targetEntity.getLocation().clone().add(0,1,0), peffectRadius, Particle.CRIT, 40, 0, 0, peffectLinelength, 10, peffectLineOffset);
        if (targetTick < targetTime - targetTime / 3)
            launchArrows(portalLocation, targetEntity.getLocation(), 1, 2, 3);
        if (targetTick >= targetTime) {
            CustomEffect.removeEffect(this);
        }

    }

    @Override
    public void onRemove() {
        for (Arrow arrow : arrows) {
            arrow.remove();
        }
    }

    private Location getRandomSphericalLocation(Location center, double radius) {
        // Randomize the elevation angle (between -90 and 90 degrees, converted to radians)
        double elevation = Math.toRadians(Math.random() * 180 - 90);
        // Randomize the azimuth angle (between 0 and 360 degrees, converted to radians)
        double azimuth = Math.toRadians(Math.random() * 360);
        // Randomize the radius for the sphere
        double randomizedRadius = Math.random() * radius;

        // Calculate the Cartesian coordinates
        double x = center.getX() + randomizedRadius * Math.cos(elevation) * Math.cos(azimuth);
        double y = center.getY() + randomizedRadius * Math.sin(elevation);
        double z = center.getZ() + randomizedRadius * Math.cos(elevation) * Math.sin(azimuth);

        return new Location(center.getWorld(), x, y, z);
    }
    public void launchArrows(Location from, Location to, int numberOfArrows, double spawnRadius, double accuracyRadius) {
        for (int i = 0; i < numberOfArrows; i++) {
            Location spawnLoc = getRandomSphericalLocation(from, spawnRadius);
            // Spawn clouds when the arrow is spawned
            spawnLoc.getWorld().spawnParticle(Particle.CLOUD, spawnLoc, 3, 0, 0, 0, 0.02);
            Arrow arrow = spawnLoc.getWorld().spawn(spawnLoc, Arrow.class);

            Vector toVector = to.toVector();
            Vector fromVector = spawnLoc.toVector();

            // Add a random offset for inaccuracy
            Vector offset = new Vector((Math.random() - 0.5) * accuracyRadius, (Math.random() - 0.5) * accuracyRadius, (Math.random() - 0.5) * accuracyRadius);
            Vector direction = toVector.clone().add(offset).subtract(fromVector).normalize();

            // You might need to adjust this speed
            double speed = 3.0;
            arrow.setVelocity(direction.multiply(speed));
            arrow.setGravity(false);
            arrow.setMetadata("GoldenHarpArrowSmall", new FixedMetadataValue(PGUtils.loader.instance, true));
            arrows.add(arrow);
        }
    }


}
