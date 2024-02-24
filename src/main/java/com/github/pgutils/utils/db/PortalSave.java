package com.github.pgutils.utils.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pgutils.utils.db.location.Deserialize;
import com.github.pgutils.utils.db.location.Serialize;
import org.bukkit.Location;
import org.github.icore.mysql.utils.IEntity;
import org.github.icore.mysql.utils.ITable;
import org.github.icore.mysql.utils.Indexed;

import java.util.UUID;

@ITable(name = "portals", schema = "", catalog = "")
public class PortalSave extends IEntity<UUID> {

    @Indexed
    private UUID key;

    @JsonProperty("lobby")
    private String lobbyID;

    @JsonProperty("name")
    private String name;

    @JsonSerialize(using = Serialize.class)
    @JsonDeserialize(using = Deserialize.class)
    @JsonProperty("location1")
    private Location location1;

    @JsonSerialize(using = Serialize.class)
    @JsonDeserialize(using = Deserialize.class)
    @JsonProperty("location2")
    private Location location2;

    @JsonSerialize(using = Serialize.class)
    @JsonDeserialize(using = Deserialize.class)
    @JsonProperty("respawn")
    private Location respawn;

    public Location getLocation1(){ return this.location1; }
    public Location getLocation2(){ return this.location2; }
    public Location getRespawn(){ return this.respawn; }
    public String getName() { return this.name; }
    public String getLobbyID() { return this.lobbyID; }

    public PortalSave setLocation1(Location location){ this.location1 = location; return this; }
    public PortalSave setLocation2(Location location){ this.location2 = location; return this; }
    public PortalSave setRespawn(Location location){ this.respawn = location; return this; }
    public PortalSave setName(String name) { this.name = name; return this; }
    public PortalSave setLobbyID(String lobbyID) { this.lobbyID = lobbyID; return this; }
    public PortalSave() {}
}
