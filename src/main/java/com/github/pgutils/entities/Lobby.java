package com.github.pgutils.entities;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.PlayerChestReward;
import com.github.pgutils.enums.LobbyMode;
import com.github.pgutils.interfaces.EvenDependent;
import com.github.pgutils.interfaces.EvenIndependent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.github.pgutils.enums.LobbyStatus;

public class Lobby {

    public static List<Lobby> lobbies = new ArrayList<>();

    private int ID;

    private Location pos;

    List<Player> players;

    private LobbyStatus status;

    private List<PlaySpace> playSpaces = new ArrayList<>();

    private PlaySpace currentPlaySpace = null;

    private int lastGame = 0;

    private int pickedGameID = 0;

    private int maxPlayers = 32;

    private int minPlayers = 2;

    private int lobbyStartingTime = 60;

    private int lobbyResettingTime = 200;

    private int lobbyStartingTick = 0;

    private int lobbyResettingTick = 0;

    private int showPlayersMessageTime = 5;

    private int showPlayersMessageTick = 0;

    private LobbyMode mode = LobbyMode.AUTO;

    private boolean isLocked = false;

    public Lobby() {
        players = new ArrayList<>();
        status = LobbyStatus.WAITING_FOR_PLAYERS;
        lobbies.add(this);
        ID = lobbies.size();
    }

    public void update() {
        if (status == LobbyStatus.WAITING_FOR_PLAYERS) {
            if (players.size() >= minPlayers && mode == LobbyMode.AUTO) {
                startSequence();
            }
            showPlayersMessageTick++;
            if (showPlayersMessageTick >= showPlayersMessageTime) {
                showPlayersMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eWaiting for players ( " + players.size() + "/" + minPlayers + " )"))));
            }
        }
        else if (status == LobbyStatus.STARTING) {
            if (lobbyStartingTick >= lobbyStartingTime) {
                start();
            }
            lobbyStartingTick++;
            if (lobbyStartingTick % 20 == 0)
                players.stream()
                    .forEach(player -> player.spigot()
                            .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eThe game will start in &b" + (lobbyStartingTime / 20 - lobbyStartingTick / 20) + "&e seconds!"))));
            if (players.size() < minPlayers) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyStartingTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eGame starting failed due to not enough players!"))));
            }
        }
        else if (status == LobbyStatus.IN_PROGRESS) {
            currentPlaySpace.update();
        }
        else if (status == LobbyStatus.RESETTING) {
            if (lobbyResettingTick >= lobbyResettingTime) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyResettingTick = 0;
            }
            lobbyResettingTick++;
            if (lobbyResettingTick % 20 == 0)
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eThe lobby is resetting!"))));
        }
    }

    private int pickRandomGame() {
        if (playSpaces.size() == 1) return 0;
        List<PlaySpace> possiblePlaySpaces = new ArrayList<>();
        if (players.size() % 2 != 0) {
            for (PlaySpace playSpace : playSpaces) {
                if (playSpace instanceof EvenIndependent) {
                    possiblePlaySpaces.add(playSpace);
                }
            }
        }
        else {
            for (PlaySpace playSpace : playSpaces) {
                if (playSpace instanceof EvenDependent || playSpace instanceof EvenIndependent) {
                    possiblePlaySpaces.add(playSpace);
                }
            }
        }
        possiblePlaySpaces.remove(playSpaces.get(lastGame));
        return playSpaces.indexOf(possiblePlaySpaces.get((int) (Math.random() * possiblePlaySpaces.size())));
    }

    public void startSequence() {
        status = LobbyStatus.STARTING;
        lobbyStartingTick = 0;
    }

    private void start() {
        if (mode == LobbyMode.AUTO) pickedGameID = pickRandomGame();
        status = LobbyStatus.IN_PROGRESS;
        currentPlaySpace = playSpaces.get(pickedGameID);
        currentPlaySpace.setup(players);
    }

    public void reset() {
        System.out.println("Resetting lobby " + ID);
        status = LobbyStatus.RESETTING;
        lobbyResettingTick = 0;
        pickedGameID = lastGame;
        players.stream()
                .forEach(player -> player.spigot()
                        .sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(GeneralUtils.fixColors("&eThe game has been ended!"))));
        players.stream()
                .forEach(player -> player.teleport(pos));

    }

    public void addPlayer(Player player) {
        if (players.size() >= maxPlayers){
            player.sendMessage(GeneralUtils.fixColors("&cLobby is full!"));
            return;
        }
        if (players.contains(player)) {
            player.sendMessage(GeneralUtils.fixColors("&cYou are already in the lobby!"));
            return;
        }
        player.sendMessage(GeneralUtils.fixColors("&aYou have joined lobby " + ID +" !"));
        player.teleport(pos);
        PlayerChestReward.saveInv(player);
        players.add(player);
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) {
            player.sendMessage(GeneralUtils.fixColors("&cYou are not in the lobby!"));
            return;
        }
        if (currentPlaySpace != null) {
            if (currentPlaySpace.players.contains(player)) {
                currentPlaySpace.removePlayer(player);
            }
        }
        player.sendMessage(GeneralUtils.fixColors("&aYou have left lobby " + ID +" !"));
        PlayerChestReward.restoreInv(player);
        players.remove(player);
    }

    public void setMode(LobbyMode mode) {
        this.mode = mode;
    }

    public void addPlaySpace(PlaySpace playSpace) {
        playSpaces.add(playSpace);
    }

    public void removePlaySpace(PlaySpace playSpace) {
        playSpaces.remove(playSpace);
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setGame(int gameID) {
        pickedGameID = gameID;
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }
    public Location getPos() { return this.pos; }

    public int getID() {
        return ID;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public LobbyMode getMode() {
        return mode;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void kickPlayer(Player player) {
        if (players.contains(player)) {
            player.sendMessage(GeneralUtils.fixColors("&cYou have been kicked from the lobby!"));
            removePlayer(player);
        }
    }

    public void kickAll() {
        for(int i = players.size() - 1; i >= 0; i--) {
            kickPlayer(players.get(i));
        }
    }


}
