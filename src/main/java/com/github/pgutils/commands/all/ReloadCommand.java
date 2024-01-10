package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.Messages;
import jdk.tools.jlink.internal.Platform;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloading Plugin!";
    }

    @Override
    public String getPermission() {
        return "pgutils.reload";
    }

    @Override
    public String getUsage() {
        return "/pg reload";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        PGUtils.getPlugin(PGUtils.class).reloadConfig();
        if(sender instanceof Player){
            Player player = (Player) sender;
            player.sendMessage(Messages.messageWithPrefix("reload-message", "&aSuccessful reload!"));
        } else {
            sender.sendMessage(Messages.getMessage("reload-message", "&aSuccessful reload!"));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
