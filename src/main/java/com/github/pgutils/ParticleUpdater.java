package com.github.pgutils;

import org.bukkit.scheduler.BukkitRunnable;

public class ParticleUpdater extends BukkitRunnable {
    @Override
    public void run() {
        PGUtils.selectedPlaySpace.forEach(playSpace -> {
            playSpace.playSpace.updateView(playSpace.player);
        });
    }
}
