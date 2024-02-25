package com.github.pgutils.utils;

import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.service.RewardService;
import com.github.pgutils.utils.db.RewardSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RewardManager {

    public static List<RewardSave> rewards = new ArrayList<>();


    public static List<String> getRewards(int lobbyID) {
        List<String> _rewards = new ArrayList<>();

        Optional<Lobby> lobby = Lobby.lobbies.stream()
                .filter(lobby1 -> lobby1.getID() == lobbyID)
                .findFirst();

        if(lobby.isPresent()){
            Lobby _lobby = lobby.get();
            for (int i = 0; i<rewards.size(); i++){
                RewardSave reward = rewards.get(i);
                if (_lobby.getUID().equalsIgnoreCase(reward.getLobbyId())) {
                    _rewards.add(String.valueOf((i + 1)));
                }
            }

        }

        return _rewards;
    }

    public static boolean addCommandReward(int lobbyID, String command) {

        Optional<Lobby> lobby = Lobby.lobbies.stream()
                .filter(lobby1 -> lobby1.getID() == lobbyID)
                .findFirst();

        if(lobby.isPresent()) {
            Lobby _lobby = lobby.get();
            RewardSave reward = new RewardSave();
            reward.setCommand(command);
            reward.setLobbyId(_lobby.getUID());
            reward.setKey(UUID.randomUUID());
            rewards.add(reward);
            RewardService.saveRewards(reward);
            return true;
        }
        return false;
    }

    public static boolean removeItem(int lobbyID, int itemID) {
        Optional<Lobby> lobby = Lobby.lobbies.stream()
                .filter(lobby1 -> lobby1.getID() == lobbyID)
                .findFirst();

        if(lobby.isPresent()) {
            Lobby _lobby = lobby.get();
            RewardSave reward = rewards.get(( itemID - 1 ));
            if (reward.getLobbyId().equalsIgnoreCase(_lobby.getUID())){
                RewardService.deleteRewards(reward);
                return true;
            }
        }
        return false;
    }

    public static void giveRewards(int lobbyID, Player player) {
        Optional<Lobby> lobby = Lobby.lobbies.stream()
                .filter(lobby1 -> lobby1.getID() == lobbyID)
                .findFirst();

        if(lobby.isPresent()) {
            Lobby _lobby = lobby.get();
            for (RewardSave reward : rewards) {
                if (reward.getLobbyId().equalsIgnoreCase(_lobby.getUID())){
                    GeneralUtils.runCommand(Bukkit.getConsoleSender(), GeneralUtils.fixColors(reward.getCommand().replace("%player%", player.getName())));
                }
            }
        }
    }

    public static StringBuilder getList(int lobbyID) {
        StringBuilder outputList = new StringBuilder();
        outputList.append(Messages.getMessage("items-listing-lobby", "&bRewards in Lobby: &6&l%lobby%\n", false).replace("%lobby%", lobbyID + ""));
        outputList.append("------------------------\n");

        Optional<Lobby> lobby = Lobby.lobbies.stream()
                .filter(lobby1 -> lobby1.getID() == lobbyID)
                .findFirst();

        if(lobby.isPresent()) {
            Lobby _lobby = lobby.get();
            for (int i = 0; i<rewards.size(); i++) {
                RewardSave reward = rewards.get(i);
                if (reward.getLobbyId().equalsIgnoreCase(_lobby.getUID())){
                    outputList.append(Messages.getMessage("items-listing-id", "&eID: &c%id%\n", false).replace("%id%", String.valueOf(i)));
                    outputList.append(Messages.getMessage("items-listing-command", "&eCommand: &c%command%\n", false).replace("%command%", reward.getCommand()));
                }
            }
        }
        outputList.append("------------------------\n");

        return outputList;
    }
}