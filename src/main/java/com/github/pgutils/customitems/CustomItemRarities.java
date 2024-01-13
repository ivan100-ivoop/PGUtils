package com.github.pgutils.customitems;

import org.bukkit.ChatColor;

public enum CustomItemRarities {
    UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC;

    public ChatColor getColor() {
        switch (this) {
            case UNCOMMON:
                return ChatColor.GREEN;
            case RARE:
                return ChatColor.BLUE;
            case EPIC:
                return ChatColor.DARK_PURPLE;
            case LEGENDARY:
                return ChatColor.GOLD;
            case MYTHIC:
                return ChatColor.AQUA;
        }
        return null;
    }
}
