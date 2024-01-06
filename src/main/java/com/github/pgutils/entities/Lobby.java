package com.github.pgutils.entities;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.interfaces.EvenDependent;
import com.github.pgutils.interfaces.EvenIndependent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import com.github.pgutils.enums.LobbyStatus;

public class Lobby {

    private static int lobbyID;

    private Location pos;

    List<Player> players;

    private LobbyStatus status;

    private List<PlaySpace> playSpaces;

    private PlaySpace currentPlaySpace = null;

    private int lastGame = 0;

    private int pickedGameID = 0;

    private int maxPlayers = 32;

    private int minPlayers = 4;

    private int lobbyStartingTime = 20;

    private int lobbyResettingTime = 10;

    private int lobbyStartingTick = 0;

    private int lobbyResettingTick = 0;

    private int showPlayersMessageTime = 100;

    private int showPlayersMessageTick = 0;

    private int gameStartingTime = 20;

    private int gameStartingTick = 0;

    private boolean autoStart = false;

    public Lobby() {
        players = new ArrayList<>();
        status = LobbyStatus.WAITING_FOR_PLAYERS;
        lobbyID++;
    }

    public void update() {
        if (status == LobbyStatus.WAITING_FOR_PLAYERS) {
            if (players.size() >= minPlayers && autoStart) {
                startSequence();
            }
            showPlayersMessageTick++;
            if (showPlayersMessageTick >= showPlayersMessageTime) {
                showPlayersMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eThere are &b" + players.size() + "&e players in the lobby!"))));
            }
        }
        else if (status == LobbyStatus.STARTING) {
            if (lobbyStartingTick >= lobbyStartingTime) {
                start();
            }
            lobbyStartingTick++;
            gameStartingTime++;
            players.stream()
                    .forEach(player -> player.spigot()
                            .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eThe game will start in &b" + (lobbyStartingTime - lobbyStartingTick) + "&e seconds!"))));
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
                reset();
            }
            lobbyResettingTick++;
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
    }

    private void start() {
        if (autoStart) pickedGameID = pickRandomGame();
        status = LobbyStatus.IN_PROGRESS;
        currentPlaySpace = playSpaces.get(pickedGameID);
    }

    public void reset() {
        status = LobbyStatus.RESETTING;
        pickedGameID = lastGame;
        players.stream()
                .forEach(player -> player.spigot()
                        .sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(GeneralUtils.fixColors("&eThe game has been ended!"))));
        players.stream()
                .forEach(player -> player.teleport(pos));
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
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





}
