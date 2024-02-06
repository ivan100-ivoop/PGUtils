package com.github.pgutils.particles.variants;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public abstract class HollowCircleDirParticle extends HollowCircleParticle{

    private Vector direction;
    public HollowCircleDirParticle(Location location, double radius, Particle particle, int particleCount, double y_offset, double verticalSpeed, Vector direction) {
        super(location, radius, particle, particleCount, y_offset, verticalSpeed);
        this.direction = direction;
    }

    protected void render() {
        direction.normalize(); // Normalize the direction vector

        // Find two orthogonal vectors to the direction vector to construct the circle plane
        Vector ortho1 = new Vector(direction.getZ(), 0, -direction.getX()).normalize();
        Vector ortho2 = direction.getCrossProduct(ortho1).normalize();

        for (int i = 0; i < 360; i += 360 / getParticleCount()) {
            double angle = Math.toRadians(i);
            double x = getRadius() * Math.cos(angle);
            double z = getRadius() * Math.sin(angle);

            // Project x and z onto the plane defined by the two orthogonal vectors
            Vector point = ortho1.clone().multiply(x).add(ortho2.clone().multiply(z));
            Location particleLocation = getLocation().clone().add(point);

            // Apply the y_offset in the direction of the circle's normal (direction vector)
            particleLocation.add(direction.clone().multiply(getY_offset()));

            // Spawn the particle
            particleLocation.getWorld().spawnParticle(getParticle(), particleLocation, 0, 0, getVerticalSpeed(), 0, 0, null, true);
        }
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Vector getDirection() {
        return direction;
    }

}
