package com.github.pgutils.commands;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.PGSpawn;
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
import org.checkerframework.checker.units.qual.A;

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
						((Player)sender).getInventory().setItem(((Player)sender).getInventory().firstEmpty(), GeneralUtils.getTool());
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&eYour retrieve PGUtils Tool!"));
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("setportal")) {
					Location leaveLocation;

					if(PGLobbyHook.pos1 == null) {
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&cYour not select &bpos2&e!"));
						return true;
					}

					if(PGLobbyHook.pos2 == null) {
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&cYour  not select selected &bpos2&e!"));
						return true;
					}

					if(!((Player) sender).isFlying()) {
						leaveLocation = ((Player) sender).getLocation();
					} else {
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&cYour need to stand on respawn Location!"));
						return true;
					}

					if (PGSpawn.setPortal(PGLobbyHook.pos1, PGLobbyHook.pos2, leaveLocation)) {
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("save-portal-message", "&aSuccesval saved Portal Location's!")));
					}
					return true;
					
				}
				
				if(args[0].equalsIgnoreCase("setlobby")) {
					if(sender instanceof Player) {
						if (PGSpawn.setLobby(((Player) sender).getLocation())) {
							sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("save-lobby-message", "&aSuccesval saved Lobby Location!")));
						}
						return true;
					}
				}

				if(args[0].equalsIgnoreCase("tp")) {
					if (args[1].equalsIgnoreCase("lobby")) {
						Location lobby = PGSpawn.getLobby();
						if (lobby == null) {
							sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-lobby-message", "&cLobby Location is not set!")));
							return true;
						}
						((Player) sender).teleport(lobby);
						sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("tp-lobby-message", "&aTeleported to Lobby Location!")));
						return true;

					}

					if (args[1].equalsIgnoreCase("portal")) {
						ArrayList<Location> portal = PGSpawn.getPortal();
						if (portal.size() < 1) {
							sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-portal-message", "&cPortal Location's is not set!")));
							return true;
						}

						((Player) sender).teleport(portal.get(portal.size() - 1));

						sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("tp-portal-message", "&aTeleported to Portal Location!")));
						return true;
					}
					return false;
				}

				if(args[0].equalsIgnoreCase("leave")) {
					Player player = (Player) sender;
					ArrayList<Location> portal = PGSpawn.getPortal();

					if (portal.size() >= 1) {
						if (PGSpawn.joinPlayer.contains(player)) {
							PGSpawn.restoreInv(player);
							PGSpawn.joinPlayer.remove(player);
							player.teleport(portal.get(portal.size() - 1));

							sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("leave-message", "&eYour leave a game!")));
						} else {
							sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("not-join-message", "&eYour are not in game!")));
						}
					}
					return true;
				}

		}
		return false;
	}


}
