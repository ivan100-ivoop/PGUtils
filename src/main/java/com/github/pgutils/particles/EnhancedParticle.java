package com.github.pgutils.particles;

import org.bukkit.Location;

public abstract class EnhancedParticle {

    private Location location;

    private int tick = 0;

    private int onTickReset = -1;

    private boolean isActive = true;

    public EnhancedParticle(Location location) {
        this.location = location.clone();
    }

    public void update() {
        tick++;
        onUpdate();
        if (isActive) render();
        if (onTickReset != -1 && tick >= onTickReset) {
            tick = 0;
            reset();
        }
    }

    protected abstract void render();

    public abstract void reset();

    public abstract void onUpdate();

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setOnTickReset(int onTickReset) {
        this.onTickReset = onTickReset;
    }

    public int getOnTickReset() {
        return onTickReset;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

}
