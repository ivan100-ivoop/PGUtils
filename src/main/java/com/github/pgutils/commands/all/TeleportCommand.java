package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.utils.DatabaseManager;
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
    DatabaseManager sqlDB;
    public TeleportCommand(){
        sqlDB = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
    }
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

            } else if (args.length == 2 && args[0].equalsIgnoreCase("portal")) {
                return this.getPortalLocation(Integer.parseInt(args[1]), player);
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
        List<String> tabComplite = new ArrayList<>();
        for(Lobby lobby : Lobby.lobbies){
            tabComplite.add(lobby.getID() + "");
        }

        if(tabComplite.size() == 0){
            return Collections.singletonList(Messages.getMessage("lobby-missing-message", "&cLobby is not found!", true));
        }

        return tabComplite;
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

    private boolean getPortalLocation(int portalID, Player player){
        sqlDB.connect();
        if (sqlDB.tableExists(sqlDB.fixName("portals"))) {
            String selectAllQuery = "SELECT name, location_respawn_x, location_respawn_y, location_respawn_z, location_respawn_world FROM " + sqlDB.fixName("portals") + " WHERE id=?";
            List<Object[]> results = sqlDB.executeQuery(selectAllQuery, portalID);

            if (results != null && results.size() == 1) {
                Object[] data = results.get(0);
                String portalName = (String) data[0];
                double loc1X = (double) data[1];
                double loc1Y = (double) data[2];
                double loc1Z = (double) data[3];
                String worldName = (String) data[4];
                Location teleport = new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc1X, loc1Y, loc1Z);
                player.teleport(teleport);
                player.sendMessage(Messages.messageWithPrefix("tp-portal-message", "&aTeleported to Portal %name%!")
                        .replace("%name%", portalName));

                return true;
            } else {
                player.sendMessage(Messages.messageWithPrefix("missing-portal-message", "&cThe portal locations are not properly defined."));
                return true;
            }
        }
        return false;
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
