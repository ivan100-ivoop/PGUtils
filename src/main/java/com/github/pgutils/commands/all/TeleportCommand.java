package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Arrays;

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
        if(sender instanceof Player) {
            Player player = (Player) sender;
                if(args.length >= 1 && args[0].equalsIgnoreCase("lobby")){
                    Lobby selectedLobby = Lobby.lobbies.get(Integer.parseInt(args[2]));
                    if(selectedLobby == null){
                        player.sendMessage(Messages.messageWithPrefix("missing-lobby-message", "&cLobby is not found!"));
                        return true;
                    }

                    player.teleport(selectedLobby.getPos());
                    player.sendMessage(Messages.messageWithPrefix("tp-lobby-message", "&aTeleported to Lobby Location!"));
                    return true;

                } else if(args.length >= 1 && args[0].equalsIgnoreCase("portal")){
                    if(PGUtils.getPlugin(PGUtils.class).getPortalManager().teleportToPortal(player, "join"))
                        player.sendMessage(Messages.messageWithPrefix("tp-portal-message", "&aTeleported to Portal Location!"));
                    return true;
                }
        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command"));
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("portal", "lobby");
    }
}
