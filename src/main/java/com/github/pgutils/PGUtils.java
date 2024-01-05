package com.github.pgutils;

import com.github.pgutils.commands.PGCommand;
import com.github.pgutils.commands.PGTabComplete;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class PGUtils extends JavaPlugin {

    public Logger logger = Bukkit.getLogger();
    public String prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        prefix = getConfig().getString("prefix", "&7[&e&lPGUtils&7] ");

        if(!new File(getDataFolder(), "database").exists())
            new File(getDataFolder(), "database").mkdir();


        getCommand("pgutils").setExecutor(new PGCommand());
        getCommand("pgutils").setTabCompleter(new PGTabComplete());

    }

    @Override
    public void onDisable() {}
}
