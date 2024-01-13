package com.github.pgutils.hooks;

import com.github.pgutils.utils.*;
import com.github.pgutils.entities.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.pgutils.PGUtils;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;


public class PGLobbyHook implements Listener {
	public static Location pos1 = null;
	public static Location pos2 = null;


	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (player.getItemInHand().equals(PortalManager.getTool())) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand().equals(EquipmentSlot.HAND)) {
				pos2 = e.getClickedBlock().getLocation();
				player.sendMessage(Messages.messageWithPrefix("portal-selected-pos2", "&cYour selected &bposition2&e!"));
			}

			if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getHand().equals(EquipmentSlot.HAND)){
				pos1 = e.getClickedBlock().getLocation();
				player.sendMessage(Messages.messageWithPrefix("portal-selected-pos1", "&eYour selected &bposition1&e!"));
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
					lobby.addPlayer(player);
				}
			});
		}

		if (PlayerManager.cannotMove.contains(player)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void closeInventory(InventoryCloseEvent e){
		if (e.getPlayer().getOpenInventory().getTitle().equals(PlayerChestReward.ChestTitle)) {
			PlayerChestReward.updatePlayerCheste(e.getInventory().getContents(), ((Player) e.getPlayer()));
		}
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		LobbyMenu.JoinLobbyClick(e);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		GeneralUtils.kickPlayerGlobal(player);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			if (PlayerManager.cannotDamage.contains(event.getDamager())) {
				event.setCancelled(true);
			}
		}
		if (event.getDamager() instanceof Firework) {
			Firework fw = (Firework) event.getDamager();
			if (fw.hasMetadata("nodamage")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (PlayerManager.isInvulnerable.contains(event.getEntity())) {
				event.setCancelled(true);
			}
		}

	}

}
