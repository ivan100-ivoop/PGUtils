package com.github.pgutils.particles.variants;

import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.particles.EnhancedParticle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SwirlingParticle extends EnhancedParticle {

    private double initial_radius;

    private double radius;

    private double initial_y_offset;

    private double y_offset;

    private int points;

    private double angle;

    private double speed;

    private double initial_speed;

    private Particle swirlingParticle;

    private double verticalSpeed;

    private double initialVerticalSpeed;


    public SwirlingParticle(Location location, double radius, double y_offset, int points, double speed, Particle swirlingParticle, double verticalSpeed) {
        super(location);
        this.initial_radius = radius;
        this.radius = radius;
        this.initial_y_offset = y_offset;
        this.y_offset = y_offset;
        this.points = points;
        this.angle = 0;
        this.speed = speed;
        this.initial_speed = speed;
        this.swirlingParticle = swirlingParticle;
        this.verticalSpeed = verticalSpeed;
        this.initialVerticalSpeed = verticalSpeed;
    }

    @Override
    public void render() {
        // The angle has to be rotated by the speed

        angle += speed;
        // Each point is 360/points degrees apart
        for (int i = 0; i < points; i++) {
            // Calculate the angle for this point
            double a = angle + (2 * Math.PI * i / points);
            // Calculate the x and y coordinates for this point
            double x = radius * Math.cos(a);
            double z = radius * Math.sin(a);

            Location location = getLocation().clone();
            location.add(x, y_offset, z);

            location.getWorld().spawnParticle(swirlingParticle, location, 0, 0, verticalSpeed, 0);

        }
    }

    @Override
    public void reset() {
        radius = initial_radius;
        y_offset = initial_y_offset;
        speed = initial_speed;
        verticalSpeed = initialVerticalSpeed;
        setTick(0);
    }

    @Override
    public abstract void onUpdate();

    public double getInitial_radius() {
        return initial_radius;
    }

    public void setInitial_radius(double initial_radius) {
        this.initial_radius = initial_radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getInitial_y_offset() {
        return initial_y_offset;
    }

    public void setInitial_y_offset(double initial_y_offset) {
        this.initial_y_offset = initial_y_offset;
    }

    public double getY_offset() {
        return y_offset;
    }

    public void setY_offset(double y_offset) {
        this.y_offset = y_offset;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getInitial_speed() {
        return initial_speed;
    }

    public void setInitial_speed(double initial_speed) {
        this.initial_speed = initial_speed;
    }

    public Particle getSwirlingParticle() {
        return swirlingParticle;
    }

    public void setSwirlingParticle(Particle swirlingParticle) {
        this.swirlingParticle = swirlingParticle;
    }

    public double getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(double verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public double getInitialVerticalSpeed() {
        return initialVerticalSpeed;
    }

    public void setInitialVerticalSpeed(double initialVerticalSpeed) {
        this.initialVerticalSpeed = initialVerticalSpeed;
    }

    public static void objectLessParticle(Location location, double radius, double y_offset, int points, Particle swirlingParticle, double verticalSpeed, double angle) {
        // The angle has to be rotated by the speed
        // Each point is 360/points degrees apart
        for (int i = 0; i < points; i++) {
            // Calculate the angle for this point
            double a = angle + (2 * Math.PI * i / points);
            // Calculate the x and y coordinates for this point
            double x = radius * Math.cos(a);
            double z = radius * Math.sin(a);

            Location location1 = location.clone();
            location1.add(x, y_offset, z);

            location1.getWorld().spawnParticle(swirlingParticle, location1, 0, 0, verticalSpeed, 0);

        }
    }
}
