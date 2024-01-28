package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        RewardManager rewards = PGUtils.getPlugin(PGUtils.class).loader.rewardManager;

        if (args.length < 1) {
            return false;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length >= 2){
                int lobbyID = Integer.parseInt(args[0]);
                switch (args[1]){
                    case "add":
                        if(!addCommand(player, args, rewards, lobbyID)){
                            return false;
                        }
                        break;
                    case "list":
                        player.sendMessage(rewards.getList(lobbyID).toString());
                        break;
                    case "give":
                        if(!giveToPlayer(args, rewards, lobbyID)){
                            return false;
                        }
                        player.sendMessage(Messages.messageWithPrefix("rewards-success-give-message", "&aSuccessfully give reward!"));
                        break;
                    case "remove":
                        if(!removeCommand(player, args, rewards, lobbyID)){
                            return false;
                        }
                        break;
                    default:
                        player.sendMessage(Messages.messageWithPrefix("invalid-command-message", "&4Invalid command."));
                        break;
                }
            }
            return true;
        }

        sender.sendMessage(Messages.getMessage("error-not-player", "&cYou must be a player to execute this command", true));
        return false;
    }

    private boolean giveToPlayer(String[] args, RewardManager rewards, int lobbyID){
        Player p = Bukkit.getPlayer(args[2]);
        if(p == null){
            return false;
        }
        rewards.giveRewards(lobbyID, p);
        return true;
    }
    private boolean addCommand(Player player, String[] args, RewardManager rewards, int lobbyID){
        switch(args[2]){
            case "command":
                String cmd = parseCommand(Arrays.copyOfRange(args, 3, args.length));
                if(!rewards.addCommandReward(lobbyID, cmd)){
                    return false;
                }
                player.sendMessage(Messages.messageWithPrefix("rewards-success-add-message", "&aSuccessfully added a new reward!"));
                break;
            case "item":
                if(!rewards.addItemReward(lobbyID, player.getItemInHand())){
                    return false;
                }
                player.sendMessage(Messages.messageWithPrefix("rewards-success-add-message", "&aSuccessfully added a new reward!"));
                break;
            default:
                player.sendMessage(Messages.messageWithPrefix("rewards-error-message", "&cYou need to include a command or item for the reward!"));
                break;
        }
        return true;
    }
    private String parseCommand(String[] cmds) {
        StringBuilder output = new StringBuilder();
        for(String cmd : cmds){
            output.append(cmd).append(" ");
        }
        return output.toString().trim();
    }

    private boolean removeCommand(Player player, String[] args, RewardManager rewards, int lobbyID){
        if(args.length == 3){
            if(!rewards.removeItem(lobbyID, Integer.parseInt(args[2]))){
                return false;
            }
            player.sendMessage(Messages.messageWithPrefix("rewards-success-remove-message", "&aSuccessfully removed reward!"));
        } else {
            player.sendMessage(Messages.messageWithPrefix("invalid-command-message", "&4Invalid command."));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 1) {
            List<String> tabComplite = new ArrayList<>();
            for(Lobby lobby : Lobby.lobbies){
                tabComplite.add(lobby.getID() + "");
            }

            if(tabComplite.size() == 0){
                return Collections.singletonList(Messages.getMessage("lobby-missing-message", "&cLobby is not found!", true));
            }

            return tabComplite;
        }

        if (args.length == 2) {
            return Arrays.asList("add", "remove", "list", "give");
        }

        if (args.length == 3 && args[1].equals("add")) {
            return Arrays.asList("command", "item");
        }

        if (args.length == 3 && args[1].equals("remove")) {
            return PGUtils.getPlugin(PGUtils.class).loader.rewardManager.getRewards(Integer.parseInt(args[0]));
        }

        if (args.length == 3 && args[1].equals("give")) {
            List<String> tabComplite = new ArrayList<>();
            for(Player player : Bukkit.getOnlinePlayers()){
                tabComplite.add(player.getName());
            }
            return tabComplite;
        }

        return Collections.emptyList();
    }
}
