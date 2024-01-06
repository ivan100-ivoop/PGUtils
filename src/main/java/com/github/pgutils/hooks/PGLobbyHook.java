package com.github.pgutils.hooks;

import com.github.pgutils.PGSpawn;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.PGUtils;
import com.github.pgutils.commands.PGCommand;
import org.bukkit.event.player.PlayerMoveEvent;
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
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
				pos2 = player.getLocation();
				player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + "&eYour selected &bpos2&e!"));
			}

			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				pos1 = player.getLocation();
				player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + "&eYour selected &bpos1&e!"));
			}

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		ArrayList<Location> portal = PGSpawn.getPortal();
		if (portal.size() >= 1) {
			if (player.getLocation().distance(portal.get(0)) <= 1 || player.getLocation().distance(portal.get(1)) <= 1) {
				player.teleport(PGSpawn.getLobby());
				PGSpawn.AddPlayer(player);
				player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("lobby-join-message", "&eYour join to Lobby!")));
			}
		}
	}
}
