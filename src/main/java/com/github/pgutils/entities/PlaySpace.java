package com.github.pgutils.entities;

import com.github.pgutils.PGUtils;
import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.utils.GameScoreboardManager;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PlaySpace {
    private int ID;

    private String UID;

    public static List<PlaySpace> playSpaces = new ArrayList<>();

    public static Map<String, Class<? extends PlaySpace>> playSpaceTypes = new HashMap<>();

    private Location pos;

    protected List<Player> players = new ArrayList<>();

    private Lobby currentLobby = null;

    protected GameStatus status = GameStatus.INACTIVE;

    private GameScoreboardManager scoreboardManager = null;

    protected int tick = 0;

    protected String type = "Typeless";

    private String name;

    public PlaySpace() {
        playSpaces.add(this);
        ID = playSpaces.size();
        UID = GeneralUtils.generateUniqueID();
        name = "PlaySpace-" + ID;
        scoreboardManager = new GameScoreboardManager();
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

    public boolean delete() {

        playSpaces.remove(this);
        if (getLobby() != null) {
            end();
            getLobby().removePlaySpace(this);
            setLobby(null);
        }
        for (int i = PGUtils.selectedPlaySpace.size() - 1; i >= 0; i--) {
            if (PGUtils.selectedPlaySpace.get(i).playSpace == this) {
                PGUtils.selectedPlaySpace.remove(i);
            }
        }
        System.out.println("Deleted PlaySpace " + ID + " playSpaces.size() = " + playSpaces.size());
        return true;
    }

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
        if (getSbManager() != null && getSbManager().hasGame(getID())) {
            getSbManager().removeGameScore(getID());
        }
        players = new ArrayList<>();
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }

    public Location getPos() {
        return pos;
    }

    public int getID() {
        return ID;
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

    public GameStatus getStatus() {
        return status;
    }

    public Location getLocation() {
        return pos;
    }

    public void setLocation(Location location) {
        this.pos = location;
    }

    public void setUID(String readObject) {
        UID = readObject;
    }

    public String getUID() {
        return UID;
    }

    public abstract boolean addGameObjects(Player player, String[] args);

    public abstract boolean removeGameObjects(Player player, String[] args);


    public abstract boolean setGameObjects(Player player, String[] args);

    public Scoreboard getScoreboard() {
        return scoreboardManager.getScoreboard(getID());
    }

    public GameScoreboardManager getSbManager() {
        return scoreboardManager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
