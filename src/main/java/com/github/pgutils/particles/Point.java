package com.github.pgutils.particles;

import org.bukkit.Location;
import org.bukkit.util.Vector;


public class Point {

    private Location location;

    private int life;

    private Vector direction;

    public Point(Location location, int life) {
        this.location = location;
        this.life = life;
    }

    public void onUpdate() {
        Location newLocation = location.clone().add(direction);
        location = newLocation;
        life--;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public Location getLocation() {
        return location;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getLife() {
        return life;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
