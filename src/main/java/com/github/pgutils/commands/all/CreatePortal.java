package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.service.PortalService;
import com.github.pgutils.hooks.PGLobbyHook;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.PortalManager;
import com.github.pgutils.utils.db.PortalSave;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CreatePortal extends PGSubCommand {

    @Override
    public String getName() {
        return "portal";
    }

    @Override
    public String getDescription() {
        return "Create portal must be selected first and last position with PGUtils Tool!";
    }

    @Override
    public String getPermission() {
        return "pgutils.portal.admin";
    }

    @Override
    public String getUsage() {
        return "/pg " + getName() + " <create, hook, remove>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length >= 1){
                switch (args[0]){
                    case "create":
                        if (PGLobbyHook.pos1 == null) {
                            player.sendMessage(Messages.messageWithPrefix("portal-missing-pos1", "&cYou have not selected &bposition1&e!"));
                            return true;
                        }

                        if (PGLobbyHook.pos2 == null) {
                            player.sendMessage(Messages.messageWithPrefix("portal-missing-pos2", "&cYou have not selected &bposition2&e!"));
                            return true;
                        }

                        if(args.length == 2){
                            if(this.createPortal(PGLobbyHook.pos1, PGLobbyHook.pos2, player.getLocation(), args[1])){

                                if (player.getInventory().contains(PortalManager.getTool())) {
                                    player.getInventory().remove(PortalManager.getTool());
                                }

                                player.sendMessage(Messages.messageWithPrefix("save-portal-message", "&aSuccessfully saved Portal Locations."));
                                return true;
                            }
                        } else if(args.length == 3){
                            if(this.createPortalWithHook(PGLobbyHook.pos1, PGLobbyHook.pos2, player.getLocation(), args[1], Integer.parseInt(args[2]))){

                                if (player.getInventory().contains(PortalManager.getTool())) {
                                    player.getInventory().remove(PortalManager.getTool());
                                }

                                player.sendMessage(Messages.messageWithPrefix("save-lobby-portal-message", "&aSuccessfully saved Portal with hooked Lobby."));
                                return true;
                            }
                        }
                        break;
                    case "hook":
                        if(args.length > 1) {
                             int portalID = Integer.parseInt(args[1]);
                             int lobbyID = Integer.parseInt(args[2]);
                             return this.hookPortal((portalID - 1), (lobbyID - 1), player);
                        }
                        break;
                    case "delete":
                        if(args.length > 1) {
                            int portalID = Integer.parseInt(args[1]);
                            return this.removePortal((portalID - 1), player);
                        }
                        break;
                    default:
                        return false;
                }
            }

            return false;

        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command", true));
        return false;
    }

    private boolean removePortal(int portalID, Player player) {

        if( PortalManager.portals.get(portalID) == null){
            player.sendMessage(Messages.messageWithPrefix("remove-portal-error-message", "&cPortal %id% remove unsuccessful!").replace("%id%",  String.valueOf((portalID + 1))));
            return true;
        }

        PortalSave portal = PortalManager.portals.get(portalID);

        PortalManager.portals.remove(portal);
        PortalService.deletePortal(portal);
        player.sendMessage(Messages.messageWithPrefix("remove-portal-message", "&aPortal %id% removed successful!").replace("%id%", String.valueOf((portalID + 1))));
        return true;
    }

    private boolean hookPortal(int portalID, int lobbyID, Player player) {

        if( PortalManager.portals.get(portalID) == null){
            player.sendMessage(Messages.messageWithPrefix("error-portal-lobby-message", "&cUnable to hook Lobby in this portal!!"));
            return true;
        }

        if(Lobby.lobbies.get(lobbyID) == null){
            player.sendMessage(Messages.messageWithPrefix("missing-lobby-message", "&cLobby is not found!"));
            return true;
        }

        PortalSave portal = PortalManager.portals.get(portalID);
        Lobby lobby = Lobby.lobbies.get(lobbyID);

        if (portal.getLobbyID() != null && !portal.getLobbyID().isEmpty()){
            player.sendMessage(Messages.messageWithPrefix("error-portal-lobby-hook-message", "&cUnable to hook Lobby in this portal already hooked!!"));
            return true;
        }

        portal.setLobbyID(lobby.getUID());
        PortalService.updatePortal(portal);
        player.sendMessage(Messages.messageWithPrefix("save-portal-lobby-message", "&aSuccessfully hook Lobby to this portal."));
        return true;
    }

    private boolean createPortal(Location pos1, Location pos2, Location respawn, String name) {
        PortalSave portal = new PortalSave();
        portal.setName(name);
        portal.setLocation1(pos1);
        portal.setLocation2(pos2);
        portal.setRespawn(respawn);
        portal.setLobbyID("");
        PortalManager.portals.add(portal);
        PortalService.savePortal(portal);
        return true;
    }

    private boolean createPortalWithHook(Location pos1, Location pos2, Location respawn, String name, int lobbyID) {

        if(Lobby.lobbies.get(lobbyID) == null){
            return false;
        }

        Lobby lobby = Lobby.lobbies.get(lobbyID);

        PortalSave portal = new PortalSave();
        portal.setName(name);
        portal.setLocation1(pos1);
        portal.setLocation2(pos2);
        portal.setRespawn(respawn);
        portal.setLobbyID(lobby.getUID());

        PortalManager.portals.add(portal);
        PortalService.savePortal(portal);
        return true;
    }

    private List<String> getPortalsID(){
        List<String> tabComplete = new ArrayList<>();
        for (int i = 0; i<PortalManager.portals.size(); i++) {
            tabComplete.add(String.valueOf(( i + 1)));
        }
        return tabComplete;
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

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if(args.length == 2 && args[0].equalsIgnoreCase("delete")){
           return this.getPortalsID();
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("hook")){
            return this.getPortalsID();
        }

        if(args.length == 3 && (args[0].equalsIgnoreCase("hook") || args[0].equalsIgnoreCase("create"))){
            return this.getLobbyID();
        }

        if(args.length == 1){
            return Arrays.asList("create", "hook", "delete");
        }

        return Collections.emptyList();
    }
}
