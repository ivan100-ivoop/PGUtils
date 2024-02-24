package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.utils.db.PortalSave;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PortalManager {
    public static List<PortalSave> portals = new ArrayList<>();
    public static String inPortal(Location location) {
        for (PortalSave portal : portals) {
            if (isInRegion(location, portal.getLocation1(), portal.getLocation2())){
                if (portal.getLobbyID() != null && !portal.getLobbyID().isEmpty()) {
                    return portal.getLobbyID();
                }
            }
        }
        return null;
    }

    public static void teleportPlayer(Player player, PlayerMoveEvent event){
        String lobbyID = inPortal(player.getLocation());
        if (lobbyID != null) {
            Bukkit.getScheduler().runTask(PGUtils.getPlugin(PGUtils.class), () -> {
                Lobby lobby = Lobby.lobbies.stream()
                        .filter(lobby_ -> lobby_.getUID().equalsIgnoreCase(lobbyID))
                        .findFirst()
                        .orElse(null);
                if (lobby != null) {
                    lobby.addPlayer(player);
                } else {
                    player.sendMessage(Messages.messageWithPrefix("missing-lobby-message", "&cLobby is not found!"));
                }
            });
            event.setCancelled(true);
        }
    }

    private static boolean isInRegion(Location target, Location loc1, Location loc2) {

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int targetX = target.getBlockX();
        int targetY = target.getBlockY();
        int targetZ = target.getBlockZ();

        boolean inPortal = targetX >= minX && targetX <= maxX &&
                targetY >= minY && targetY <= maxY &&
                targetZ >= minZ + 1 && targetZ <= maxZ + 1;

        return inPortal;
    }

    private static List<String> getLoreWithFix(List<String> lores) {
        ArrayList<String> colored = new ArrayList<String>();
        for (String lore : lores) {
            colored.add(GeneralUtils.fixColors(lore));
        }
        return colored;
    }

    public static ItemStack getTool() {
        ItemStack tool = new ItemStack(Material.getMaterial(PGUtils.getPlugin(PGUtils.class).getConfig().getString("portal-tool.material", "STICK")));
        ItemMeta meta = tool.getItemMeta();
        meta.setCustomModelData(Integer.parseInt("6381260"));
        meta.setDisplayName(GeneralUtils.fixColors(PGUtils.instance.getConfig().getString("portal-tool.name", "&5&lPGUtils &e&lTool")));
        meta.setLore(getLoreWithFix(PGUtils.instance.getConfig().getStringList("portal-tool.lore")));
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        tool.setItemMeta(meta);

        return tool;
    }
}
