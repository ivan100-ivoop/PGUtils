package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.enums.LobbyMode;
import com.github.pgutils.selections.PlayerLobbySelector;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class UltimateUtilsX {

    public static boolean createLobby(Player player) {
        Lobby lobby = new Lobby();
        lobby.setPos(player.getLocation());
        GeneralUtils.playerSelectLobby(player, lobby);
        player.sendMessage(Messages.messageWithPrefix("create-lobby-message", "&aSuccessful created Lobby Location %size%&a!").replace("%size%", "" + Lobby.lobbies.size()));
        return true;
    }

    public static boolean removeLobby(Player player) {
        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                .filter(selector -> selector.player.equals(player))
                .findFirst();
        if (!lobbySelector.isPresent()) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        Lobby lobby = lobbySelector.get().lobby;
        Lobby.lobbies.remove(lobby);
        PGUtils.selectedLobby.remove(lobbySelector.get());
        player.sendMessage(Messages.messageWithPrefix("lobby-removed-message", "&aSuccessful removed Lobby %lobby%&a!").replace("%lobby%", "" + lobby.getID()));
        return true;
    }

    public static boolean joinLobby(Player player, String[] args) {
        if (args.length >= 2) {
            int id = Integer.parseInt(args[1]);
            Lobby lobby = GeneralUtils.getLobbyByID(id);
            if (lobby == null) {
                player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
                return true;
            } else {
                PlayerChestReward.saveInv(player);
                lobby.addPlayer(player);
                return true;
            }
        } else {
            Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                    .filter(selector -> selector.player.equals(player))
                    .findFirst();
            if (!lobbySelector.isPresent()) {
                player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
                return true;
            }
            Lobby lobby = lobbySelector.get().lobby;
            lobby.addPlayer(player);
            return true;
        }
    }

    public static boolean setLobby(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        switch (args[1].toLowerCase()) {
            case "location":
                return setLobbyLocation(player);
            case "min-players":
                return setLobbyMinPlayers(player, args);
            case "max-players":
                return setLobbyMaxPlayers(player, args);
            case "mode":
                return setLobbyMode(player, args);
            default:
                player.sendMessage("Unknown set command.");
                return true;
        }
    }

    public static boolean addGameToLobby(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        int id = Integer.parseInt(args[1]);
        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                .filter(selector -> selector.player.equals(player))
                .findFirst();
        if (!lobbySelector.isPresent()) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return false;
        }
        Lobby lobby = lobbySelector.get().lobby;
        PlaySpace playSpace = PlaySpace.playSpaces.stream()
                .filter(space -> space.getID() == id)
                .findFirst()
                .orElse(null);
        if (playSpace == null) {
            player.sendMessage(Messages.messageWithPrefix("missing-playspace-message", "&cPlaySpace is not found!"));
            return false;
        }

        if (lobby.getPlaySpaces().contains(playSpace)) {
            player.sendMessage(Messages.messageWithPrefix("game-already-added-message", "&cPlaySpace is already added!"));
            return true;
        }

        if (playSpace.getLobby() != null) {
            player.sendMessage(Messages.messageWithPrefix("game-already-added-message", "&cPlaySpace is already added!"));
            return true;
        }

        lobby.addPlaySpace(playSpace);
        playSpace.setLobby(lobby);
        player.sendMessage(Messages.messageWithPrefix("game-add-message", "&aSuccessful added %type% &ato %id% &a!").replace("%type%", "" + playSpace.getType()).replace("%id%", "" + lobby.getID()));
        return true;
    }


    public static boolean removeGameFromLobby(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        int id = Integer.parseInt(args[1]);
        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                .filter(selector -> selector.player.equals(player))
                .findFirst();
        if (!lobbySelector.isPresent()) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return false;
        }
        Lobby lobby = lobbySelector.get().lobby;
        PlaySpace playSpace = PlaySpace.playSpaces.stream()
                .filter(space -> space.getID() == id)
                .findFirst()
                .orElse(null);
        if (playSpace == null) {
            player.sendMessage(Messages.messageWithPrefix("missing-playspace-message", "&cPlaySpace is not found!"));
            return false;
        }

        if (!lobby.getPlaySpaces().contains(playSpace)) {
            player.sendMessage(Messages.messageWithPrefix("game-not-added-message", "&cPlaySpace is not added!"));
            return true;
        }

        if (playSpace.getLobby() == null) {
            player.sendMessage(Messages.messageWithPrefix("game-not-added-message", "&cPlaySpace is not added!"));
            return true;
        }

        lobby.removePlaySpace(playSpace);
        playSpace.setLobby(null);
        player.sendMessage(Messages.messageWithPrefix("game-remove-message", "&aSuccessful removed %type% &afrom %id% &a!").replace("%type%", "" + playSpace.getType()).replace("%id%", "" + lobby.getID()));
        return true;
    }
    
    public static boolean setLobbyLocation(Player player) {
        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                .filter(selector -> selector.player.equals(player))
                .findFirst();
        if (!lobbySelector.isPresent()) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        Lobby lobby = lobbySelector.get().lobby;
        lobby.setPos(player.getLocation());
        player.sendMessage(Messages.messageWithPrefix("set-lobby-location-message", "&aSuccessful set Lobby Location!"));
        return true;
    }
    
    public static boolean setLobbyMinPlayers(Player player, String[] args) {
        if (args.length < 3) {
            return false;
        }
        int minPlayers = Integer.parseInt(args[2]);
        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                .filter(selector -> selector.player.equals(player))
                .findFirst();
        if (!lobbySelector.isPresent()) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        Lobby lobby = lobbySelector.get().lobby;
        lobby.setMinPlayers(minPlayers);
        player.sendMessage(Messages.messageWithPrefix("set-lobby-min-players-message", "&aSuccessful set Lobby Min Players to %min%&a!").replace("%min%", "" + minPlayers));
        return true;
    }
    
    public static boolean setLobbyMaxPlayers(Player player, String[] args) {
        if (args.length < 3) {
            return false;
        }
        int maxPlayers = Integer.parseInt(args[2]);
        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                .filter(selector -> selector.player.equals(player))
                .findFirst();
        if (!lobbySelector.isPresent()) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        Lobby lobby = lobbySelector.get().lobby;
        lobby.setMaxPlayers(maxPlayers);
        player.sendMessage(Messages.messageWithPrefix("set-lobby-max-players-message", "&aSuccessful set Lobby Max Players to %max%&a!").replace("%max%", "" + maxPlayers));
        return true;
    }
    
    public static boolean setLobbyMode(Player player, String[] args) {
        if (args.length < 3) {
            return false;
        }
        String mode = args[2];
        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                .filter(selector -> selector.player.equals(player))
                .findFirst();
        if (!lobbySelector.isPresent()) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        Lobby lobby = lobbySelector.get().lobby;
        lobby.setMode(LobbyMode.valueOf(mode));
        player.sendMessage(Messages.messageWithPrefix("set-lobby-mode-message", "&aSuccessful set Lobby Mode to %mode%&a!").replace("%mode%", "" + mode));
        return true;
    }

    public static boolean selectLobby(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        int id = Integer.parseInt(args[1]);
        Lobby lobby = GeneralUtils.getLobbyByID(id);
        if (lobby == null) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        GeneralUtils.playerSelectLobby(player, lobby);
        player.sendMessage(Messages.messageWithPrefix("select-lobby-message", "&aSuccessful selected Lobby %id%&a!").replace("%id%", "" + lobby.getID()));
        return true;
    }

    public static boolean addGameToLobbyID(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return false;
        }
        int lobbyID = Integer.parseInt(args[1]);
        int gameID = Integer.parseInt(args[2]);
        Lobby lobby = GeneralUtils.getLobbyByID(lobbyID);
        if (lobby == null) {
            sender.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        PlaySpace playSpace = PlaySpace.playSpaces.stream()
                .filter(space -> space.getID() == gameID)
                .findFirst()
                .orElse(null);
        if (playSpace == null) {
            sender.sendMessage(Messages.messageWithPrefix("missing-playspace-message", "&cPlaySpace is not found!"));
            return true;
        }

        if (lobby.getPlaySpaces().contains(playSpace)) {
            sender.sendMessage(Messages.messageWithPrefix("game-already-added-message", "&cPlaySpace is already added!"));
            return true;
        }

        if (playSpace.getLobby() != null) {
            sender.sendMessage(Messages.messageWithPrefix("game-already-added-message", "&cPlaySpace is already added!"));
            return true;
        }

        lobby.addPlaySpace(playSpace);
        playSpace.setLobby(lobby);
        sender.sendMessage(Messages.messageWithPrefix("game-add-message", "&aSuccessful added %type% &ato %id% &a!").replace("%type%", "" + playSpace.getType()).replace("%id%", "" + lobby.getID()));
        return true;
    }

    public static boolean removeGameFromLobbyID(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return false;
        }
        int lobbyID = Integer.parseInt(args[1]);
        int gameID = Integer.parseInt(args[2]);
        Lobby lobby = GeneralUtils.getLobbyByID(lobbyID);
        if (lobby == null) {
            sender.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        PlaySpace playSpace = PlaySpace.playSpaces.stream()
                .filter(space -> space.getID() == gameID)
                .findFirst()
                .orElse(null);
        if (playSpace == null) {
            sender.sendMessage(Messages.messageWithPrefix("missing-playspace-message", "&cPlaySpace is not found!"));
            return true;
        }

        if (!lobby.getPlaySpaces().contains(playSpace)) {
            sender.sendMessage(Messages.messageWithPrefix("game-not-added-message", "&cPlaySpace is not added!"));
            return true;
        }

        if (playSpace.getLobby() == null) {
            sender.sendMessage(Messages.messageWithPrefix("game-not-added-message", "&cPlaySpace is not added!"));
            return true;
        }

        lobby.removePlaySpace(playSpace);
        playSpace.setLobby(null);
        return true;
    }

    public static boolean kickPlayerFromLobby(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }
        String name = args[1];
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            sender.sendMessage(Messages.messageWithPrefix("player-not-found-message", "&cPlayer is not found!"));
            return true;
        }
        Lobby lobby = GeneralUtils.isPlayerInGame(player);
        if (lobby == null) {
            sender.sendMessage(Messages.messageWithPrefix("player-not-in-game-message", "&cPlayer is not in game!"));
            return true;
        }

        lobby.kickPlayer(player);
        sender.sendMessage(Messages.messageWithPrefix("kick-player-message", "&aSuccessful kicked %player%&a!").replace("%player%", "" + player.getName()));
        return true;
    }

    public static boolean kickAllFromLobbyID(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }
        int id = Integer.parseInt(args[1]);
        Lobby lobby = GeneralUtils.getLobbyByID(id);
        if (lobby == null) {
            sender.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        lobby.kickAll();
        sender.sendMessage(Messages.messageWithPrefix("kick-all-message", "&aSuccessful kicked all players from %id%&a!").replace("%id%", "" + lobby.getID()));
        return true;
    }


    public static boolean forceEndCurrentLobbyGameID(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }
        int id = Integer.parseInt(args[1]);
        Lobby lobby = GeneralUtils.getLobbyByID(id);
        if (lobby == null) {
            sender.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        if (lobby.getCurrentPlaySpace() == null) {
            sender.sendMessage(Messages.messageWithPrefix("game-not-started-message", "&cGame is not started!"));
            return true;
        }
        lobby.getCurrentPlaySpace().end();
        sender.sendMessage(Messages.messageWithPrefix("force-end-message", "&aSuccessful force end game from %id%&a!").replace("%id%", "" + lobby.getID()));
        return true;
    }

    public static boolean removeLobbyID(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }
        int id = Integer.parseInt(args[1]);
        Lobby lobby = GeneralUtils.getLobbyByID(id);
        if (lobby == null) {
            sender.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        Lobby.lobbies.remove(lobby);
        sender.sendMessage(Messages.messageWithPrefix("lobby-removed-message", "&aSuccessful removed Lobby %lobby%&a!").replace("%lobby%", "" + lobby.getID()));
        return true;
    }

    public static boolean forcePullPlayerToLobby(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return false;
        }
        String name = args[1];
        int id = Integer.parseInt(args[2]);

        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            sender.sendMessage(Messages.messageWithPrefix("player-not-found-message", "&cPlayer is not found!"));
            return true;
        }
        Lobby lobby = GeneralUtils.getLobbyByID(id);
        if (lobby == null) {
            sender.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        lobby.addPlayer(player);
        sender.sendMessage(Messages.messageWithPrefix("pull-player-message", "&aSuccessful pulled %player%&a!").replace("%player%", "" + player.getName()));
        return true;
    }

    public static boolean forcePullAllInWorldToLobbyID(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        int id = Integer.parseInt(args[1]);
        Lobby lobby = GeneralUtils.getLobbyByID(id);
        if (lobby == null) {
            player.sendMessage(Messages.messageWithPrefix("lobby-missing-message", "&cLobby is not found!"));
            return true;
        }
        for (Player player_ : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(lobby.getPos().getWorld())) {
                lobby.addPlayer(player_);
            }
        }
        player.sendMessage(Messages.messageWithPrefix("pull-all-message", "&aSuccessful pulled all players from %id%&a!").replace("%id%", "" + lobby.getID()));
        return true;
    }

}
