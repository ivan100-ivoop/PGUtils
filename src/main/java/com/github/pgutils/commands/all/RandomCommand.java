package com.github.pgutils.commands.all;

import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.UltimateUtilsX;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.pgutils.customitems.CustomItemRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RandomCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "random";
    }

    @Override
    public String getDescription() {
        return "Random Command";
    }

    @Override
    public String getPermission() {
        return "pgutils.random";
    }

    @Override
    public String getUsage() {
        return "/pg random [<args>]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
           return false;
        }
        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "item":
                return UltimateUtilsX.getItem(player, args);
        }

        return true;

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList(
                    "item"

            );
        }
        // if its item then return the list of items
        if (args[0].equalsIgnoreCase("item")) {
            // add items in a list
            List<String> list = new ArrayList<>(CustomItemRepository.custom_item_name.keySet());
            return list;

        }
        return null;
    }
}
