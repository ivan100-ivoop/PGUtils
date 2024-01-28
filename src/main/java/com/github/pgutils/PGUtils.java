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
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class PGUtils extends JavaPlugin {

    public static PGUtilsLoader loader;
    public Logger logger = Bukkit.getLogger();
    @Override
    public void onEnable() {
        loader = new PGUtilsLoader(this);
        loader.start();
    }
    @Override
    public void onDisable() {
        loader.stop();
    }

}
