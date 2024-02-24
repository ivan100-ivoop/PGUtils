package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import org.bukkit.entity.Entity;

public class JumpFatigueEffect extends CustomEffect {

    int ticks ;

    public JumpFatigueEffect(Entity effectedEntity, int ticks) {
        super(effectedEntity);
        this.ticks = ticks;
    }

    @Override
    public void onUpdate() {
        if (!CustomEffect.hasEffect(getEffectedEntity(), HumanBombEffect.class))
            ticks--;
        if (ticks <= 0) {
            CustomEffect.removeEffect(this);
        }

    }

    @Override
    public void onRemove() {

    }

    public int getTimeout() {
        return ticks;
    }
}
