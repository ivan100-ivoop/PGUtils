package com.github.pgutils.commands.all;

import com.github.pgutils.interfaces.PGSubCommand;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PlayerChestReward;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ChestCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "chest";
    }

    @Override
    public String getDescription() {
        return "Open Your Game Chest!";
    }

    @Override
    public String getPermission() {
        return "pgutils.chest";
    }

    @Override
    public String getUsage() {
        return "/pg chest";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            player.openInventory(PlayerChestReward.getPlayerChest(player));
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
