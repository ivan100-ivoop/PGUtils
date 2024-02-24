package com.github.pgutils.particles.variants;

import org.bukkit.Location;
import org.bukkit.Particle;

public abstract class TargetParticle extends HollowCircleParticle {

    // How long the lines of the target are
    private double linesLength;

    private double initialLinesLength;

    // How many particles are in each line
    private double lineParticleCount;

    private double initialLineParticleCount;

    // How offset the lines are from the center
    private double lineOffset;

    private double initialLineOffset;

    public TargetParticle(Location location, double radius, Particle particle, int particleCount, double y_offset, double verticalSpeed, double linesLength, double lineParticleCount, double lineOffset) {
        super(location, radius, particle, particleCount, y_offset, verticalSpeed);
        this.linesLength = linesLength;
        this.initialLinesLength = linesLength;
        this.lineParticleCount = lineParticleCount;
        this.initialLineParticleCount = lineParticleCount;
        this.lineOffset = lineOffset;
        this.initialLineOffset = lineOffset;
    }

    @Override
    public void render(){
        super.render();
        Location center = getLocation().clone();
        double increment = (2 * Math.PI) / getParticleCount();

        // Render lines at 0, 90, 180, and 270 degrees from the circle center
        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2;
            for (int j = 0; j < lineParticleCount; j++) {
                double x = lineOffset * Math.cos(angle) + (j * linesLength / lineParticleCount) * Math.cos(angle);
                double z = lineOffset * Math.sin(angle) + (j * linesLength / lineParticleCount) * Math.sin(angle);
                Location lineLoc = center.clone().add(x, getY_offset(), z);
                center.getWorld().spawnParticle(getParticle(), lineLoc, 1, 0, 0, 0, 0);
            }
        }

    }

    public double getLinesLength() {
        return linesLength;
    }

    public void setLinesLength(double linesLength) {
        this.linesLength = linesLength;
    }

    public double getLineParticleCount() {
        return lineParticleCount;
    }

    public void setLineParticleCount(int lineParticleCount) {
        this.lineParticleCount = lineParticleCount;
    }

    public double getLineOffset() {
        return lineOffset;
    }

    public void setLineOffset(double lineOffset) {
        this.lineOffset = lineOffset;
    }

    public double getInitialLinesLength() {
        return initialLinesLength;
    }

    public void setInitialLinesLength(double initialLinesLength) {
        this.initialLinesLength = initialLinesLength;
    }

    public double getInitialLineParticleCount() {
        return initialLineParticleCount;
    }

    public void setInitialLineParticleCount(int initialLineParticleCount) {
        this.initialLineParticleCount = initialLineParticleCount;
    }

    public double getInitialLineOffset() {
        return initialLineOffset;
    }

    public void setInitialLineOffset(double initialLineOffset) {
        this.initialLineOffset = initialLineOffset;
    }

    public static void objectlessParticle(Location location, double radius, Particle particle, int particleCount, double y_offset, double verticalSpeed, double linesLength, double lineParticleCount, double lineOffset) {
        HollowCircleParticle.objectlessParticle(location, radius, particle, particleCount, y_offset, verticalSpeed);
        Location center = location.clone();
        double increment = (2 * Math.PI) / particleCount;

        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2;
            for (int j = 0; j < lineParticleCount; j++) {
                double x = lineOffset * Math.cos(angle) + (j * linesLength / lineParticleCount) * Math.cos(angle);
                double z = lineOffset * Math.sin(angle) + (j * linesLength / lineParticleCount) * Math.sin(angle);
                Location lineLoc = center.clone().add(x, y_offset, z);
                center.getWorld().spawnParticle(particle, lineLoc, 1, 0, 0, 0, 0);
            }
        }
    }

}
