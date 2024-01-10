package com.github.pgutils.particles.variants;

import com.github.pgutils.particles.EnhancedParticle;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.Random;

public abstract class RandomisedDirCylinderParticle extends EnhancedParticle {

    private double initial_radius;

    private double radius;

    private double initial_height;

    private double height;

    private double amountOfParticles;

    private Particle particle;

    private double verticalSpeed;

    private double initialVerticalSpeed;


    public RandomisedDirCylinderParticle(Location location, double radius, double height, double amountOfParticles, Particle particle, double verticalSpeed) {
        super(location);
        this.initial_radius = radius;
        this.radius = radius;
        this.initial_height = height;
        this.height = height;
        this.amountOfParticles = amountOfParticles;
        this.particle = particle;
        this.verticalSpeed = verticalSpeed;
        this.initialVerticalSpeed = verticalSpeed;
    }

    @Override
    protected void render() {
        for (int i = 0; i < amountOfParticles; i++) {
            Location location = getRandomPointInCylinder(getLocation(), radius, height);
            getLocation().getWorld().spawnParticle(getParticle(), location, 0, 0, verticalSpeed, 0);
        }

    }

    @Override
    public void reset() {
        radius = initial_radius;
        height = initial_height;
        verticalSpeed = initialVerticalSpeed;

    }

    @Override
    public abstract void onUpdate();


    public Location getRandomPointInCylinder(Location center, double radius, double height) {
        Random random = new Random();
        double angle = random.nextDouble() * Math.PI * 2;
        double randomRadius = radius * Math.sqrt(random.nextDouble());
        double x = center.getX() + randomRadius * Math.cos(angle);
        double z = center.getZ() + randomRadius * Math.sin(angle);
        double y = center.getY() + random.nextDouble() * height - height / 2;
        return new Location(center.getWorld(), x, y, z);
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setInitial_radius(double initial_radius) {
        this.initial_radius = initial_radius;
    }

    public double getInitial_radius() {
        return initial_radius;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public void setInitial_height(double initial_height) {
        this.initial_height = initial_height;
    }

    public double getInitial_height() {
        return initial_height;
    }

    public void setAmountOfParticles(double amountOfParticles) {
        this.amountOfParticles = amountOfParticles;
    }

    public double getAmountOfParticles() {
        return amountOfParticles;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public Particle getParticle() {
        return particle;
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
