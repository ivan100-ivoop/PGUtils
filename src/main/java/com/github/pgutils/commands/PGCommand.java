package com.github.pgutils.commands;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.LobbyMenu;
import com.github.pgutils.PGUtils;
import com.github.pgutils.PlayerChestReward;
import com.github.pgutils.entities.KOTHArena;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.hooks.PGLobbyHook;

import java.util.Optional;

import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PGCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
			if(args.length >= 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					PGUtils.getPlugin(PGUtils.class).reloadConfig();
					sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("reload-message", "&aSuccesval reload!")));
					return true;
				}
				if (args[0].equalsIgnoreCase("leave")) {
					if (Lobby.lobbies.stream().anyMatch(lobby -> lobby.getPlayers().contains(player))) {
						Lobby.lobbies.stream()
								.filter(lobby -> lobby.getPlayers().contains(player))
								.findFirst()
								.get()
								.removePlayer(player);
						PlayerChestReward.restoreInv(player);
						player.teleport(GeneralUtils.getRespawnPoint());
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("tool")) {
					if (sender instanceof Player) {
						player.getInventory().setItem(((Player) sender).getInventory().firstEmpty(), GeneralUtils.getTool());
						sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + "&eYour retrieve PGUtils Tool!"));
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("chest")) {
					player.openInventory(PlayerChestReward.getPlayerChest(((Player) sender)));
					return true;
				}

				if (args[0].equalsIgnoreCase("setportal")) {
					if (PGLobbyHook.pos1 == null) {
						sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + "&cYour not select &bpos2&e!"));
						return true;
					}
					if (PGLobbyHook.pos2 == null) {
						sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + "&cYour  not select selected &bpos2&e!"));
						return true;
					}


					if (PGUtils.getPlugin(PGUtils.class).getPortalManager().savePortalLocations("join", PGLobbyHook.pos1, PGLobbyHook.pos2, player.getLocation())) {
						sender.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("save-portal-message", "&aSuccesval saved Portal Location's!")));
					}
					return true;
					
				}

				if(args[0].equalsIgnoreCase("tp")) {
					if(args[1].equalsIgnoreCase("lobby")){
						Lobby selectedLobby = Lobby.lobbies.get(Integer.parseInt(args[2]));
						if(selectedLobby == null){
							sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-lobby-message", "&cLobby Location is not set!")));
							return true;
						}

						player.teleport(selectedLobby.getPos());
						sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("tp-lobby-message", "&aTeleported to Lobby Location!")));
						return true;

					}

					if(args[1].equalsIgnoreCase("portal")){
						if(PGUtils.getPlugin(PGUtils.class).getPortalManager().teleportToPortal(player, "join"))
							sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("tp-portal-message", "&aTeleported to Portal Location!")));
						return true;
					}
					return false;

				}

				if (args[0].equalsIgnoreCase("setleave")) {
					if(GeneralUtils.setRespawnPoint(player.getLocation())){
						player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("respawn-set-message", "&aSuccesval saved Leave Location!")));
						return true;
					}
					return false;
				}

				if (args[0].equalsIgnoreCase("game")) {
					if (args.length >= 2) {
						if (args[1].equalsIgnoreCase("koth")) {
							if (args.length >= 3) {
								if (args[2].equalsIgnoreCase("create")) {
									if (args.length >= 4) {
										if (args[3].equalsIgnoreCase("arena")) {
											KOTHArena arena = new KOTHArena();
											arena.setPos(player.getLocation());
											sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("create-arena-message", "&aSuccessful created Arena! N" + arena.getID())));
											GeneralUtils.playerSelectPlaySpace(player, arena);
										}
										if (args[3].equalsIgnoreCase("spawn")) {
											Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
													.filter(selector -> selector.player.equals(sender))
													.findFirst();
											if (!arena.isPresent()) {
												player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-arena-message", "&cPlayspace is not selected!")));
											} else {
												if (arena.get().playSpace instanceof KOTHArena) {
													KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
													kothArena.addSpawnLocation(player.getLocation());
													sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("create-spawn-message", "&aSuccessful created Spawn Location!")));
												} else {
													sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-arena-message", "&cYou need to select a KOTH arena!")));
												}
											}
										}

									}
								}
							}
						}
						if (args[1].equalsIgnoreCase("select")) {
							if (args.length >= 3) {
								int id = Integer.parseInt(args[2]);
								PlaySpace playSpace = PlaySpace.playSpaces.stream()
										.filter(space -> space.getID() == id)
										.findFirst()
										.orElse(null);
								if (playSpace == null) {
									sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-arena-message", "&cPlaySpace is not found!")));
								} else {
									GeneralUtils.playerSelectPlaySpace(player, playSpace);
									sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("select-arena-message", "&aSuccessful selected " + playSpace.getType() + "!")));
								}
							}
						}

					}
					return true;
				}
				if (args[0].equalsIgnoreCase("lobby")) {
					if (args.length >= 2) {
						if (args[1].equalsIgnoreCase("create")) {
							Lobby lobby = new Lobby();
							lobby.setPos(player.getLocation());
							GeneralUtils.playerSelectLobby(player, lobby);
							sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("create-lobby-message", "&aSuccessful created Lobby Location! " + Lobby.lobbies.size())));
						}
						if (args[1].equalsIgnoreCase("join")) {
							if (args.length >= 3) {
								int id = Integer.parseInt(args[2]);
								Lobby lobby = Lobby.lobbies.stream()
										.filter(lobby_ -> lobby_.getID() == id)
										.findFirst()
										.orElse(null);
								if (lobby == null) {
									sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-lobby-message", "&cLobby is not found!")));
									return false;
								} else {
									PlayerChestReward.saveInv(player);
									lobby.addPlayer(player);
								}
							} else {
								Optional<PlayerLobbySelector> lobbySelector = PGUtils.selectedLobby.stream()
										.filter(selector -> selector.player.equals(sender))
										.findFirst();
								if (!lobbySelector.isPresent()) {
									sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-arena-message", "&cLobby is not found!")));
									return false;
								}
								Lobby lobby = lobbySelector.get().lobby;
								lobby.addPlayer(player);

							}
						}

						if (args[1].equalsIgnoreCase("add-game")) {
							if (args.length >= 4) {
								int lobbyId = Integer.parseInt(args[2]);
								int gameId = Integer.parseInt(args[3]);

								Lobby lobby = Lobby.lobbies.stream()
										.filter(lobby_ -> lobby_.getID() == lobbyId)
										.findFirst()
										.orElse(null);

								PlaySpace playSpace = PlaySpace.playSpaces.stream()
										.filter(space -> space.getID() == gameId)
										.findFirst()
										.orElse(null);

								if (lobby == null) {
									sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-lobby-message", "&cLobby is not found!")));
									return false;
								}

								if (playSpace == null) {
									sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-arena-message", "&cPlaySpace is not found!")));
									return false;
								}

								lobby.addPlaySpace(playSpace);
								playSpace.setLobby(lobby);
								sender.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("add-arena-message", "&aSuccessful added " + playSpace.getType() + " to " + lobby.getID() + "!")));
							}
						}

					} else {
						player.openInventory(new LobbyMenu().prepareMenu().getLobby());
					}
					return true;
				}

		}
		return false;
	}


}
