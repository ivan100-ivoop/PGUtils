package com.github.pgutils.customitems;

import org.bukkit.scheduler.BukkitRunnable;

public class CustomEffectUpdater extends BukkitRunnable {

        @Override
        public void run() {
               for (int i = CustomEffect.customEffects.size() - 1; i >= 0; i--) {
                       CustomEffect customEffect = CustomEffect.customEffects.get(i);
                       if (customEffect.isRunning()) {
                               customEffect.update();
                       } else {
                               CustomEffect.customEffects.remove(customEffect);
                       }
               }

        }
}
