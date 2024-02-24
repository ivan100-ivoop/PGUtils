package com.github.pgutils.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {

    public static List<Player> cannotDamage = new ArrayList<>();

    public static List<Player> cannotMove = new ArrayList<>();

    public static List<Player> isInvulnerable = new ArrayList<>();

    public static void disablePVP(Player player) {
        if (!cannotDamage.contains(player))
            cannotDamage.add(player);
    }

    public static void enablePVP(Player player) {
        if (cannotDamage.contains(player))
            cannotDamage.remove(player);
    }

    public static void disableMove(Player player) {
        if (!cannotMove.contains(player))
            cannotMove.add(player);
    }

    public static void enableMove(Player player) {
        if (cannotMove.contains(player))
            cannotMove.remove(player);
    }

    public static void disableDamage(Player player) {
        if (!isInvulnerable.contains(player))
            isInvulnerable.add(player);
    }

    public static void enableDamage(Player player) {
        if (isInvulnerable.contains(player))
            isInvulnerable.remove(player);
    }

}
