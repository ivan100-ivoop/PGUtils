package com.github.pgutils.utils.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.icore.mysql.utils.IEntity;
import org.github.icore.mysql.utils.ITable;
import org.github.icore.mysql.utils.Indexed;
import java.util.UUID;

@ITable(name = "rewards", schema = "", catalog = "")
public class RewardSave extends IEntity<UUID> {

    @Indexed
    private UUID key;
    @JsonProperty("lobbyId")
    private String lobbyId;

    @JsonProperty("commandSet")
    private String command;

    public String getLobbyId() {
        return lobbyId;
    }

    public RewardSave setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public RewardSave setCommand(String command) {
        this.command = command;
        return this;
    }
    public RewardSave() {}

}
