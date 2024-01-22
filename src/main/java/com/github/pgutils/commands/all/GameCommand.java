package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.UltimateUtilsX;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class GameCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "game";
    }

    @Override
    public String getDescription() {
        return "Game Settings!";
    }

    @Override
    public String getPermission() {
        return "pgutils.game";
    }

    @Override
    public String getUsage() {
        return "/pg game koth create [<args>]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return false;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            switch (args[0].toLowerCase()) {
                case "create":
                    return UltimateUtilsX.createGame(player, args);

                case "set":
                    return UltimateUtilsX.setGame(player, args);

                case "select":
                    return UltimateUtilsX.selectGame(player, args);

                case "select-name":
                    return UltimateUtilsX.selectGameByName(player, args);

                case "validate":
                    return UltimateUtilsX.checkValid(player, args);

                case "set-objects":
                    return UltimateUtilsX.setGameObjects(player, args);

                case "info":
                    return UltimateUtilsX.gameInfo(player, args);

                case "delete":
                    return UltimateUtilsX.deleteGame(player, args);


            }
        }
        return false;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("koth", "set", "select", "delete");
        }

        if (args.length == 2 && args[0].equals("koth")) {
            return Arrays.asList("create", "set");
        }

        if (args.length >= 3 && args[1].equals("create")) {
            return Arrays.asList("spawn", "point");
        }

        if (args.length == 2 && (args[0].equals("delete") || args[0].equals("select"))) {
            List<String> all = new ArrayList<>();
            for (Lobby lobby : Lobby.lobbies) {
                all.add("" + lobby.getID());
            }
            return all;
        }

        return Collections.emptyList();
    }
}
