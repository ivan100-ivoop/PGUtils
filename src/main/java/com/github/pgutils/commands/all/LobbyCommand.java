package com.github.pgutils.commands.all;

import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.utils.LobbyMenu;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.UltimateUtilsX;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new LobbyMenu().prepareMenu().getLobby(player);
                return true;
            }
        }

        switch (args[0].toLowerCase()) {
            case "add-game-id":
                return UltimateUtilsX.addGameToLobbyID(sender, args);

            case "remove-game-id":
                return UltimateUtilsX.removeGameFromLobbyID(sender, args);

            case "delete-id":
                return UltimateUtilsX.removeLobbyID(sender, args);

            case "kick-player":
                return UltimateUtilsX.kickPlayerFromLobby(sender, args);

            case "kick-all":
                return UltimateUtilsX.kickAllFromLobbyID(sender,args);

            case "force-end-id":
                return UltimateUtilsX.forceEndCurrentLobbyGameID(sender,args);

            case "force-pull-player":
                return UltimateUtilsX.forcePullPlayerToLobby(sender,args);

            case "info-id":
                return UltimateUtilsX.lobbyInfoID(sender,args);

            case "info-games-id":
                return UltimateUtilsX.lobbyGamesInfoID(sender,args);
        }


        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("pgutils.lobby.admin")) {

                switch (args[0].toLowerCase()) {
                    case "create":
                        return UltimateUtilsX.createLobby(player);

                    case "delete":
                        return UltimateUtilsX.removeLobby(player);

                    case "join":
                        return UltimateUtilsX.joinLobby(player, args);

                    case "set":
                        return UltimateUtilsX.setLobby(player, args);

                    case "add-game":
                        return UltimateUtilsX.addGameToLobby(player, args);

                    case "remove-game":
                        return UltimateUtilsX.removeGameFromLobby(player, args);

                    case "select":
                        return UltimateUtilsX.selectLobby(player, args);

                    case "select-name":
                        return UltimateUtilsX.selectLobbyByName(player, args);

                    case "force-pull-world-id":
                        return UltimateUtilsX.forcePullAllInWorldToLobbyID(player,args);

                    case "info":
                        return UltimateUtilsX.lobbyInfo(player,args);

                    case "info-games":
                        return UltimateUtilsX.lobbyGamesInfo(player,args);

                    case "select-game-id":
                        return UltimateUtilsX.selectALobbyGameID(player,args);

                    case "start":
                        return UltimateUtilsX.startLobbyGame(player,args);
                }
            }
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList(
                    "join",
                    "create",
                    "select",
                    "set",
                    "delete",
                    "delete-id",
                    "add-game",
                    "add-game-id",
                    "remove-game",
                    "remove-game-id",
                    "kick-player",
                    "kick-all",
                    "force-end-id",
                    "force-pull-player",
                    "force-pull-world-id",
                    "info",
                    "info-id",
                    "info-games",
                    "info-games-id",
                    "start"
            );
        }
        if (args.length == 2 && args[0].equals("kick-player")) {
            List<String> all = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                all.add(player.getName());
            }
            return all;
        }

        if (args.length == 2 && args[0].equals("add-game")) {
            List<String> all = new ArrayList<>();
            for (Lobby lobby : Lobby.lobbies) {
                all.add("" + lobby.getID());
            }
            return all;
        }

        if (args.length == 2 && args[0].equals("remove-game")) {
            List<String> all = new ArrayList<>();
            for (PlaySpace game : KOTHArena.playSpaces) {
                all.add("" + game.getID());
            }
            return all;
        }

        if (args.length == 3 && args[0].equals("add-game")) {
            List<String> all = new ArrayList<>();
            for (PlaySpace game : KOTHArena.playSpaces) {
                all.add("" + game.getID());
            }
            return all;
        }

        if (args.length == 2 && args[0].equals("force-end-id")) {
            List<String> all = new ArrayList<>();
            for (Lobby lobby : Lobby.lobbies) {
                all.add("" + lobby.getID());
            }
            return all;
        }

        if (args.length == 2 && args[0].equals("set")) {
            return Arrays.asList("location", "min-players", "max-players", "mode", "name", "lock", "tournament", "testmode");
        }

        if (args.length == 2 && args[0].equals("join")) {
            List<String> all = new ArrayList<>();
            for (Lobby lobby : Lobby.lobbies) {
                all.add("" + lobby.getID());
            }
            return all;
        }

        if (args.length == 3 && args[0].equals("set") && args[1].equals("mode")) {
            return Arrays.asList("AUTO", "MANUAL");
        }

        return Collections.emptyList();
    }
}


