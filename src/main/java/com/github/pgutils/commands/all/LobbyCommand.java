package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.KOTHArena;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.enums.LobbyMode;
import com.github.pgutils.interfaces.PGSubCommand;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.LobbyMenu;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PlayerChestReward;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class LobbyCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "lobby";
    }

    @Override
    public String getDescription() {
        return "Lobby Command";
    }

    @Override
    public String getPermission() {
        return "pgutils.lobby";
    }

    @Override
    public String getUsage() {
        return "/pg lobby [<args>]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        for (String arg : args){
            System.out.println(arg);
        }

        if(sender instanceof Player){
            Player player = (Player) sender;

            if (args.length == 0) {
                new LobbyMenu().prepareMenu().getLobby(player);
                return true;
            }

            if(player.hasPermission("pgutils.lobby.admin")) {

                if (args[0].equalsIgnoreCase("create")) {
                    Lobby lobby = new Lobby();
                    lobby.setPos(player.getLocation());
                    GeneralUtils.playerSelectLobby(player, lobby);
                    player.sendMessage(Messages.messageWithPrefix("create-lobby-message", "&aSuccessful created Lobby Location %size%&a!").replace("%size%", "" + Lobby.lobbies.size()));
                    return true;
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                            .filter(selector -> selector.player.equals(player))
                            .findFirst();
                    if (!lobbySelector.isPresent()) {
                        player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                        return false;
                    }
                    Lobby lobby = lobbySelector.get().lobby;
                    Lobby.lobbies.remove(lobby);
                    PGUtils.selectedLobby.remove(lobbySelector.get());
                    player.sendMessage(Messages.messageWithPrefix("looby-removed-message", "&aSuccessful removed Lobby %lobby%&a!").replace("%lobby%", "" + lobby.getID()));
                    return true;
                }

                if (args[0].equalsIgnoreCase("join")) {
                    if (args.length >= 1) {
                        int id = Integer.parseInt(args[1]);
                        Lobby lobby = Lobby.lobbies.stream()
                                .filter(lobby_ -> lobby_.getID() == id)
                                .findFirst()
                                .orElse(null);
                        if (lobby == null) {
                            player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                            return false;
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
                            player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                            return false;
                        }
                        Lobby lobby = lobbySelector.get().lobby;
                        lobby.addPlayer(player);
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("set")) {

                    if (args[1].equalsIgnoreCase("location")) {
                        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                                .filter(selector -> selector.player.equals(player))
                                .findFirst();
                        if (!lobbySelector.isPresent()) {
                            player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                            return false;
                        }
                        Lobby lobby = lobbySelector.get().lobby;
                        lobby.setPos(player.getLocation());
                        player.sendMessage(Messages.messageWithPrefix("set-lobby-message", "&aSuccessful set Lobby Location!"));
                        return true;
                    }

                    if (args.length >= 2) {
                        if (args[1].equalsIgnoreCase("min-players")) {
                            int minPlayers = Integer.parseInt(args[2]);
                            Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                                    .filter(selector -> selector.player.equals(player))
                                    .findFirst();
                            if (!lobbySelector.isPresent()) {
                                player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                                return false;
                            }
                            Lobby lobby = lobbySelector.get().lobby;
                            lobby.setMinPlayers(minPlayers);
                            player.sendMessage(Messages.messageWithPrefix("set-min-players-message", "&aSuccessful set Min Players to %min%&a!").replace("%min%", "" + minPlayers));
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("max-players")) {
                            int maxPlayers = Integer.parseInt(args[2]);
                            Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                                    .filter(selector -> selector.player.equals(player))
                                    .findFirst();
                            if (!lobbySelector.isPresent()) {
                                player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                                return false;
                            }
                            Lobby lobby = lobbySelector.get().lobby;
                            lobby.setMaxPlayers(maxPlayers);
                            player.sendMessage(Messages.messageWithPrefix("set-max-players-message", "&aSuccessful  set Max Players to %max%&a!").replace("%max%", "" + maxPlayers));
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("mode")) {
                            String mode = args[2];
                            Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                                    .filter(selector -> selector.player.equals(player))
                                    .findFirst();
                            if (!lobbySelector.isPresent()) {
                                player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                                return false;
                            }
                            Lobby lobby = lobbySelector.get().lobby;

                            lobby.setMode(LobbyMode.valueOf(mode));
                            player.sendMessage(Messages.messageWithPrefix("set-mode-message", "&aSuccessful set Mode to %mode%&a!").replace("%mode%", mode));
                            return true;
                        }
                    }
                    return false;
                }


                if (args[0].equalsIgnoreCase("add-game")) {
                    if (args.length >= 1) {
                        int id = Integer.parseInt(args[1]);
                        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                                .filter(selector -> selector.player.equals(player))
                                .findFirst();
                        if (!lobbySelector.isPresent()) {
                            player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
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
                        lobby.addPlaySpace(playSpace);
                        playSpace.setLobby(lobby);
                        player.sendMessage(Messages.messageWithPrefix("game-add-message", "&aSuccessful added %type% &ato %id% &a!").replace("%type%", "" + playSpace.getType()).replace("%id%", "" + lobby.getID()));
                        return true;
                    }
                    return false;
                }

                if (args[1].equalsIgnoreCase("add-game-id")) {
                    if (args.length >= 2) {
                        int lobbyId = Integer.parseInt(args[1]);
                        int gameId = Integer.parseInt(args[2]);

                        Lobby lobby = Lobby.lobbies.stream()
                                .filter(lobby_ -> lobby_.getID() == lobbyId)
                                .findFirst()
                                .orElse(null);

                        PlaySpace playSpace = PlaySpace.playSpaces.stream()
                                .filter(space -> space.getID() == gameId)
                                .findFirst()
                                .orElse(null);

                        if (lobby == null) {
                            player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                            return false;
                        }

                        if (playSpace == null) {
                            player.sendMessage(Messages.messageWithPrefix("missing-playspace-message", "&cPlaySpace is not found!"));
                            return false;
                        }

                        lobby.addPlaySpace(playSpace);
                        playSpace.setLobby(lobby);
                        player.sendMessage(Messages.messageWithPrefix("game-add-message", "&aSuccessful added %type% &ato %id% &a!").replace("%type%", "" + playSpace.getType()).replace("%id%", "" + lobby.getID()));
                        return true;
                    }
                    return false;
                }

                if (args[0].equalsIgnoreCase("remove-game")) {
                    if (args.length >= 1) {
                        int id = Integer.parseInt(args[1]);
                        Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                                .filter(selector -> selector.player.equals(player))
                                .findFirst();
                        if (!lobbySelector.isPresent()) {
                            player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
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
                        lobby.removePlaySpace(playSpace);
                        playSpace.setLobby(null);
                        player.sendMessage(Messages.messageWithPrefix("game-remove-message", "&aSuccessful removed %type% &afrom %id% &a!").replace("%type%", "" + playSpace.getType()).replace("%id%", "" + lobby.getID()));
                        return true;
                    }
                    return false;
                }

                if (args[0].equalsIgnoreCase("remove-game-id")) {
                    if (args.length >= 2) {
                        int lobbyId = Integer.parseInt(args[1]);
                        int gameId = Integer.parseInt(args[2]);

                        Lobby lobby = Lobby.lobbies.stream()
                                .filter(lobby_ -> lobby_.getID() == lobbyId)
                                .findFirst()
                                .orElse(null);

                        PlaySpace playSpace = PlaySpace.playSpaces.stream()
                                .filter(space -> space.getID() == gameId)
                                .findFirst()
                                .orElse(null);

                        if (lobby == null) {
                            player.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                            return false;
                        }

                        if (playSpace == null) {
                            player.sendMessage(Messages.messageWithPrefix("missing-playspace-message", "&cPlaySpace is not found!"));
                            return false;
                        }

                        lobby.removePlaySpace(playSpace);
                        playSpace.setLobby(null);
                        player.sendMessage(Messages.messageWithPrefix("game-remove-message", "&aSuccessful removed %type% &afrom %id% &a!").replace("%type%", "" + playSpace.getType()).replace("%id%", "" + lobby.getID()));
                        return true;
                    }
                    return false;
                }
            }

            return false;
        }

        if (args[0].equalsIgnoreCase("remove-id")) {
            if (args.length >= 1) {
                int id = Integer.parseInt(args[1]);
                Lobby lobby = Lobby.lobbies.stream()
                        .filter(lobby_ -> lobby_.getID() == id)
                        .findFirst()
                        .orElse(null);
                if (lobby == null) {
                    sender.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                    return false;
                }
                Lobby.lobbies.remove(lobby);
                sender.sendMessage(Messages.messageWithPrefix("looby-removed-message", "&aSuccessful removed Lobby %lobby%&a!").replace("%lobby%", "" + lobby.getID()));
                return true;
            }
            return false;
        }

        if (args[0].equalsIgnoreCase("kick-player")) {
            if (args.length >= 2) {
                String name = args[1];
                Player player1 = Bukkit.getPlayer(name);
                Lobby lobby = GeneralUtils.isPlayerInGame(player1);
                if (lobby == null) {
                    sender.sendMessage(Messages.getMessage("error-not-lobby-player", "&&cPlayer is not in a lobby!"));
                    return false;
                }
                lobby.kickPlayer(player1);
                sender.sendMessage(Messages.messageWithPrefix("lobby-kick-player", "&aSuccessful kicked %player% from %lobby%&a!").replace("%lobby%", ""+ lobby.getID()).replace("%player%", ""+ player1.getName()));
                return true;
            }
            return false;
        }

        if (args[0].equalsIgnoreCase("kick-all")) {
            if (args.length >= 2) {
                int id = Integer.parseInt(args[1]);
                Lobby lobby = Lobby.lobbies.stream()
                        .filter(lobby_ -> lobby_.getID() == id)
                        .findFirst()
                        .orElse(null);
                if (lobby == null) {
                    sender.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                    return false;
                }
                lobby.kickAll();
                sender.sendMessage(Messages.messageWithPrefix("lobby-kick-all", "&aSuccessful kicked all players from %lobby%&a!").replace("%lobby%", ""+ lobby.getID()));
                return true;
            }

            Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
                    .filter(selector -> selector.player.equals(sender))
                    .findFirst();
            if (!lobbySelector.isPresent()) {
                sender.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                return false;
            }
            Lobby lobby = lobbySelector.get().lobby;
            lobby.kickAll();
            sender.sendMessage(Messages.messageWithPrefix("lobby-kick-all", "&aSuccessful kicked all players from %lobby%&a!").replace("%lobby%", ""+ lobby.getID()));
            return true;
        }

        if (args[0].equalsIgnoreCase("force-end-id")) {
            if (args.length >= 1) {
                int id = Integer.parseInt(args[1]);
                Lobby lobby = Lobby.lobbies.stream()
                        .filter(lobby_ -> lobby_.getID() == id)
                        .findFirst()
                        .orElse(null);
                if (lobby == null) {
                    sender.sendMessage(Messages.messageWithPrefix("looby-missing-message", "&cLobby is not found!"));
                    return false;
                }
                if (lobby.getCurrentPlaySpace() == null){
                    sender.sendMessage(Messages.messageWithPrefix("lobby-not-active", "&cLobby is not active!"));
                    return false;
                }
                if (lobby.getCurrentPlaySpace().getStatus() == GameStatus.INACTIVE){
                    sender.sendMessage(Messages.messageWithPrefix("lobby-not-active", "&cLobby is not active!"));
                    return false;
                }
                lobby.getCurrentPlaySpace().end();
                sender.sendMessage(Messages.messageWithPrefix("lobby-force-end", "&aSuccessful force ended %id%&a!").replace("%id%", "" + lobby.getID()));
                return true;
            }
            return false;
        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command"));
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList(
                    "join",
                    "create",
                    "set",
                    "remove",
                    "remove-id",
                    "add-game",
                    "add-game-id",
                    "remove-game",
                    "remove-game-id",
                    "kick-player",
                    "kick-all",
                    "force-end-id"
            );
        }
        if (args.length == 2 && args[0].equals("kick-player")) {
            List<String> all = new ArrayList<>();
            for(Player player : Bukkit.getOnlinePlayers()){
                all.add(player.getName());
            }
            return all;
        }

        if (args.length == 2 && args[0].equals("add-game")) {
            List<String> all = new ArrayList<>();
            for(Lobby lobby : Lobby.lobbies){
                all.add("" + lobby.getID());
            }
            return all;
        }

        if (args.length == 2 && args[0].equals("remove-game")) {
            List<String> all = new ArrayList<>();
            for(PlaySpace game: KOTHArena.playSpaces){
                all.add("" + game.getID());
            }
            return all;
        }

        if (args.length == 3 && args[0].equals("add-game")) {
            List<String> all = new ArrayList<>();
            for(PlaySpace game: KOTHArena.playSpaces){
                all.add("" + game.getID());
            }
            return all;
        }

        if (args.length == 2 && args[0].equals("force-end-id")) {
            List<String> all = new ArrayList<>();
            for(Lobby lobby : Lobby.lobbies){
                all.add("" + lobby.getID());
            }
            return all;
        }

        if (args.length == 2 && args[0].equals("set")){
            return Arrays.asList("location", "min-players", "max-players", "mode");
        }

        if (args.length == 2 && args[0].equals("join")){
            List<String> all = new ArrayList<>();
            for(Lobby lobby : Lobby.lobbies){
                all.add("" + lobby.getID());
            }
            return all;
        }

        if (args.length == 3 && args[0].equals("set") && args[1].equals("mode")){
            return Collections.singletonList("AUTO");
        }

        return Collections.emptyList();
    }
}
