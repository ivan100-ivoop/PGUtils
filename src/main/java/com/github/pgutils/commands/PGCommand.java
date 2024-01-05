package com.github.pgutils.commands;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.PGUtils;
import com.github.pgutils.hooks.PGLobbyHook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PGCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
			if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					PGUtils.getPlugin(PGUtils.class).reloadConfig();
					sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("reload-message", "&aSuccesval reload!")));
					return true;
				}
				
				if(args[0].equalsIgnoreCase("tool")) {
					if(sender instanceof Player) {
						((Player)sender).getInventory().setItem(((Player)sender).getInventory().firstEmpty(), PGCommand.getTool());
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&eYour retrieve PGUtils Tool!"));
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("setportal")) {
					if(PGLobbyHook.pos1 == null) {
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&cYour not select &bpos2&e!"));
						return true;
					}
					if(PGLobbyHook.pos2 == null) {
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&cYour  not select selected &bpos2&e!"));
						return true;
					} 
					
					YamlConfiguration lobby = PGCommand.getLobby();
					lobby.set("portal", new Location[] {
						PGLobbyHook.pos1,
						PGLobbyHook.pos2
					});
						
					try {
						lobby.save(new File(PGUtils.getPlugin(PGUtils.class).database, "lobby.yml"));
					} catch (IOException e) {
						PGUtils.getPlugin(PGUtils.class).logger.log(Level.SEVERE, e.getMessage());
						e.printStackTrace();
					}
						
					sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("save-portal-message", "&aSuccesval saved Portal Location's!")));
					return true;
					
				}
				
				if(args[0].equalsIgnoreCase("setlobby")) {
					if(sender instanceof Player) {
						YamlConfiguration lobby = PGCommand.getLobby();
						lobby.set("lobby", ((Player) sender).getLocation());
							
						try {
							lobby.save(new File(PGUtils.getPlugin(PGUtils.class).database, "lobby.yml"));
						} catch (IOException e) {
							PGUtils.getPlugin(PGUtils.class).logger.log(Level.SEVERE, e.getMessage());
							e.printStackTrace();
						}
							
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("save-lobby-message", "&aSuccesval saved Lobby Location!")));
						return true;
					}
					
				}
		}
		return false;
	}
	
	
	public static YamlConfiguration getLobby() {
		// TODO Auto-generated method stub
		File portal = new File(PGUtils.getPlugin(PGUtils.class).database, "lobby.yml");
		if(!portal.exists())
			try {
				portal.createNewFile();
			} catch (IOException e) {
				PGUtils.getPlugin(PGUtils.class).logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			}
		
		YamlConfiguration lobby = new YamlConfiguration();
		try {
			lobby.load(portal);
		} catch (Exception e) {
			PGUtils.getPlugin(PGUtils.class).logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		return lobby;
		
	}
	
	public static ItemStack getTool() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(GeneralUtils.fixColors("&7Left Click on pos1"));
		lore.add(GeneralUtils.fixColors("&7Right Click on pos1"));
		
		ItemStack tool = new ItemStack(Material.STICK);
		ItemMeta meta = tool.getItemMeta();
		
		meta.setCustomModelData(Integer.parseInt("6381260"));
		meta.setDisplayName(GeneralUtils.fixColors("&5&lPGUtils &e&lTool"));
		meta.setLore(lore);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		tool.setItemMeta(meta);
		
		return tool;
	}

}
