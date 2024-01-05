package com.github.pgutils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabComplite implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String command, String[] args){
		ArrayList<String> tab = new ArrayList<String>();
		
		if(command.equalsIgnoreCase("pg")) {
			if(args.length < 1) {
					tab.add("reload");
					tab.add("setlobby");
					tab.add("tool");
			}	
		}
		return tab;
	}

}
