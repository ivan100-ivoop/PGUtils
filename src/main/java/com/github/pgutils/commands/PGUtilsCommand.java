package com.github.pgutils.commands;

import com.github.pgutils.commands.all.*;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class PGUtilsCommand implements CommandExecutor, TabCompleter {

    private final Map<String, PGSubCommand> subCommands = new HashMap<>();

    public PGUtilsCommand() {
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new GetToolCommand());
        registerSubCommand(new ChestCommand());
        registerSubCommand(new CreatePortal());
        registerSubCommand(new LeaveCommand());
        registerSubCommand(new TeleportCommand());
        registerSubCommand(new RewardCommand());
        registerSubCommand(new GameCommand());
        registerSubCommand(new LobbyCommand());
    }

    public void registerSubCommand(PGSubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && subCommands.containsKey(args[0].toLowerCase())) {
            PGSubCommand subCommand = subCommands.get(args[0].toLowerCase());

            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                command.setDescription(subCommand.getDescription());
                if(!subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length))){
                    sender.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));

                    if (sender instanceof Player) {
                        Player player = (Player) sender;

                        TextComponent main = new TextComponent("Usage: " + subCommand.getUsage() + "");
                        main.setColor(ChatColor.YELLOW);
                        main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(subCommand. getUsage()).create()));
                        main.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pg " + subCommand.getName() + " "));

                        player.spigot().sendMessage(main);
                    }
                }
            } else {
                sender.sendMessage(Messages.messageWithPrefix("command-noperms-message", "&4You do not have the necessary permissions to execute this command."));
            }
        } else {
            sender.sendMessage(Messages.messageWithPrefix("invalid-command-message", "&4Invalid command."));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String subCommand : subCommands.keySet()) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length > 1 && subCommands.containsKey(args[0].toLowerCase())) {
            PGSubCommand subCommand = subCommands.get(args[0].toLowerCase());
            completions.addAll(subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length)));
        }

        return completions;
    }
}