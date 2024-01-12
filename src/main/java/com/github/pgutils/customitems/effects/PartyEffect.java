package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.utils.PlayerManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyEffect extends CustomEffect {

    private int graceTick = 0;

    private int graceTime = 5;

    public PartyEffect(Player effectedPlayer) {
        super(effectedPlayer);
        PlayerManager.disableDamage(effectedPlayer);
    }

    @Override
    public void onUpdate() {
        getEffectedPlayer().getLocation().getWorld().spawnParticle(Particle.CLOUD, getEffectedPlayer().getLocation(), 2, 0, 0, 0, 0);
        if (getTicks() > 5 && getEffectedPlayer().isOnGround()) {
            graceTick++;
            if (graceTick > graceTime) {
                PlayerManager.enableDamage(getEffectedPlayer());
                CustomEffect.removeEffect(this);
            }

        }
    }
}
