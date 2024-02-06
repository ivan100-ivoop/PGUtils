package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LostBombEffect extends CustomEffect {

    private Player effectedPlayer;
    public LostBombEffect(Player effectedEntity) {
        super(effectedEntity);
        this.effectedPlayer = effectedEntity;
    }
    @Override
    public void onUpdate() {


    }

    @Override
    public void onRemove() {

    }
}
