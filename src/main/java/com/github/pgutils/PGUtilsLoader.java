package com.github.pgutils;

import com.github.pgutils.commands.PGUtilsCommand;
import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.customitems.CustomEffectUpdater;
import com.github.pgutils.customitems.CustomItemLibrary;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.entity_utils.KOTHArenaUtils;
import com.github.pgutils.entities.entity_utils.LobbyUtils;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.hooks.PGLobbyHook;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import com.github.pgutils.utils.*;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PGUtilsLoader {
    public PGUtils instance;
    public File savedPlayers = null, saveInv = null, rewardsChest = null, lang = null;
    public DatabaseManager sqlDB;
    public String prefix;
    public RewardManager rewardManager = null;
    public PortalManager PM = null;
    public PGUtilsCommand PGCommands = null;

    public List<PlayerPlaySpaceSelector> selectedPlaySpace = new ArrayList<>();
    public List<PlayerLobbySelector> selectedLobby = new ArrayList<>();

    public PGUtilsLoader(PGUtils pl){
        this.instance = pl;
    }

    private void fixDir(){
        if (!this.lang.exists()) { this.lang.mkdir(); }
        if (!this.savedPlayers.exists()) { this.savedPlayers.mkdir(); }
        if (!this.saveInv.exists()) { saveInv.mkdir(); }
        if (!this.rewardsChest.exists()) { this.rewardsChest.mkdir(); }
        if (!new File(this.lang, "en.yml").exists()) {
            this.lang.mkdir();
            this.instance.saveResource("lang/en.yml", false);
        }
    }

    public void start(){
        this.instance.saveDefaultConfig();

        this.prefix = Messages.getMessage("prefix", "&7[&e&lPGUtils&7] ", false);
        this.lang = new File(this.instance.getDataFolder(), "lang");
        this.savedPlayers = new File(this.instance.getDataFolder(), "savedPlayers");
        this.saveInv = new File(this.savedPlayers, "saveInv");
        this.rewardsChest = new File(this.savedPlayers, "PlayerChest");
        this.sqlDB = new DatabaseManager(this.instance);

        this.fixDir();

        this.PM = new PortalManager(sqlDB);
        this.rewardManager = new RewardManager(sqlDB);
        this.PGCommands = new PGUtilsCommand();

        this.registerCommands();
        this.registerEvents();
        this.registerTimers();
        this.loadGames();

        CustomItemLibrary.onStart();
        GeneralUtils.cleanupArmorStands();

        this.registerGames();
    }

    public void stop(){
        Lobby.lobbies.forEach(lobby -> {
            lobby.kickAll();
        });

        PlaySpace.playSpaces.forEach(playSpace -> {
            playSpace.end(null);
        });

        CustomEffect.removeAllEffects();
        this.sqlDB.disconnect();
    }

    public void restart(){
        Lobby.lobbies.forEach(lobby -> {
            lobby.kickAll();
        });

        PlaySpace.playSpaces.forEach(playSpace -> {
            playSpace.end(null);
        });

        CustomEffect.removeAllEffects();
        this.sqlDB.disconnect();
        this.instance.reloadConfig();
    }

    private void registerGames() {
        PlaySpace.playSpaceTypes.put("koth", KOTHArena.class);
    }

    private void loadGames() {
        LobbyUtils.loadLobbies();
        KOTHArenaUtils.loadArenas();
    }

    private void registerEvents(){
        Bukkit.getPluginManager().registerEvents(new PGLobbyHook(), this.instance);
        Bukkit.getPluginManager().registerEvents(new CustomItemLibrary(), this.instance);
    }

    private void registerTimers(){
        new LobbyUpdater().runTaskTimer(this.instance, 20, 1);
        new CustomEffectUpdater().runTaskTimer(this.instance, 20, 1);
        new ParticleUpdater().runTaskTimer(this.instance, 20, 1);
    }

    private void registerCommands() {
        this.instance.getCommand("pg").setExecutor(this.PGCommands);
        this.instance.getCommand("pg").setTabCompleter(this.PGCommands);
    }
}