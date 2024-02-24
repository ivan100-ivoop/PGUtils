package com.github.pgutils.particles.variants;

import com.github.pgutils.particles.EnhancedParticle;
import com.github.pgutils.particles.Point;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SphericalPointParticle extends EnhancedParticle {

    private int particleMaxCount;

    private int particleSpawnTime;

    private int particleSpawnTick;

    private int particleLifeTime;

    private int initialParticleMaxCount;

    private int initialParticleSpawnTime;

    private int initialParticleLifeTime;

    private double radius;

    private double speed;

    private double initialRadius;

    private double initialSpeed;

    private List<Point> points;

    private Particle particle;



    public SphericalPointParticle(Location location, int particleMaxCount, int particleSpawnTime, int particleLifeTime, Particle particle, double radius, double speed) {
        super(location);
        this.particleMaxCount = particleMaxCount;
        this.particleSpawnTime = particleSpawnTime;
        this.particleLifeTime = particleLifeTime;
        this.initialParticleMaxCount = particleMaxCount;
        this.initialParticleSpawnTime = particleSpawnTime;
        this.initialParticleLifeTime = particleLifeTime;
        this.radius = radius;
        this.speed = speed;
        this.initialRadius = radius;
        this.initialSpeed = speed;
        this.particle = particle;
        points = new ArrayList<>();

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
        radius = initialRadius;
        speed = initialSpeed;
        points.clear();
    }

    @Override
    public void onUpdate() {
        if (particleSpawnTick >= particleSpawnTime) {
            if (points.size() < particleMaxCount) {
                points.add(new Point(getRandomPointInSphere(getLocation(), radius), particleLifeTime));
                Vector direction = points.get(points.size() - 1).getLocation().toVector().subtract(getLocation().toVector()).normalize().multiply(speed);
                points.get(points.size() - 1).setDirection(direction);
            }
            particleSpawnTick = 0;
        }
        particleSpawnTick++;
        for (int i = points.size() - 1; i >= 0; i--) {
            Point point = points.get(i);
            point.onUpdate();
            if (point.isDead() || point.getLocation().distance(getLocation()) < 0.1) {
                points.remove(i);
            }
        }

    }

    public static Location getRandomPointInSphere(Location center, double radius) {
        Random random = new Random();
        double u = random.nextDouble();
        double v = random.nextDouble();
        double theta = u * 2 * Math.PI; // Angle around the Y axis
        double phi = Math.acos(2 * v - 1); // Angle from the Z axis
        double r = Math.cbrt(random.nextDouble()) * radius; // Cube root to distribute points uniformly

        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);

        // Spherical to Cartesian conversion
        double x = center.getX() + r * sinPhi * cosTheta;
        double y = center.getY() + r * sinPhi * sinTheta;
        double z = center.getZ() + r * cosPhi;

        return new Location(center.getWorld(), x, y, z);
    }
}
