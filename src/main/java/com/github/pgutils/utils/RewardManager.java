package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.enums.RewardsType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RewardManager {

    private List<Rewards> rewards = new ArrayList<>();

    public RewardManager() {
        if (rewards.isEmpty()) {
            loadRewards();
        }
    }

    public List<Rewards> getRewards() {
        if (rewards.isEmpty()) {
            loadRewards();
        }
        return rewards;
    }

    public void loadRewards() {

        if(!rewards.isEmpty()){
            rewards.clear();
        }
        FileConfiguration config = PGUtils.getPlugin(PGUtils.class).getConfig();

            for (String key : config.getConfigurationSection("rewards").getKeys(false)) {
                ConfigurationSection rewardSection = config.getConfigurationSection("rewards." + key);
                RewardsType type = (rewardSection.getString("type", "command" ).equals("command") ? RewardsType.COMMAND : RewardsType.ITEM);

                if (type == RewardsType.COMMAND) {
                    this.rewards.add(new Rewards()
                            .setType(type)
                            .setItemID(rewards.size() + 1)
                            .addCommand(rewardSection.getString("reward"))
                            .setLobbyId(rewardSection.getInt("lobby")));
                } else {
                    ConfigurationSection _reward = rewardSection.getConfigurationSection("reward");
                    ItemStack item = new ItemStack(Material.valueOf(_reward.getString("type", "STONE")), _reward.getInt("amount", 1));
                    item.setItemMeta(((ItemMeta) _reward.get("meta")));

                    this.rewards.add(new Rewards()
                            .setType(type)
                            .setItemID(rewards.size() + 1)
                            .addItem(item)
                            .setLobbyId(rewardSection.getInt("lobby")));
                }
            }
        }

    public FileConfiguration getClear(){
        FileConfiguration config = PGUtils.getPlugin(PGUtils.class).getConfig();
        config.set("rewards", null);
        PGUtils.getPlugin(PGUtils.class).saveConfig();
        return PGUtils.getPlugin(PGUtils.class).getConfig();
    }

    private void saveRewards() {

        if (!rewards.isEmpty()) {
            FileConfiguration config = getClear();

            ConfigurationSection newRewards = config.getConfigurationSection("rewards");
            for (int i = 0; i < rewards.size(); i++) {
                Rewards reward = rewards.get(i);
                ConfigurationSection update = newRewards.createSection((i + 1) + "");
                update.set("type", (reward.getType() == RewardsType.COMMAND ? "command" : "item"));

                if (reward.getType() == RewardsType.COMMAND) {
                    update.set("reward", reward.getCommand());
                } else {

                   ConfigurationSection _reward = update.createSection("reward");
                    _reward.set("meta", reward.getItem().getItemMeta());
                    _reward.set("type", reward.getItem().getType().toString());
                    _reward.set("amount", reward.getItem().getAmount());
                }

                update.set("lobby", reward.getLobbyID());
            }
            PGUtils.getPlugin(PGUtils.class).saveConfig();
        }
    }

    public boolean addCommandReward(int lobbyID, String command) {
        if (rewards.add(new Rewards()
                .setType(RewardsType.COMMAND)
                .addCommand(command)
                .setItemID(rewards.size())
                .setLobbyId(lobbyID))) {
            saveRewards();
            return true;
        }
        return false;
    }

    public boolean addItemReward(int lobbyID, ItemStack item) {
        if (rewards.add(new Rewards()
                .setType(RewardsType.ITEM)
                .addItem(item)
                .setItemID(rewards.size())
                .setLobbyId(lobbyID))) {
            saveRewards();
            return true;
        }
        return false;
    }

    public boolean removeItem(int lobbyID, int itemID) {
        if (rewards.isEmpty())
            this.loadRewards();

        Iterator<Rewards> iterator = rewards.iterator();

        while (iterator.hasNext()) {
            Rewards reward = iterator.next();

            if (reward.getLobbyID() == lobbyID && reward.getItemID() == itemID) {
                iterator.remove();
                this.saveRewards();

                return true;
            }
        }
        return false;
    }

    public void giveRewards(int lobbyID, Player player) {
        for(Rewards reward : rewards){
            if(reward.getLobbyID() == lobbyID){
                if(reward.getType() == RewardsType.COMMAND){
                    reward.setPlayer(player);
                    GeneralUtils.runCommand(Bukkit.getConsoleSender(), GeneralUtils.fixColors(reward.getCommand()));
                } else if(reward.getType() == RewardsType.ITEM){
                    PlayerChestReward.addItem(reward.getItem(), player);
                }
            }
        }
    }

    public StringBuilder getList(int lobbyID) {
        if (rewards.isEmpty()) {
            loadRewards();
        }

        StringBuilder outputList = new StringBuilder();
        outputList.append(Messages.getMessage("items-listing-lobby", "&bRewards in Lobby: &6&l%lobby%\n", false).replace("%lobby%", lobbyID + ""));
        outputList.append("------------------------\n");

        for (Rewards reward : rewards) {
            if (reward.getLobbyID() == lobbyID) {
                outputList.append(Messages.getMessage("items-listing-id", "&eID: &c%id%\n", false).replace("%id%", reward.getItemID() + ""));
                outputList.append(Messages.getMessage("items-listing-type", "&eType: &c%type%\n", false).replace("%type%", ((reward.getType() == RewardsType.COMMAND) ? "command" : "item")));

                if (reward.getType() == RewardsType.COMMAND) {
                    outputList.append(Messages.getMessage("items-listing-command", "&eCommand: &c%command%\n", false).replace("%command%", reward.getCommand()));
                } else {
                    outputList.append(Messages.getMessage("items-listing-material", "&eMaterial: &c%material%\n", false).replace("%material%", reward.getItem().getType().toString()));
                    outputList.append(Messages.getMessage("items-listing-amount", "&eAmount: &c%amount%\n", false).replace("%amount%", String.valueOf(reward.getItem().getAmount())));
                }
                outputList.append("------------------------\n");
            }
        }

        return outputList;
    }

    public class Rewards {
        private RewardsType type = null;
        private int itemID = -1;
        private int lobbyId = -1;
        private String command = null;
        private ItemStack item = null;

        public Rewards setType(RewardsType type) {
            this.type = type;
            return this;
        }

        public int getLobbyID() {
            return this.lobbyId;
        }

        public int getItemID() {
            return this.itemID;
        }

        public Rewards setItemID(int ID) {
            this.itemID = ID;
            return this;
        }

        public Rewards setLobbyId(int lobbyID) {
            this.lobbyId = lobbyID;
            return this;
        }

        public RewardsType getType() {
            return this.type;
        }

        public String getCommand() {
            return this.command;
        }

        public ItemStack getItem() {
            return this.item;
        }

        public Rewards addItem(ItemStack item) {
            if (this.type == RewardsType.ITEM) {
                this.item = item;
            }
            return this;
        }

        public Rewards addCommand(String cmd) {
            if (this.type == RewardsType.COMMAND) {
                this.command = cmd;
            }
            return this;
        }

        public Rewards setPlayer(Player player) {
            if (this.type == RewardsType.COMMAND) {
                this.command = GeneralUtils.fixColors(this.command.replace("%player%", player.getName()));
            }
            return this;
        }
    }
}
