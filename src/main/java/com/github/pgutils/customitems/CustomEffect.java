package com.github.pgutils.customitems;

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
    }

}
