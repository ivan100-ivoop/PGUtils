package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.utils.PlayerManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class PartyEffect extends CustomEffect {

    private int graceTick = 0;

    private int graceTime = 5;

    Player effectedPlayer;

    public PartyEffect(Player effectedPlayer) {
        super(effectedPlayer);
        PlayerManager.disableDamage(effectedPlayer);
        this.effectedPlayer = effectedPlayer;
    }

    @Override
    public void onUpdate() {
        getEffectedEntity().getLocation().getWorld().spawnParticle(Particle.CLOUD, getEffectedEntity().getLocation(), 2, 0, 0, 0, 0);
        if (getTicks() > 5 && getEffectedEntity().isOnGround()) {
            graceTick++;
            if (graceTick > graceTime) {
                PlayerManager.enableDamage(effectedPlayer);
                CustomEffect.removeEffect(this);
            }

        }
    }

    @Override
    public void onRemove() {

    }
}
