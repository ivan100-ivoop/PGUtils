package com.github.pgutils.entities;

import com.github.pgutils.enums.GameStatus;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class PlaySpace {
    public static List<PlaySpace> playSpaces;

    public Location pos;

    protected List<Player> players;

    private Lobby currentLobby = null;

    protected GameStatus status;

    protected int tick = 0;

    public PlaySpace() {
        playSpaces.add(this);
    }

    public void setCurrentLobby(Lobby lobby) {
        currentLobby = lobby;
    }

    public Lobby getCurrentLobby() {
        return currentLobby;
    }

    public void setup(List<Player> players) {
        this.players = players;
        status = GameStatus.STARTING;
        start();
    }

    abstract public void start();

    public void update() {
        tick++;
        onUpdate();
    }

    abstract public void onUpdate();

    abstract public void endProcedure();

    public void end() {
        if (currentLobby != null) {
            currentLobby.reset();
        }
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }

    public Location getPos() {
        return pos;
    }


}
