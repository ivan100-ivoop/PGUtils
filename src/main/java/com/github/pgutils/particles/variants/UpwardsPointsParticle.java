package com.github.pgutils.particles.variants;

import com.github.pgutils.particles.EnhancedParticle;
import com.github.pgutils.particles.Point;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UpwardsPointsParticle extends EnhancedParticle {

    private int particleMaxCount;

    private int particleSpawnTime;

    private int particleSpawnTick;

    private int particleLifeTime;

    private int initialParticleMaxCount;

    private int initialParticleSpawnTime;

    private int initialParticleLifeTime;

    private List<Point> points;

    private Particle particle;

    private Vector direction;

    public UpwardsPointsParticle(Location location, int particleMaxCount, int particleSpawnTime, int particleLifeTime, Vector direction, Particle particle) {
        super(location);
        this.particleMaxCount = particleMaxCount;
        this.particleSpawnTime = particleSpawnTime;
        this.particleLifeTime = particleLifeTime;
        this.initialParticleMaxCount = particleMaxCount;
        this.initialParticleSpawnTime = particleSpawnTime;
        this.initialParticleLifeTime = particleLifeTime;
        this.points = new ArrayList<>();
        this.particle = particle;

        this.direction = direction;
    }

    @Override
    protected void render() {
        if (points.size() > 0) {
            for (Point point : points) {
                getLocation().getWorld().spawnParticle(particle, point.getLocation(), 0, 0, 0, 0);

            }
        }
    }

    @Override
    public void reset() {
        particleMaxCount = initialParticleMaxCount;
        particleSpawnTime = initialParticleSpawnTime;
        particleLifeTime = initialParticleLifeTime;
        points.clear();

    }

    @Override
    public void onUpdate() {
        if (particleSpawnTick >= particleSpawnTime) {
            if (points.size() < particleMaxCount) {
                Location location = getRandomPointInCircle(getLocation(), 1, direction);
                Point point = new Point(location, particleLifeTime);
                point.setDirection(direction);
                points.add(point);
            }
            particleSpawnTick = 0;
        }
        particleSpawnTick++;
        if (points.size() > 0) {
            for (int i = points.size() - 1; i >= 0; i--) {
                points.get(i).onUpdate();
                if (points.get(i).isDead()) {
                    points.remove(points.get(i));
                }
            }
        }

    }

    public static Location getRandomPointInCircle(Location center, double radius, Vector direction) {
        Random random = new Random();

        // Normalize the direction vector
        Vector normalisedDir = direction.clone().normalize();

        // Create a vector that is not parallel to the direction vector for cross product
        Vector notParallel = normalisedDir.clone().crossProduct(new Vector(1, 0, 0));
        if (notParallel.length() == 0) { // In case direction is parallel to the x-axis
            notParallel = normalisedDir.clone().crossProduct(new Vector(0, 0, 1));
        }

        // Create a vector that is orthogonal to the direction vector
        Vector ortho = normalisedDir.getCrossProduct(notParallel).normalize();

        // Create another vector orthogonal to both the direction and ortho vectors
        Vector ortho2 = normalisedDir.getCrossProduct(ortho).normalize();

        // Random angle and radius for the circle point
        double angle = random.nextDouble() * Math.PI * 2;
        double randomRadius = radius * Math.sqrt(random.nextDouble());

        // Calculate the point in the circle plane
        double x = randomRadius * Math.cos(angle);
        double z = randomRadius * Math.sin(angle);

        // Calculate the 3D point in space relative to the center and circle's orientation
        Vector point = center.toVector().add(ortho.multiply(x)).add(ortho2.multiply(z));


        // Return the new location
        return new Location(center.getWorld(), point.getX(), point.getY(), point.getZ());
    }

}
