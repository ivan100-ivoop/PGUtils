package com.github.pgutils.utils.updaters;

import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class LowPriorityUpdater extends BukkitRunnable {
    @Override
    public void run() {
        GeneralUtils.cleanupArmorStands();
    }
}
