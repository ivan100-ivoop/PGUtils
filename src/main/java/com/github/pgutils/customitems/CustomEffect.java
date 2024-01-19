package com.github.pgutils.customitems;

import com.github.pgutils.customitems.effects.PartyEffect;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomEffect {

    boolean isRunning = true;

    public static List<CustomEffect> customEffects = new ArrayList<>();

    private Player effectedPlayer;

    private int ticks;

    public CustomEffect(Player effectedPlayer) {
        this.effectedPlayer = effectedPlayer;
        customEffects.add(this);
    }

    public static void removeAllEffects(Player player) {
        for (int i = customEffects.size() - 1; i >= 0; i--) {
            if (customEffects.get(i).getEffectedPlayer().equals(player)) {
                removeEffect(customEffects.get(i));
            }
        }
    }

    public static CustomEffect getEffect(Player e, Class<? extends CustomEffect> partyEffectClass) {
        for (CustomEffect effect : customEffects) {
            if (effect.getEffectedPlayer().equals(e) && effect.getClass().equals(partyEffectClass)) {
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

    public Player getEffectedPlayer() {
        return effectedPlayer;
    }

    public void setEffectedPlayer(Player effectedPlayer) {
        this.effectedPlayer = effectedPlayer;
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

    public static boolean hasEffect(Player player, Class<? extends CustomEffect> effectClass) {
        for (CustomEffect effect : customEffects) {
            if (effect.getEffectedPlayer().equals(player) && effect.getClass().equals(effectClass)) {
                return true;
            }
        }
        return false;
    }

}
