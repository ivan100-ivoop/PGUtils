package com.github.pgutils.commands;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.PGUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PGCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
        if(cmd.getName().equalsIgnoreCase("pgutils")) {
            if(args.length > 1) {
                if(args[0] == "reload") {
                    PGUtils.getPlugin(PGUtils.class).reloadConfig();
                    sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).getConfig().getString("reload-message", "&aSuccesval reload!")));
                }

                if(args[0] == "tool") {
                    if(sender instanceof Player) {
                        ((Player) sender).getInventory().addItem(getTool());
                    }
                }
                //if(args[1] == "setlobby") {
                //}
            }
            return true;
        }
        return false;
    }

    private ItemStack getTool() {
        ItemStack tool = new ItemStack(Material.STICK);
        ItemMeta meta = tool.getItemMeta();
        meta.setCustomModelData(Integer.parseInt("6381260"));
        meta.setDisplayName(GeneralUtils.fixColors("&5&lPGUtils &e&lTool"));
        tool.setItemMeta(meta);

        return tool;
    }

}
