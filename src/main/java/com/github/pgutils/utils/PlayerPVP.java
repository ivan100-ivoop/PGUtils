package com.github.pgutils.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerPVP {

    public static List<Player> cannotDamage = new ArrayList<>();

    public static void disablePVP(Player player) {
        cannotDamage.add(player);
    }

    public static void enablePVP(Player player) {
        cannotDamage.remove(player);
    }
}
