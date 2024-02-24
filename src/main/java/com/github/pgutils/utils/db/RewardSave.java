package com.github.pgutils.utils.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pgutils.enums.RewardsType;
import com.github.pgutils.utils.db.item.Deserialize;
import com.github.pgutils.utils.db.item.Serialize;
import org.bukkit.inventory.ItemStack;
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

    @JsonProperty("command")
    private String command;

    @JsonProperty("type")
    private RewardsType typeReward;

    @JsonSerialize(using = Serialize.class)
    @JsonDeserialize(using = Deserialize.class)
    @JsonProperty("item")
    private ItemStack item;

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

    public RewardsType getTypeReward() {
        return typeReward;
    }

    public RewardSave setTypeReward(RewardsType typeReward) {
        this.typeReward = typeReward;
        return this;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public RewardSave setItem(ItemStack item) {
        this.item = item;
        return this;
    }

}
