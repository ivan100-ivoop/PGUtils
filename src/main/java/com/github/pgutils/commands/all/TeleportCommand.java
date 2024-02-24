package com.github.pgutils.commands.all;

import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.utils.*;
import com.github.pgutils.utils.db.PortalSave;
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
        return "/pg tp <portal, leave, lobby, game> <id>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 1 && args[0].equalsIgnoreCase("lobby")) {
                int lobbyID = Integer.parseInt(args[1]);
                Lobby lobby = Lobby.lobbies.get(( lobbyID - 1));

                if (lobby == null){
                    player.sendMessage(Messages.messageWithPrefix("missing-lobby-message", "&cLobby is not found!"));
                    return true;
                }

                player.teleport(lobby.getLocation());
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

            } else if (args.length == 2 && args[0].equalsIgnoreCase("portal")) {
                return this.getPortalLocation((Integer.parseInt(args[1]) - 1), player);
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
            return false;
        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command", true));
        return false;
    }

    private List<String> getLobbyID(){
        List<String> tabComplete = new ArrayList<>();
        for (int i = 0; i<Lobby.lobbies.size(); i++) {
            tabComplete.add(String.valueOf(( i + 1)));
        }

        if(tabComplete.size() == 0){
            return Collections.singletonList(Messages.getMessage("lobby-missing-message", "&cLobby is not found!", true));
        }

        return tabComplete;
    }

    private List<String> getPortalsID(){
        List<String> tabComplete = new ArrayList<>();
        for (int i = 0; i< PortalManager.portals.size(); i++) {
            tabComplete.add(String.valueOf(( i + 1)));
        }
        return tabComplete;
    }

    private boolean getPortalLocation(int portalID, Player player){
        PortalSave portal = PortalManager.portals.get(portalID);

        if(portal == null) {
            player.sendMessage(Messages.messageWithPrefix("missing-portal-message", "&cThe portal locations are not properly defined."));
            return false;
        }

        player.teleport(portal.getRespawn());
        player.sendMessage(
                Messages.messageWithPrefix("tp-portal-message", "&aTeleported to Portal %name%!")
                        .replace("%name%", portal.getName())
        );
        return true;
    }

    private List<String> getPlaySpaceID(){
        List<String> tabComplite = new ArrayList<>();
        for(Lobby lobby : Lobby.lobbies){
            tabComplite.add(lobby.getID() + "");
        }

        if(tabComplite.size() == 0){
            return Collections.singletonList(Messages.getMessage("playSpace-missing-message", "&cPlaySpace is not found!", true));
        }

        return tabComplite;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("lobby")) {
            return this.getLobbyID();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("game")) {
            return this.getPlaySpaceID();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("portal")) {
            return this.getPortalsID();
        }

        if(args.length == 1){
            return Arrays.asList("portal", "leave", "lobby", "game");
        }

        return Collections.emptyList();
    }
}
