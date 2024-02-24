package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import org.bukkit.entity.Entity;

public class DynamicEffect extends CustomEffect {
    public DynamicEffect(Entity effectedEntity) {
        super(effectedEntity);
    }

    @Override
    public void onUpdate() {
        if (getEffectedEntity() == null)
            CustomEffect.removeEffect(this);
    }

    @Override
    public void onRemove() {

    }
}
