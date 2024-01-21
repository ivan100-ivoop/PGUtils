package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PGSubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeleportCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "tp";
    }

    @Override
    public String getDescription() {
        return "Teleport to Portal or Lobby!";
    }

    @Override
    public String getPermission() {
        return "pgutils.tp";
    }

    @Override
    public String getUsage() {
        return "/pg tp <portal/lobby> <name>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 1 && args[0].equalsIgnoreCase("lobby")) {
                int lobbyID = Integer.parseInt(args[1]);
                Lobby selectedLobby = Lobby.lobbies.get(lobbyID - 1);
                if (selectedLobby == null) {
                    player.sendMessage(Messages.messageWithPrefix("missing-lobby-message", "&cLobby is not found!"));
                    return true;
                }

                player.teleport(selectedLobby.getLocation());
                player.sendMessage(Messages.messageWithPrefix("tp-lobby-message", "&aTeleported to Lobby Location!"));
                return true;

            } else if (args.length >= 1 && args[0].equalsIgnoreCase("game")) {
                int playSpaceID = Integer.parseInt(args[1]);
                PlaySpace selectedPlaySpace= PlaySpace.playSpaces.get(playSpaceID - 1);
                if (selectedPlaySpace == null) {
                    player.sendMessage(Messages.messageWithPrefix("playSpace-missing-message", "&cPlaySpace is not found!"));
                    return true;
                }

                player.teleport(selectedPlaySpace.getLocation());
                player.sendMessage(Messages.messageWithPrefix("tp-playspace-message", "&aTeleported to PlaySpace Location!"));
                return true;

            } else if (args.length >= 1 && args[0].equalsIgnoreCase("portal")) {
                if (PGUtils.getPlugin(PGUtils.class).PM.teleportToPortal(player, "join"))
                    player.sendMessage(Messages.messageWithPrefix("tp-portal-message", "&aTeleported to Portal Location!"));
                return true;
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("leave")) {
                Location loc = GeneralUtils.getRespawnPoint();
                if(loc != null) {
                    player.teleport(loc);
                    player.sendMessage(Messages.messageWithPrefix("tp-leave-message", "&aTeleported to Leave Location!"));
                } else {
                    player.sendMessage(Messages.messageWithPrefix("tp-leave-error-message", "&aLeave Location not found!"));
                }
                return true;
            }
        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command", true));
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length >= 0 && args[0].equalsIgnoreCase("lobby")) {
            List<String> tabComplite = new ArrayList<>();
            for(Lobby lobby : Lobby.lobbies){
                tabComplite.add(lobby.getID() + "");
            }

            if(tabComplite.size() == 0){
                return Collections.singletonList(Messages.getMessage("lobby-missing-message", "&cLobby is not found!", true));
            }

            return tabComplite;
        }

        if (args.length >= 0 && args[0].equalsIgnoreCase("game")) {
            List<String> tabComplite = new ArrayList<>();
            for(PlaySpace playSpace : PlaySpace.playSpaces){
                tabComplite.add(playSpace.getID() + "");
            }

            if(tabComplite.size() == 0){
                return Collections.singletonList(Messages.getMessage("playSpace-missing-message", "&cPlaySpace is not found!", true));
            }

            return tabComplite;
        }

        return Arrays.asList("portal", "leave", "lobby", "game");
    }
}
