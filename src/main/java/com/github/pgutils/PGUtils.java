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
	public static File database = null;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		
		prefix = getConfig().getString("prefix", "&7[&e&lPGUtils&7] ");
		
		if(!new File(getDataFolder(), "database").exists())
			new File(getDataFolder(), "database").mkdir();
		
		database = new File(getDataFolder(), "database");
		
		getCommand("pgutils").setExecutor(new PGCommand());
		getCommand("pgutils").setTabCompleter(new PGTabComplete());
		
		Bukkit.getPluginManager().registerEvents(new PGLobbyHook(), this);
		
	}

    @Override
    public void onDisable() {}
}
