package com.github.pgutils.commands;

import com.github.pgutils.entities.Lobby;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class PGTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String command, String[] args){
        ArrayList<String> tab = new ArrayList<String>();

        if(cmd.getName().equalsIgnoreCase("pg")) {

            if(args.length == 1) {
                tab.add("reload");
                tab.add("lobby");
                tab.add("setportal");
                tab.add("setleave");
                tab.add("tool");
                tab.add("tp");
                tab.add("leave");
                tab.add("select");
                tab.add("game");
                tab.add("chest");
            }

            if(args.length > 1 && args[0].equalsIgnoreCase("tp")){
                tab.add("lobby");
                tab.add("portal");
            }

            if(args.length > 1 && args[0].equalsIgnoreCase("game")){
                tab.add("koth");
            }

            if(args.length > 1 && args[0].equalsIgnoreCase("lobby")){
                tab.add("create");
                tab.add("add-game");
                tab.add("join");
            }


        }
        return tab;
    }
}
