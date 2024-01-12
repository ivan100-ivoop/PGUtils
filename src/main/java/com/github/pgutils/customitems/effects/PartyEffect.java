package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyEffect extends CustomEffect {


    public PartyEffect(Player effectedPlayer) {
        super(effectedPlayer);
    }

    @Override
    public void onUpdate() {
        getEffectedPlayer().getLocation().getWorld().spawnParticle(Particle.CLOUD, getEffectedPlayer().getLocation(), 2, 0, 0, 0, 0);
        // if player is on ground
        if (getEffectedPlayer().isOnGround()) {
            CustomEffect.removeEffect(this);
        }
    }
}
