package com.github.pgutils.selections;

import com.github.pgutils.entities.Lobby;
import org.bukkit.entity.Player;

public class PlayerLobbySelector {

    public Player player;

    public Lobby lobby;

    public PlayerLobbySelector(Player player, Lobby lobby) {
        this.player = player;
        this.lobby = lobby;
    }
}
