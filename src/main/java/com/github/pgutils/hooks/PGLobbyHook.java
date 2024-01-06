package com.github.pgutils.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.PGUtils;
import com.github.pgutils.commands.PGCommand;
import org.bukkit.inventory.EquipmentSlot;


public class PGLobbyHook implements Listener {
	public static Location pos1 = null;
	public static Location pos2 = null; 
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(player.getItemInHand().equals(PGCommand.getTool())) {
        	if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
				pos2 = player.getLocation();
				player.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&eYour selected &bpos2&e!"));
			}

			if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
				pos1 = player.getLocation();
				player.sendMessage(GeneralUtils.fixColors( PGUtils.getPlugin(PGUtils.class).prefix + "&eYour selected &bpos1&e!"));
			}
        	
        	e.setCancelled(true);
		}
	}
	
}