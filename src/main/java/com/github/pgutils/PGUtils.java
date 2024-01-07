package com.github.pgutils;

import com.github.pgutils.commands.PGCommand;
import com.github.pgutils.commands.PGTabComplete;
import com.github.pgutils.hooks.PGLobbyHook;

import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class PGUtils extends JavaPlugin {
    public Logger logger = Bukkit.getLogger();
    public String prefix;
    public static File database = null, saveInv = null;
    public static PortalManager PM = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        prefix = getConfig().getString("prefix", "&7[&e&lPGUtils&7] ");

        database = new File(getDataFolder(), "database");
        saveInv = new File(database, "saveInv");

        if (!database.exists()){
            database.mkdir();
        }

        if (!saveInv.exists()){
            saveInv.mkdir();
        }

        PM = new PortalManager();

        getCommand("pg").setExecutor(new PGCommand());
        getCommand("pg").setTabCompleter(new PGTabComplete());

        Bukkit.getPluginManager().registerEvents(new PGLobbyHook(), this);

    }

    @Override
    public void onDisable() {}
}
