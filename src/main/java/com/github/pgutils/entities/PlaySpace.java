package com.github.pgutils.entities;

import com.github.pgutils.enums.GameStatus;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaySpace {
    private int playSpaceID;

    public static List<PlaySpace> playSpaces = new ArrayList<>();

    public Location pos;

    protected List<Player> players = new ArrayList<>();

    private Lobby currentLobby = null;

    protected GameStatus status = GameStatus.INACTIVE;

    protected int tick = 0;

    protected String type = "Typeless";

    public PlaySpace() {
        playSpaces.add(this);
        playSpaceID = playSpaces.size();
    }

    public void setCurrentLobby(Lobby lobby) {
        currentLobby = lobby;
    }

    public Lobby getCurrentLobby() {
        return currentLobby;
    }

    public void setup(List<Player> players) {
        this.players.addAll(players);
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
        endProcedure();
        reset();
        if (currentLobby != null) {
            currentLobby.reset();
        }
    }

    public void reset() {
        status = GameStatus.INACTIVE;
        tick = 0;
        players = new ArrayList<>();
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }

    public Location getPos() {
        return pos;
    }

    public int getID() {
        return playSpaceID;
    }

    public String getType() {
        return type;
    }

    public void setLobby(Lobby lobby) {
        currentLobby = lobby;
    }

    public Lobby getLobby() {
        return currentLobby;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public abstract void removePlayer(Player player);

    public abstract boolean passesChecks();

    public abstract void updateView(Player player);

}
