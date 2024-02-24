package com.github.pgutils.utils.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pgutils.enums.LobbyMode;
import org.bukkit.Location;
import org.github.icore.mysql.utils.IEntity;
import org.github.icore.mysql.utils.ITable;

import com.github.pgutils.utils.db.location.Deserialize;
import com.github.pgutils.utils.db.location.Serialize;
import org.github.icore.mysql.utils.Indexed;

import java.util.UUID;

@ITable(name = "lobbies", schema = "", catalog = "")
public class LobbySave extends IEntity<UUID> {

    @Indexed
    private UUID key;
    @JsonProperty("name")
    private String name;

    @JsonSerialize(using = Serialize.class)
    @JsonDeserialize(using = Deserialize.class)
    @JsonProperty("location")
    private Location location;

    @JsonProperty("tournamentMode")
    private boolean tournamentMode;

    @JsonProperty("mode")
    private LobbyMode mode;

    @JsonProperty("locked")
    private boolean locked;

    @JsonProperty("maxPlayers")
    private int maxPlayers = 32;

    @JsonProperty("minPlayers")
    private int minPlayers = 2;

    public Location getLocation(){ return this.location; }
    public String getName() { return this.name; }
    public int getMaxPlayers() { return this.maxPlayers; }
    public int getMinPlayers() { return this.minPlayers; }
    public LobbyMode getMode() { return this.mode; }
    public boolean isLocked() { return this.locked; }
    public boolean isTournamentMode() { return this.tournamentMode; }

    public LobbySave setLocation(Location location) { this.location = location; return this; }
    public LobbySave setName(String name) { this.name = name; return this; }
    public LobbySave setMinPlayers(int players){ this.minPlayers = players; return this; }
    public LobbySave setMaxPlayers(int players){ this.maxPlayers = players; return this; }
    public LobbySave setMode(LobbyMode mode) { this.mode = mode; return this; }
    public LobbySave setLocked(boolean locked) { this.locked = locked; return this; }
    public LobbySave setTournamentMode(boolean tournamentMode) { this.tournamentMode = tournamentMode; return this; }

    public LobbySave() {}
}
