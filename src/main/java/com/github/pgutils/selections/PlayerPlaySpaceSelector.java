package com.github.pgutils.selections;

import com.github.pgutils.entities.PlaySpace;
import org.bukkit.entity.Player;

public class PlayerPlaySpaceSelector {

    public Player player;

    public PlaySpace playSpace;


    public PlayerPlaySpaceSelector(Player player, PlaySpace playSpace) {
        this.player = player;
        this.playSpace = playSpace;
    }
}
