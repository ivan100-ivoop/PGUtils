package com.github.pgutils.entities;

import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PlayerChestReward;
import com.github.pgutils.enums.LobbyMode;
import com.github.pgutils.interfaces.EvenDependent;
import com.github.pgutils.interfaces.EvenIndependent;
import com.github.pgutils.utils.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import com.github.pgutils.enums.LobbyStatus;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Lobby {

    public static List<Lobby> lobbies = new ArrayList<>();

    private int ID;

    private String uniqueID;

    // Saved
    private Location pos;

    private List<Player> players;

    private LobbyStatus status;

    private List<PlaySpace> playSpaces = new ArrayList<>();

    private PlaySpace currentPlaySpace = null;

    private int lastGame = 0;

    private int pickedGameID = 0;

    // Saved
    private int maxPlayers = 32;

    // Saved
    private int minPlayers = 2;

    private int lobbyStartingTime = 200;

    private int lobbyResettingTime = 200;

    private int lobbyStartingTick = 0;

    private int lobbyResettingTick = 0;

    private int showPlayersMessageTime = 5;

    private int showPlayersMessageTick = 0;

    // Saved
    private LobbyMode mode = LobbyMode.AUTO;

    // Saved
    private boolean isLocked = false;

    public Lobby() {
        players = new ArrayList<>();
        status = LobbyStatus.WAITING_FOR_PLAYERS;
        lobbies.add(this);
        // Generate a unique ID
        ID = lobbies.size();
        uniqueID = GeneralUtils.generateUniqueID();
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
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eWaiting for players &b%players%/%min_players% &e!").replace("%players%", String.valueOf(players.size())).replace("%min_players%", String.valueOf(minPlayers)))));
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
                            .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eThe game will start in &b%time%&e seconds!").replace("%time%", String.valueOf((lobbyStartingTime - lobbyStartingTick) / 20)))));
            if (players.size() < minPlayers) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyStartingTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&4Game starting failed due to not enough players!"))));
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
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&6The lobby is resetting!"))));
        }
    }

    private int pickRandomGame() {
        if (playSpaces.size() == 0) return -1;
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
        if (mode == LobbyMode.AUTO) {
            pickedGameID = pickRandomGame();
            if (pickedGameID == -1) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyStartingTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eGame starting failed due to no suitable gamemodes!"))));
                return;
            }
        }
        status = LobbyStatus.IN_PROGRESS;
        System.out.println("Starting game " + pickedGameID);
        currentPlaySpace = playSpaces.get(pickedGameID);
        currentPlaySpace.setup(players);
    }

    public void reset() {
        System.out.println("Resetting lobby " + ID);
        status = LobbyStatus.RESETTING;
        lobbyResettingTick = 0;
        pickedGameID = lastGame;
        players.stream()
                .forEach(player -> {
                    player.spigot()
                        .sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(GeneralUtils.fixColors("&eThe game has been ended!")));
                    player.teleport(pos);
                    PlayerManager.disablePVP(player);
                });


    }

    public void addPlayer(Player player) {
        if (isLocked) {
            player.sendMessage(Messages.messageWithPrefix("error-lobby-locked", "&cLobby is locked!"));
            return;
        }
        if (players.size() >= maxPlayers){
            player.sendMessage(Messages.messageWithPrefix("error-lobby-full", "&cLobby is full!"));
            return;
        }
        if (players.contains(player)) {
            player.sendMessage(Messages.messageWithPrefix("error-already-in-lobby", "&cYou are already in the lobby!"));
            return;
        }
        GeneralUtils.kickPlayerGlobal(player);
        player.sendMessage(Messages.messageWithPrefix("success-joined-lobby", "&aYou have joined lobby &6%id% &a!").replace("%id%", String.valueOf(ID)));
        player.teleport(pos);
        PlayerChestReward.saveInv(player);
        PlayerManager.disablePVP(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1000000, 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 5, true, false));
        players.add(player);
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) {
            player.sendMessage(Messages.messageWithPrefix("error-not-in-lobby", "&cYou are not in the lobby!"));
            return;
        }
        if (currentPlaySpace != null) {
            if (currentPlaySpace.players.contains(player)) {
                currentPlaySpace.removePlayer(player);
            }
        }
        player.sendMessage(Messages.messageWithPrefix("success-left-lobby", "&aYou have left lobby &6%id% &a!").replace("%id%", String.valueOf(ID)));
        PlayerChestReward.restoreInv(player);
        player.teleport(GeneralUtils.getRespawnPoint());
        player.removePotionEffect(PotionEffectType.SATURATION);
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        PlayerManager.enablePVP(player);

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

    public void delete() {
        kickAll();
        if (getCurrentPlaySpace() != null)
            getCurrentPlaySpace().end();
        playSpaces.stream().forEach(playSpace -> playSpace.end());
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getCurrentPlayersAmount() {
        return players.size();
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

    public String getStatus(){
        return (status == LobbyStatus.STARTING ? "Starting" : (status == LobbyStatus.IN_PROGRESS ? "Started" : (status == LobbyStatus.WAITING_FOR_PLAYERS ? "Waiting for Players" : "Restaring" )));
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
            removePlayer(player);
            player.sendMessage(Messages.messageWithPrefix("success-kicked-from-lobby", "&4You have been kicked from lobby &6%id% &4!").replace("%id%", String.valueOf(ID)));
        }
    }

    public void kickAll() {
        for(int i = players.size() - 1; i >= 0; i--) {
            kickPlayer(players.get(i));
        }
    }

    public PlaySpace getCurrentPlaySpace() {
        return currentPlaySpace;
    }

    public List<PlaySpace> getPlaySpaces() {
        return playSpaces;
    }

    public void setCurrentPlaySpace(PlaySpace o) {
        currentPlaySpace =  o;
    }

    public void closeDown() {
        kickAll();
        getCurrentPlaySpace().end();
    }

    public Location getLocation() {
        return pos;
    }

    public void setLocation(Location pos) {
        this.pos = pos;
    }

    public String getUID() {
        return uniqueID;
    }

    public void setUID(String uid) {
        uniqueID = uid;
    }
}
