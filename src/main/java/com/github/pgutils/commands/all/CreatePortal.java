package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.hooks.PGLobbyHook;
import com.github.pgutils.interfaces.PGSubCommand;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PortalManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CreatePortal extends PGSubCommand {
    @Override
    public String getName() {
        return "createPortal";
    }

    @Override
    public String getDescription() {
        return "Create portal must be selected first and last position with PGUtils Tool!";
    }

    @Override
    public String getPermission() {
        return "pgutils.portal";
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            if (PGLobbyHook.pos1 == null) {
                player.sendMessage(Messages.messageWithPrefix("portal-missing-pos1", "&cYou have not selected &bposition1&e!"));
                return true;
            }
            if (PGLobbyHook.pos2 == null) {
                player.sendMessage(Messages.messageWithPrefix("portal-missing-pos2", "&cYou have not selected &bposition1&e!"));
                return true;
            }

            if (PGUtils.getPlugin(PGUtils.class).getPortalManager().savePortalLocations("join", PGLobbyHook.pos1, PGLobbyHook.pos2, player.getLocation())) {

                if (player.getInventory().contains(PortalManager.getTool())){
                    player.getInventory().remove(PortalManager.getTool());
                }

                player.sendMessage(Messages.messageWithPrefix("save-portal-message", "&aSuccessfully saved Portal Locations."));
            }
            return true;
        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command"));
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
