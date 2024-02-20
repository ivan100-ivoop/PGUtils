package com.github.pgutils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
