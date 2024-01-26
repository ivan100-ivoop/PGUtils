package com.github.pgutils.customitems;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomEffect {

    boolean isRunning = true;

    public static List<CustomEffect> customEffects = new ArrayList<>();

    private Entity effectedEntity;

    private int ticks;

    public CustomEffect(Entity effectedEntity) {
        this.effectedEntity = effectedEntity;
        customEffects.add(this);
    }

    public static void removeAllEffects(Player player) {
        for (int i = customEffects.size() - 1; i >= 0; i--) {
            if (customEffects.get(i).getEffectedEntity() == null)
                continue;
            if (customEffects.get(i).getEffectedEntity().equals(player)) {
                removeEffect(customEffects.get(i));
            }
        }
    }

    public static CustomEffect getEffect(Player e, Class<? extends CustomEffect> partyEffectClass) {
        for (CustomEffect effect : customEffects) {
            if (effect.getEffectedEntity() == null)
                continue;
            if (effect.getEffectedEntity().equals(e) && effect.getClass().equals(partyEffectClass)) {
                return effect;
            }
        }
        return null;
    }

    public void update(){
        ticks++;
        onUpdate();
    }

    public abstract void onUpdate();

    public Entity getEffectedEntity() {
        return effectedEntity;
    }

    public void setEffectedPlayer(Player effectedPlayer) {
        this.effectedEntity = effectedPlayer;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public static void removeEffect(CustomEffect effect) {
        customEffects.remove(effect);
        effect.onRemove();
    }

    public static void removeAllEffects() {
        for (int i = customEffects.size() - 1; i >= 0; i--) {

            removeEffect(customEffects.get(i));
        }
    }

    public abstract void onRemove();

    public static boolean hasEffect(Entity player, Class<? extends CustomEffect> effectClass) {
        for (CustomEffect effect : customEffects) {
            if (effect.getEffectedEntity() == null)
                continue;

            if (effect.getEffectedEntity().equals(player) && effect.getClass().equals(effectClass)) {
                return true;
            }
        }
        return false;
    }

}
