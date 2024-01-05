package com.github.pgutils;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.pgutils.commands.CommandExecutor;
import com.github.pgutils.commands.CommandTabComplite;

public class Launcher extends JavaPlugin {
	public Logger logger = Bukkit.getLogger();
	public String prefix;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		
		prefix = getConfig().getString("prefix", "&7[&e&lPGUtils&7] ");
		
		if(!new File(getDataFolder(), "database").exists())
			new File(getDataFolder(), "database").mkdir();
		
		getCommand("pg").setExecutor(new CommandExecutor());
		getCommand("pg").setTabCompleter(new CommandTabComplite());
		
	}
	
	@Override
	public void onDisable() {}
}
