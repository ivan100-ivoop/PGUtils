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

import java.util.List;
import java.util.ArrayList;

public class GoldenHarpGroundEffect extends CustomEffect {

    private int targetTime = 100;

    private int targetTick = 0;

    private int targetExpandTime = 15;
    TargetParticle targetParticle;

    private Location portalLocation;

    private Location targetLocation;

    private List<Arrow> arrows = new ArrayList<>();
    public GoldenHarpGroundEffect(Location targetlocation, Location spawnlocation) {
        super(null);

        this.portalLocation = spawnlocation.clone();

        this.targetLocation = targetlocation.clone();
        targetParticle = new TargetParticle(this.targetLocation.clone().add(0,2,0), 5, Particle.CRIT, 40, 0, 0, 3, 10, 3) {
            @Override
            public void onUpdate() {

                if (getTicks() > targetExpandTime) {
                    setRadius(getRadius() + GeneralUtils.speedFunc2(2.5, getRadius(), 10));
                    setLineOffset(getLineOffset() + GeneralUtils.speedFunc2(1.75, getLineOffset(), 10));
                    setLinesLength(getLinesLength() + GeneralUtils.speedFunc2(1.5, getLinesLength(), 10));
                }
                else if (getTicks() > 1) {
                    setRadius(getRadius() + GeneralUtils.speedFunc2(1.5, getRadius(), 5));
                    setLineOffset(getLineOffset() + GeneralUtils.speedFunc2(1.25, getLineOffset(), 5));
                    setLinesLength(getLinesLength() + GeneralUtils.speedFunc2(1.25, getLinesLength(), 5));
                }
            }

        };
    }

    @Override
    public void onUpdate() {
        targetTick++;
        targetParticle.update();
        if (targetTick < targetTime - targetTime / 3)
            launchArrows(portalLocation, targetLocation, 1, 2, 2);
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
