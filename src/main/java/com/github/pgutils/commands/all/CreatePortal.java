package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.hooks.PGLobbyHook;
import com.github.pgutils.utils.DatabaseManager;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.PortalManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreatePortal extends PGSubCommand {
    DatabaseManager sqlDB;
    public CreatePortal(){
        sqlDB = PGUtils.getPlugin(PGUtils.class).sqlDB;
    }
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
                             return this.hookPortal(player, portalID, lobbyID);
                        }
                        break;
                    case "delete":
                        if(args.length > 1) {
                            int portalID = Integer.parseInt(args[1]);
                            return this.removePortal(portalID, player);
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
        return PGUtils.getPlugin(PGUtils.class).PM.removePortal(portalID, player);
    }

    private boolean hookPortal(Player player, int portalID, int lobbyID) {
        sqlDB.connect();
        if (sqlDB.tableExists(sqlDB.fixName("portals"))) {
            String updateQuery = "UPDATE " + sqlDB.fixName("portals") + " SET lobbyID=? WHERE id=?";
            if (sqlDB.execute(updateQuery, lobbyID, portalID)) {
                player.sendMessage(Messages.messageWithPrefix("save-portal-lobby-message", "&aSuccessfully hook Lobby to this portal."));
                return true;
            }
            player.sendMessage(Messages.messageWithPrefix("error-portal-lobby-message", "&cUnable to hook Lobby in this portal!!"));
        }
        return true;
    }

    private boolean createPortal(Location pos1, Location pos2, Location respawn, String name) {
        return PGUtils.getPlugin(PGUtils.class).PM.savePortalLocations(name, pos1, pos2, respawn);
    }

    private boolean createPortalWithHook(Location pos1, Location pos2, Location respawn, String name, int loobyID) {
        return PGUtils.getPlugin(PGUtils.class).PM.savePortalLocations(name, pos1, pos2, respawn, loobyID);
    }

    private List<String> getPortalsID(){
        List<String> tabComplite = new ArrayList<>();
        sqlDB.connect();
        if (sqlDB.tableExists(sqlDB.fixName("portals"))) {
            String selectAllQuery = "SELECT id FROM " + sqlDB.fixName("portals");
            List<Object[]> results = sqlDB.executeQuery(selectAllQuery);

            if (results != null) {
                for (Object[] data : results) {
                    tabComplite.add(String.valueOf(data[0]));
                }
            }
        }
        return tabComplite;
    }

    private List<String> getLobbyID(){
        List<String> tabComplite = new ArrayList<>();
        for(Lobby lobby : Lobby.lobbies){
            tabComplite.add(lobby.getID() + "");
        }

        if(tabComplite.size() == 0){
            return Collections.singletonList(Messages.getMessage("lobby-missing-message", "&cLobby is not found!", true));
        }

        return tabComplite;
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
