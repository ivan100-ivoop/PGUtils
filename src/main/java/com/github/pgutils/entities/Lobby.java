package com.github.pgutils.entities;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.entity_utils.LobbyUtils;
import com.github.pgutils.enums.LobbyMode;
import com.github.pgutils.enums.LobbyStatus;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PlayerChestReward;
import com.github.pgutils.utils.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    public static List<Lobby> lobbies = new ArrayList<>();

    private int ID;

    // Saved
    private String uniqueID;

    // Saved
    private Location pos;

    private List<Player> players;

    private List<Player> waitingPlayers;

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
    private boolean tournamentMode = false;

    // Saved
    private LobbyMode mode = LobbyMode.AUTO;

    // Saved
    private boolean isLocked = false;

    // Saved
    private String name;

    public Lobby() {
        players = new ArrayList<>();
        waitingPlayers = new ArrayList<>();
        status = LobbyStatus.WAITING_FOR_PLAYERS;
        lobbies.add(this);
        // Generate a unique ID
        ID = lobbies.size();
        uniqueID = GeneralUtils.generateUniqueID();
        name = "Unnamed Lobby " + ID;
    }

    public void update() {
        if (status == LobbyStatus.WAITING_FOR_PLAYERS) {
            if (players.size() >= minPlayers && mode == LobbyMode.AUTO) {
                startSequence();
            }
            else if (players.size() >= minPlayers && mode == LobbyMode.MANUAL) {
                startWaitingForHost();
            }
            showPlayersMessageTick++;
            if (showPlayersMessageTick >= showPlayersMessageTime) {
                showPlayersMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("waiting-players", "&eWaiting for players &b%players%/%min_players% &e!", false).replace("%players%", String.valueOf(players.size())).replace("%min_players%", String.valueOf(minPlayers)))));
            }
        }
        else if (status == LobbyStatus.WAITING_FOR_HOST) {

            showPlayersMessageTick++;
            if (showPlayersMessageTick >= showPlayersMessageTime) {
                showPlayersMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("waiting-host", "&eWaiting for a host to start the game!", false))));
            }
            if (players.size() < minPlayers) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyStartingTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("filed-start-game-players", "&4Game starting failed due to not enough players!", false))));
            }

            if (mode == LobbyMode.AUTO)
                startSequence();
        }
        else if (status == LobbyStatus.STARTING) {
            if (lobbyStartingTick >= lobbyStartingTime) {
                start();
            }
            lobbyStartingTick++;
            if (lobbyStartingTick % 20 == 0)
                players.stream()
                    .forEach(player -> player.spigot()
                            .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("start-timer", "&eThe game will start in &b%time%&e seconds!", false).replace("%time%", String.valueOf((lobbyStartingTime - lobbyStartingTick) / 20)))));
            if (players.size() < minPlayers) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyStartingTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("filed-start-game-players", "&4Game starting failed due to not enough players!", false))));
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
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("lobby-status-resetting-message", "&6The lobby is resetting!", false))));
        }
    }

    private int pickRandomGame() {
        if (playSpaces.size() == 0) return -1;
        List<PlaySpace> possiblePlaySpaces = new ArrayList<>();
        for (PlaySpace playSpace : playSpaces) {
            if (checkIfPlayspaceIsValid(playSpace) == "All Good") {
                possiblePlaySpaces.add(playSpace);
            }
        }
        if (possiblePlaySpaces.size() == 0) return -1;

        if (possiblePlaySpaces.size() > 1)
            possiblePlaySpaces.remove(playSpaces.get(lastGame));
        return playSpaces.indexOf(possiblePlaySpaces.get((int) (Math.random() * possiblePlaySpaces.size())));
    }

    private String checkIfPlayspaceIsValid(PlaySpace playSpace) {
        return playSpace.passesChecks();

    }

    public void startWaitingForHost() {
        status = LobbyStatus.WAITING_FOR_HOST;
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
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("filed-start-game", "&eGame starting failed due to no suitable gamemodes!", false))));
                return;
            }
        }
        status = LobbyStatus.IN_PROGRESS;
        System.out.println("Starting game " + pickedGameID);
        currentPlaySpace = playSpaces.get(pickedGameID);
        currentPlaySpace.setup(players);
    }

    public void reset(List<Player> winner) {
        System.out.println("Resetting lobby " + ID);
        status = LobbyStatus.RESETTING;
        lobbyResettingTick = 0;
        pickedGameID = lastGame;

        if (winner != null) {

            for (Player player : winner)
                PGUtils.getPlugin(PGUtils.class).rewardManager.giveRewards(getID(), player);

            for (Player player : players) {
                if (!winner.contains(player)) {
                    kickPlayer(player);
                }
            }
        }

        players.stream()
                .forEach(player -> {
                    player.spigot()
                        .sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(Messages.getMessage("game-end-message", "&eThe game has been ended!", false)));
                    player.teleport(pos);
                    PlayerManager.disablePVP(player);
                    PlayerManager.enableMove(player);
                    if (waitingPlayers.contains(player)) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                });
        waitingPlayers.clear();

    }

    public boolean addPlayer(Player player) {
        if (isLocked) {
            player.sendMessage(Messages.messageWithPrefix("error-lobby-locked", "&cLobby is locked!"));
            return false;
        }
        if (players.size() >= maxPlayers){
            player.sendMessage(Messages.messageWithPrefix("error-lobby-full", "&cLobby is full!"));
            return false;
        }
        if (players.contains(player)) {
            player.sendMessage(Messages.messageWithPrefix("error-already-in-lobby", "&cYou are already in the lobby!"));
            return false;
        }
        GeneralUtils.kickPlayerGlobal(player);
        player.sendMessage(Messages.messageWithPrefix("success-joined-lobby", "&aYou have joined lobby &6%id% &a!").replace("%id%", String.valueOf(ID)));
        player.teleport(pos);
        PlayerChestReward.saveInv(player);
        PlayerManager.disablePVP(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1000000, 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 5, true, false));
        players.add(player);
        if(status == LobbyStatus.IN_PROGRESS){
            waitingPlayers.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(currentPlaySpace.getPos());
        }
        return true;
    }

    public boolean removePlayer(Player player) {
        if (!players.contains(player)) {
            player.sendMessage(Messages.messageWithPrefix("error-not-in-lobby", "&cYou are not in the lobby!"));
            return false;
        }
        if (currentPlaySpace != null) {
            if (currentPlaySpace.players.contains(player)) {
                currentPlaySpace.removePlayer(player);
            }
        }
        player.sendMessage(Messages.messageWithPrefix("success-left-lobby", "&aYou have left lobby &6%id% &a!").replace("%id%", String.valueOf(ID)));
        PlayerChestReward.restoreInv(player);

        Location leaveLocation = GeneralUtils.getRespawnPoint();
        if(leaveLocation != null){ player.teleport(GeneralUtils.getRespawnPoint()); }

        player.removePotionEffect(PotionEffectType.SATURATION);
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        PlayerManager.enablePVP(player);
        PlayerManager.enableMove(player);
        PlayerManager.enableDamage(player);
        if (waitingPlayers.contains(player)) {
            waitingPlayers.remove(player);
            player.setGameMode(GameMode.SURVIVAL);
        }
        players.remove(player);
        return true;
    }

    public void setMode(LobbyMode mode) {
        this.mode = mode;
        LobbyUtils.updateLobby("mode", this.mode, this.getUID());
    }

    public void addPlaySpace(PlaySpace playSpace) {
        playSpaces.add(playSpace);
    }

    public void removePlaySpace(PlaySpace playSpace) {
        playSpaces.remove(playSpace);
    }

    public boolean delete() {
        kickAll();
        LobbyUtils.deleteLobby(this.getUID());
        if (getCurrentPlaySpace() != null)
            getCurrentPlaySpace().end(null);
        playSpaces.stream().forEach(
                playSpace -> {
                    playSpace.end(null);
                    playSpace.setLobby(null);
                });
        lobbies.remove(this);
        for (int i = PGUtils.selectedLobby.size() - 1; i >= 0; i--) {
            if (PGUtils.selectedLobby.get(i).lobby == this) {
                PGUtils.selectedLobby.remove(i);
            }
        }
        System.out.println("Deleted lobby " + ID + " Lobbies left: " + lobbies.size());
        return true;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        LobbyUtils.updateLobby("max_players", this.maxPlayers, this.getUID());
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        LobbyUtils.updateLobby("min_players", this.minPlayers, this.getUID());
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

    public String setGame(int gameID) {
        if (gameID < 1 || gameID >= playSpaces.size()) {
            return "Invalid game ID!";
        }
        String check = checkIfPlayspaceIsValid(playSpaces.get(gameID - 1));
        if (check == "All Good") {
            pickedGameID = gameID;
        }
        return check;
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
        return (status == LobbyStatus.STARTING ?
                Messages.getMessage("lobby-status-starting", "&6Starting", false)
                : (status == LobbyStatus.IN_PROGRESS ? Messages.getMessage("lobby-status-started", "&aStarted", false)
                : (status == LobbyStatus.WAITING_FOR_PLAYERS ? Messages.getMessage("lobby-status-waiting", "&eWaiting for Players", false)
                : (status == LobbyStatus.RESETTING ?  Messages.getMessage("lobby-status-resetting", "&bResetting", false)
                : Messages.getMessage("lobby-status-waiting-for-host", "&eWaiting for Host", false)))));
    }

    public LobbyMode getMode() {
        return mode;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
        LobbyUtils.updateLobby("locked", this.isLocked, this.getUID());
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
        getCurrentPlaySpace().end(null);
    }

    public Location getLocation() {
        return pos;
    }

    public void setLocation(Location pos) {
        this.pos = pos;
        LobbyUtils.updateLobby("location_x", this.pos.getX(), this.getUID());
        LobbyUtils.updateLobby("location_y", this.pos.getY(), this.getUID());
        LobbyUtils.updateLobby("location_z", this.pos.getZ(), this.getUID());
        LobbyUtils.updateLobby("location_pitch", this.pos.getPitch(), this.getUID());
        LobbyUtils.updateLobby("location_yaw", this.pos.getYaw(), this.getUID());
        LobbyUtils.updateLobby("location_world", this.pos.getWorld().getName(), this.getUID());
    }

    public String getUID() {
        return uniqueID;
    }

    public void setUID(String uid) {
        uniqueID = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        LobbyUtils.updateLobby("name", this.name, this.getUID());
    }

    public void setLock(boolean lock) {
        isLocked = lock;
    }

    public void setTournamentMode(boolean tournamentMode) {
        this.tournamentMode = tournamentMode;
        setLock(tournamentMode);
    }

    public boolean startGame() {
        if (status != LobbyStatus.WAITING_FOR_HOST) {
            startSequence();
            return true;
        }


        return false;
    }

    public boolean isTournament() {
        return tournamentMode;
    }
}
