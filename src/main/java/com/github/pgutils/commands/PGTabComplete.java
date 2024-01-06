package com.github.pgutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class PGTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String command, String[] args){
        ArrayList<String> tab = new ArrayList<String>();

        if(cmd.getName().equalsIgnoreCase("pgutils")) {
            if(args.length >= 0) {
                tab.add("reload");
                tab.add("setlobby");
                tab.add("tool");
                tab.add("setportal");
            }
        }
        return tab;
    }
}
