package com.github.pgutils.utils.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pgutils.PGUtilsLoader;
import com.github.pgutils.utils.db.location.Deserialize;
import com.github.pgutils.utils.db.location.Serialize;
import org.bukkit.Location;
import org.github.icore.mysql.utils.IEntity;
import org.github.icore.mysql.utils.ITable;
import org.github.icore.mysql.utils.Indexed;
import org.github.icore.mysql.utils.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ITable(name = "settings", schema = "", catalog = "")
public class RespawnSave extends IEntity<UUID> {

    @Indexed
    private UUID key;
    @JsonSerialize(using = Serialize.class)
    @JsonDeserialize(using = Deserialize.class)
    @JsonProperty("respawn")
    private Location respawn;
    public RespawnSave setRespawn(Location location){ this.respawn = location; return this; }
    public Location getRespawn(){ return this.respawn; }
    public RespawnSave() {}
}
