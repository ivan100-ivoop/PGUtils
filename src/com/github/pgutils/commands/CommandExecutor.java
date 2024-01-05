package com.github.pgutils.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.pgutils.Launcher;
import com.github.pgutils.Utils;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if(command.equalsIgnoreCase("pg")) {
			if(args.length >= 1) {
				if(args[1] == "reload") {
					Launcher.getPlugin(Launcher.class).reloadConfig();
					sender.sendMessage(Utils.fixColors(Launcher.getPlugin(Launcher.class).getConfig().getString("reload-message", "&aSuccesval reload!")));
				}
				
				if(args[1] == "tool") {
					
					
				}
				if(args[1] == "setlobby") {
				}
			}
			return true;
		}
		return false;
	}
	
	private ItemStack getTool() {
		ItemStack tool = new ItemStack(Material.STICK);
		ItemMeta meta = tool.getItemMeta();
		meta.setCustomModelData(Integer.parseInt("6381260"));
		meta.setDisplayName(Utils.fixColors("&5&l"));
		
		return tool;
	}

}
