package com.github.pgutils;

import com.github.pgutils.commands.PGUtilsCommand;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.entity_utils.KOTHArenaUtils;
import com.github.pgutils.entities.entity_utils.LobbyUtils;
import com.github.pgutils.hooks.PGLobbyHook;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import com.github.pgutils.utils.PortalManager;
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class PGUtils extends JavaPlugin {
    public Logger logger = Bukkit.getLogger();
    public String prefix;
    public static File database = null, saveInv = null, rewardsChest = null;
    public static PortalManager PM = null;
    public static List<PlayerPlaySpaceSelector> selectedPlaySpace = new ArrayList<>();
    public static List<PlayerLobbySelector> selectedLobby = new ArrayList<>();


    @Override
    public void onEnable() {
        saveDefaultConfig();

        prefix = getConfig().getString("prefix", "&7[&e&lPGUtils&7] ");

        database = new File(getDataFolder(), "database");
        saveInv = new File(database, "saveInv");
        rewardsChest = new File(database, "PlayerChest");

        if (!database.exists()){ database.mkdir(); }
        if (!saveInv.exists()){ saveInv.mkdir(); }
        if (!rewardsChest.exists()){ rewardsChest.mkdir(); }

        PM = new PortalManager();

        getCommand("pg").setExecutor(new PGUtilsCommand());
        getCommand("pg").setTabCompleter(new PGUtilsCommand());


        Bukkit.getPluginManager().registerEvents(new PGLobbyHook(), this);

        new LobbyUpdater().runTaskTimer(this, 20, 1);

        deserializationBootstrap();
    }
    public static PortalManager getPortalManager() { return PM; }

    @Override
    public void onDisable() {
        Lobby.lobbies.forEach(lobby -> {
            lobby.kickAll();
            lobby.delete();
        });

        serializationBootstrap();
    }

    public void serializationBootstrap() {
        LobbyUtils.saveLobbies();
        KOTHArenaUtils.saveArenas();
    }

    public void deserializationBootstrap() {
        LobbyUtils.loadLobbies();
        LobbyUtils.deleteAllLobbies();
        KOTHArenaUtils.loadArenas();
        KOTHArenaUtils.deleteAllArenas();

    }
}
