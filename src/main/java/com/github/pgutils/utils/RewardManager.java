package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.enums.RewardsType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RewardManager {
    private List<ConfigurationSection> rewards = new ArrayList<>();
    private FileConfiguration config = PGUtils.getPlugin(PGUtils.class).getConfig();
    public boolean isHaveRewards() {
        return (this.rewards.size() >= 1);
    }

    public RewardManager(){ this.loadRewards(); }
    public void loadRewards() {
        FileConfiguration config = PGUtils.getPlugin(PGUtils.class).getConfig();

        rewards = new ArrayList<>();

        for (String key : config.getConfigurationSection("rewards").getKeys(false)) {
            ConfigurationSection rewardSection = config.getConfigurationSection("rewards." + key);
            rewards.add(rewardSection);
        }
    }

    private String parseCommand(String[] args, int from) {
        StringBuilder content = new StringBuilder();
        for (int i = args.length - 1; i >= from; i--) {
            content.append(args[i]).append(" ");
        }
        return content.toString().trim();
    }

    public void addCommandReward(String[] content, int from) {
        if (!this.isHaveRewards()) {
            this.loadRewards();
        }

        ConfigurationSection newRewardSection = new YamlConfiguration();
        newRewardSection.set("type", "command");
        newRewardSection.set("reward", this.parseCommand(content, from));

        rewards.add(newRewardSection);
        saveRewards();
    }

    public void removeItem(String Id){
        for (int i = 0; i<rewards.size(); i++){
            ConfigurationSection item = rewards.get(i);
            if(item.isConfigurationSection(Id)){
                rewards.remove(i);
                saveRewards();
                return;
            }
        }
    }
    public void addItemReward(ItemStack item) {
        if (!this.isHaveRewards()) {
            this.loadRewards();
        }

        ConfigurationSection newRewardSection = new YamlConfiguration();
        newRewardSection.set("type", "item");

        ConfigurationSection itemStack = new YamlConfiguration();
        itemStack.set("material", item.getType().toString());
        itemStack.set("amount", item.getAmount());

        newRewardSection.set("reward", itemStack);

        rewards.add(newRewardSection);
        saveRewards();
    }

    private void saveRewards() {
        FileConfiguration config = PGUtils.getPlugin(PGUtils.class).getConfig();

        if (rewards != null) {
            for (int i = 0; i < rewards.size(); i++) {
                config.set("rewards." + (i + 1), rewards.get(i));
            }
        }

        PGUtils.getPlugin(PGUtils.class).saveConfig();
    }
    public List<Rewards> getRewards() {
        List<Rewards> allRewards = new ArrayList<>();

        if (rewards != null) {
            for (ConfigurationSection rewardSection : rewards) {
                String type = rewardSection.getString("type", "command");

                Rewards reward = new Rewards().setType(type.equals("command") ? RewardsType.COMMAND : RewardsType.ITEM);

                switch (type) {
                    case "command":
                        reward.addCommand(rewardSection.getString("reward", ""));
                        break;
                    case "item":
                        ConfigurationSection item = rewardSection.getConfigurationSection("reward");
                        reward.addItem(new ItemStack(Material.getMaterial(item.getString("material", "STONE")), item.getInt("amount", 1)));
                        break;

                    default:
                        break;
                }

                allRewards.add(reward);
            }
        }

        return allRewards;
    }

    public void giveRewards(Player player) {
        if (!this.isHaveRewards()) {
            this.loadRewards();
        }

        List<Rewards> _rewards = this.getRewards();

        if (_rewards != null) {
            for (Rewards reward : _rewards) {
                if (reward.getType() == RewardsType.COMMAND) {
                    reward.setPlayer(player);
                    for (String cmd : reward.getCommands()) {
                        GeneralUtils.runCommand(Bukkit.getConsoleSender(), GeneralUtils.fixColors(cmd));
                    }
                } else if (reward.getType() == RewardsType.ITEM) {
                    for (ItemStack item : reward.getItems()) {
                        PlayerChestReward.addItem(item, player);
                    }
                }
            }
        }
    }

    public class Rewards {
        private RewardsType type = null;
        private List<String> commands = new ArrayList<>();
        private List<ItemStack> items = new ArrayList<>();

        public Rewards setType(RewardsType type) {
            this.type = type;
            return this;
        }

        public RewardsType getType() {
            return this.type;
        }

        public List<String> getCommands() {
            return this.commands;
        }

        public List<ItemStack> getItems() {
            return this.items;
        }

        public Rewards addItem(ItemStack item) {
            if (this.type == RewardsType.ITEM) {
                this.items.add(item);
            }
            return this;
        }
        public Rewards addCommand(String cmd){
            if(this.type == RewardsType.COMMAND) {
                this.commands.add((String) cmd);
            }
            return this;
        }

        public Rewards setPlayer(Player player) {
            if (this.type == RewardsType.COMMAND) {
                List<String> tmpCommands = new ArrayList<>();
                for (String command : this.commands) {
                    tmpCommands.add(GeneralUtils.fixColors(command.replace("%player%", player.getName())));
                }
                this.commands.clear();
                this.commands.addAll(tmpCommands);
            }
            return this;
        }
    }
}
