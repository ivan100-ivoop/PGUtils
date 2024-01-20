package com.github.pgutils.hooks;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


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
		if (e.getClickedInventory() == null) {
			return;
		}
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

	@EventHandler
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		if(GeneralUtils.isPlayerInGame(player) != null){
			if(!player.hasPermission("pgutils.bypass.commands") || !player.isOp()) {
				String command = (event.getMessage().contains(" ") ? event.getMessage().substring(1, event.getMessage().indexOf(" ")) : event.getMessage());
				if (!PGUtils.getPlugin(PGUtils.class).getConfig().getStringList("whitelist-commands").contains(command)) {
					player.sendMessage(Messages.messageWithPrefix("not-allow-command", "&cYour in game now!"));
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onPlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
		if(event.getRightClicked().getPersistentDataContainer().has(Keys.noSteal, PersistentDataType.BOOLEAN)){
			event.setCancelled(true);
		}
	}


}
