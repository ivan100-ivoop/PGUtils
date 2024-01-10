package com.github.pgutils.particles.variants;

import com.github.pgutils.particles.EnhancedParticle;

import org.bukkit.Location;
import org.bukkit.Particle;

public abstract class HollowCircleParticle extends EnhancedParticle{

    private double initial_radius;

    private double radius;

    private Particle circleParticle;

    private int particleCount;

    private double initial_y_offset;

    private double y_offset;

    private double verticalSpeed;

    private double initialVerticalSpeed;

    public HollowCircleParticle(Location location, double radius, Particle particle, int particleCount, double y_offset, double verticalSpeed) {
        super(location);
        this.initial_radius = radius;
        this.radius = radius;
        this.circleParticle = particle;
        this.particleCount = particleCount;
        this.initial_y_offset = y_offset;
        this.y_offset = y_offset;
        this.verticalSpeed = verticalSpeed;
        this.initialVerticalSpeed = verticalSpeed;
    }
    public HollowCircleParticle(Location location) {
        super(location);
    }

    @Override
    protected void render() {
        for (int i = 0; i < 360; i += 360 / particleCount) {
            double x = radius * Math.cos(Math.toRadians(i));
            double z = radius * Math.sin(Math.toRadians(i));
            Location location = getLocation().clone();
            getLocation().getWorld().spawnParticle(getParticle(), location.add(x, y_offset, z), 0, 0, verticalSpeed, 0, 0);
        }
    }

    @Override
    public void reset() {
        radius = initial_radius;
        y_offset = initial_y_offset;
        verticalSpeed = initialVerticalSpeed;
    }

    @Override
    public abstract void onUpdate();

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setInitialRadius(double initial_radius) {
        this.initial_radius = initial_radius;
    }

    public double getInitialRadius() {
        return initial_radius;
    }

    public void setParticle(Particle particle) {
        this.circleParticle = particle;
    }

    public Particle getParticle() {
        return circleParticle;
    }

    public void setParticleCount(int particleCount) {
        this.particleCount = particleCount;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public void setInitial_y_offset(double initial_y_offset) {
        this.initial_y_offset = initial_y_offset;
    }

    public double getInitial_y_offset() {
        return initial_y_offset;
    }

    public void setY_offset(double y_offset) {
        this.y_offset = y_offset;
    }

    public double getY_offset() {
        return y_offset;
    }

    public void setVerticalSpeed(double verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public double getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setInitialVerticalSpeed(double initialVerticalSpeed) {
        this.initialVerticalSpeed = initialVerticalSpeed;
    }

    public double getInitialVerticalSpeed() {
        return initialVerticalSpeed;
    }
}