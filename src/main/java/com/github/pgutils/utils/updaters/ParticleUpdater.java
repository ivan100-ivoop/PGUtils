package com.github.pgutils.utils.updaters;

import com.github.pgutils.PGUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleUpdater extends BukkitRunnable {
    @Override
    public void run() {
        PGUtils.loader.selectedPlaySpace.forEach(playSpace -> {
            playSpace.playSpace.updateView(playSpace.player);
        });
    }
}
