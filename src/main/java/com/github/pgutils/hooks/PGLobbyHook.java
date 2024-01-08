package com.github.pgutils.hooks;

import com.github.pgutils.LobbyMenu;
import com.github.pgutils.PlayerChestReward;
import com.github.pgutils.entities.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.PGUtils;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;


public class PGLobbyHook implements Listener {
	public static Location pos1 = null;
	public static Location pos2 = null;


	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (player.getItemInHand().equals(GeneralUtils.getTool())) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand().equals(EquipmentSlot.HAND)) {
				pos2 = e.getClickedBlock().getLocation();
				player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + "&eYour selected &bpos2&e!"));
			}

			if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getHand().equals(EquipmentSlot.HAND)){
				pos1 = e.getClickedBlock().getLocation();
				player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + "&eYour selected &bpos1&e!"));
			}

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
				e.getFrom().getBlockY() == e.getTo().getBlockY() &&
				e.getFrom().getBlockZ() == e.getTo().getBlockZ()) {
			return;
		}

		Player player = e.getPlayer();
		if (PGUtils.getPlugin(PGUtils.class).getPortalManager().inPortal(player.getLocation())) {
			Bukkit.getScheduler().runTask(PGUtils.getPlugin(PGUtils.class), () -> {
				int id = GeneralUtils.findPriorityLobby();
				Lobby lobby = Lobby.lobbies.stream()
						.filter(lobby_ -> lobby_.getID() == id)
						.findFirst()
						.orElse(null);
				if (lobby == null) {
					player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-lobby-message", "&cLobby is not found!")));
				} else {
					e.setCancelled(true);
					PlayerChestReward.saveInv(player);
					lobby.addPlayer(player);
				}
			});
		}
	}

	@EventHandler
	public void closeInventory(InventoryCloseEvent e){
		if (((Player) e.getPlayer()).getOpenInventory().getTitle().equals(PlayerChestReward.ChestTitle)) {
			PlayerChestReward.updatePlayerCheste(e.getInventory().getContents(), ((Player) e.getPlayer()));
		}
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if(e.getClickedInventory().getViewers().get(0).getOpenInventory().getTitle().equals(LobbyMenu.LobbyGuiTitle)){
			if(e.getClick().isLeftClick() || e.getClick().isRightClick() ){
				Player player = ((Player) e.getWhoClicked());
				Bukkit.getScheduler().runTask(PGUtils.getPlugin(PGUtils.class), () -> {
					int id = e.getCurrentItem().getItemMeta().getCustomModelData();
					Lobby lobby = Lobby.lobbies.stream()
							.filter(lobby_ -> lobby_.getID() == id)
							.findFirst()
							.orElse(null);
					if (lobby == null) {
						player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-lobby-message", "&cLobby is not found!")));
					} else {
						e.setCancelled(true);
						PlayerChestReward.saveInv(player);
						lobby.addPlayer(player);
					}
				});
			}
		}
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (GeneralUtils.isPlayerInGame(player)) {
			PlayerChestReward.restoreInv(player);
			player.teleport(GeneralUtils.getRespawnPoint());
		}
	}

}
