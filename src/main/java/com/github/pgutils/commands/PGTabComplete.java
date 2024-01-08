package com.github.pgutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class PGTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> tab = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("pg")) {
            if (args.length == 1) {
                // Main commands
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
            } else if (args[0].equalsIgnoreCase("tp")) {
                if (args.length == 2) {
                    tab.add("lobby");
                    tab.add("portal");
                }
            } else if (args[0].equalsIgnoreCase("game")) {
                if (args.length == 2) {
                    tab.add("koth");
                    tab.add("select");
                    tab.add("delete");
                }
                if (args.length == 3 && args[1].equalsIgnoreCase("koth")) {
                    tab.add("create");
                    tab.add("set");
                    tab.add("arena");
                    tab.add("spawn");
                }
            } else if (args[0].equalsIgnoreCase("lobby")) {
                if (args.length == 2) {
                    tab.add("create");
                    tab.add("remove");
                    tab.add("remove-id");
                    tab.add("join");
                    tab.add("set");
                    tab.add("add-game");
                    tab.add("add-game-id");
                    tab.add("remove-game");
                    tab.add("remove-game-id");
                    tab.add("kick-player");
                    tab.add("kick-all");
                }
                if (args.length == 3 && args[1].equalsIgnoreCase("set")) {
                    tab.add("location");
                    tab.add("min-players");
                    tab.add("max-players");
                    tab.add("mode");
                }
            }
        }

        return tab;
    }
}