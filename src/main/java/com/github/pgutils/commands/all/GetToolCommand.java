package com.github.pgutils.commands.all;

import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.PortalManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GetToolCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "tool";
    }

    @Override
    public String getDescription() {
        return "Get Portal Create Tool!";
    }

    @Override
    public String getPermission() {
        return "pgutils.portal.tool";
    }

    @Override
    public String getUsage() {
        return "/pg tool";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.getInventory().setItem(player.getInventory().firstEmpty(), PortalManager.getTool());
            sender.sendMessage(Messages.messageWithPrefix("tool-message", "&eYour retrieve PGUtils Tool!"));
            return true;
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
