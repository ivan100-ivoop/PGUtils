package com.github.pgutils;

import com.github.pgutils.commands.PGCommand;
import com.github.pgutils.commands.PGTabComplete;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.hooks.PGLobbyHook;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class PGUtils extends JavaPlugin {
    public Logger logger = Bukkit.getLogger();
    public String prefix;
    public static File database = null, saveInv = null;

    public static List<PlayerPlaySpaceSelector> selectedPlaySpace = new ArrayList<>();
    public static List<PlayerLobbySelector> selectedLobby = new ArrayList<>();


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

        getCommand("pg").setExecutor(new PGCommand());
        getCommand("pg").setTabCompleter(new PGTabComplete());

        Bukkit.getPluginManager().registerEvents(new PGLobbyHook(), this);

        new LobbyUpdater().runTaskTimer(this, 20, 1);

    }

    @Override
    public void onDisable() {}
}
