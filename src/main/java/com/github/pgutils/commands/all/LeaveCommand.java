package com.github.pgutils.commands.all;

import com.github.pgutils.entities.Lobby;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LeaveCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave Game!";
    }

    @Override
    public String getPermission() {
        return "pgutils.leave";
    }

    @Override
    public String getUsage() {
        return "/pg leave";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length >= 1 && args[0].equals("set")){
                if(player.hasPermission("pgutils.leave.set")){
                    GeneralUtils.setRespawnPoint(player.getLocation());
                    player.sendMessage(Messages.messageWithPrefix("respawn-set-message", "&aSuccessfully saved Respawn Location."));

                }
            } else {
                if (Lobby.lobbies.stream().anyMatch(lobby -> lobby.getPlayers().contains(player))) {
                    Lobby.lobbies.stream()
                            .filter(lobby -> lobby.getPlayers().contains(player))
                            .findFirst()
                            .get()
                            .removePlayer(player);
                } else {
                    player.sendMessage(Messages.messageWithPrefix("error-leave-message", "&eYou currently not in game or lobby!"));
                }
            }
            return true;
        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command"));
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.singletonList("set");
    }
}
