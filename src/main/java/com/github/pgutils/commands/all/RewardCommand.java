package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.interfaces.PGSubCommand;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.RewardManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RewardCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "reward";
    }

    @Override
    public String getDescription() {
        return "Manage your rewards!";
    }

    @Override
    public String getPermission() {
        return "pgutils.reward";
    }

    @Override
    public String getUsage() {
        return "/pg reward <add/remove/list> <command/item> ";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        RewardManager rewardManager = new RewardManager();

        if(args.length < 1){
            sender.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return false;
        }

        if(sender instanceof Player) {
            Player player = (Player) sender;

            switch (args[0]){
                case "add":
                    switch (args[1]){
                        case "command":
                            rewardManager.addCommandReward(args,1);
                            player.sendMessage(Messages.messageWithPrefix("rewards-success-add-message", "&aSuccessfully added a new reward!"));
                            break;
                        case "item":
                            rewardManager.addItemReward(player.getItemInHand());
                            player.sendMessage(Messages.messageWithPrefix("rewards-success-add-message", "&aSuccessfully added a new reward!"));
                            break;
                        default:
                            player.sendMessage(Messages.messageWithPrefix("rewards-error-message", "&cYou need to include a command or item for the reward!"));
                            break;
                    }
                    break;
                case "remove":
                    System.out.println(args.length);
                    if(args.length == 2){
                        rewardManager.removeItem(args[1]);
                        player.sendMessage(Messages.messageWithPrefix("rewards-success-remove-message", "&aSuccessfully removed reward!"));
                    } else {
                        sender.sendMessage(Messages.messageWithPrefix("invalid-command-message", "&4Invalid command."));
                    }
                    break;
                case "list":
                    rewardManager.getList(player);
                    break;
                case "get":
                    rewardManager.giveRewards(player);
                    break;
                default:
                    player.sendMessage(Messages.messageWithPrefix("invalid-command-message", "&4Invalid command."));
                    break;
            }
            return true;
        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command"));
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list");
        }

        if (args.length == 2 && args[0].equals("add")){
            return Arrays.asList("command", "item");
        }

        return Collections.emptyList();
    }
}
