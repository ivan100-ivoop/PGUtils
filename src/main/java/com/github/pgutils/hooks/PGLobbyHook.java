package com.github.pgutils.hooks;

import com.github.pgutils.PGSpawn;
import com.github.pgutils.PlayerChestReward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.PGUtils;
import com.github.pgutils.commands.PGCommand;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;


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
			if (PGSpawn.addPlayer(player)) {
				Bukkit.getScheduler().runTask(PGUtils.getPlugin(PGUtils.class), () -> {
					player.getInventory().clear();
					player.teleport(PGSpawn.getLobby());
					player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("lobby-join-message", "&eYour join to Lobby!")));
					e.setCancelled(true);
				});
			}
		}
	}

	@EventHandler
	public void closeInventory(InventoryCloseEvent e){
		if (((Player) e.getPlayer()).getOpenInventory().getTitle().equals(PlayerChestReward.ChestTitle)) {
			PlayerChestReward.updatePlayerCheste(e.getInventory().getContents(), ((Player) e.getPlayer()));
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (PGSpawn.joinPlayer.contains(player)) {
			PGSpawn.restoreInv(player);
			PGSpawn.joinPlayer.remove(player);
			PGUtils.getPlugin(PGUtils.class).getPortalManager().teleportToPortal(player, "join");
		}
	}

}
