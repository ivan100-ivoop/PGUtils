package com.github.pgutils;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class PGSpawn {
    public static ArrayList<Player> joinPlayer = new ArrayList<Player>();

    public static boolean addPlayer(Player player){
        if(!joinPlayer.contains(player)){
            try {
                if(PGSpawn.saveInv(player)){
                    joinPlayer.add(player);
                    return true;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public static boolean inPotral(Location location) {
        ArrayList<Location> portal = PGSpawn.getPortal();
        if (portal.size() >= 1) {
            Location loc1 = portal.get(1);
            Location loc2 = portal.get(0);

            if (!loc1.getWorld().equals(location.getWorld())) {
                return false;
            }

            double minX = Math.min(loc1.getX(), loc2.getX());
            double minY = Math.min(loc1.getBlockY(), loc2.getY());
            double minZ = Math.min(loc1.getZ(), loc2.getZ());

            double maxX = Math.max(loc1.getX(), loc2.getX());
            double maxY = Math.max(loc1.getY(), loc2.getY());
            double maxZ = Math.max(loc1.getZ(), loc2.getZ());

            return ((location.getX() >= minX || location.getX() <= maxX) &&
                    (location.getY() >= minY || location.getY() <= maxY) &&
                    (location.getZ() >= minZ || location.getZ() <= maxZ));

        } else {
            return false;
        }
    }


    public static boolean restoreInv(Player player){
        try{
            File invBackup = new File(PGUtils.getPlugin(PGUtils.class).saveInv, player.getName() + ".yml");
            if (invBackup.exists()) {
                YamlConfiguration invPlayer = new YamlConfiguration();
                invPlayer.load(invBackup);
                ArrayList<ItemStack> tempInv = (ArrayList<ItemStack>) invPlayer.getList("inv");
                for(int i=0; i<tempInv.size(); i++){
                    player.getInventory().setItem(i, tempInv.get(i));
                }
                invBackup.delete();
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean saveInv(Player player) throws Exception {
        ArrayList<ItemStack> tempInv = new ArrayList<ItemStack>();
        File invBackup = new File(PGUtils.getPlugin(PGUtils.class).saveInv, player.getName() + ".yml");
        if (!invBackup.exists()) {
            invBackup.createNewFile();
            YamlConfiguration invPlayer = new YamlConfiguration();
            invPlayer.load(invBackup);
            player.getInventory().forEach(itemStack -> {
                tempInv.add(itemStack);
            });
            invPlayer.set("inv", tempInv);
            invPlayer.save(invBackup);
            return true;
        }
        return false;
    }


    public static Location getLobby() {

        File lobbyFile = new File(PGUtils.getPlugin(PGUtils.class).database, "lobby.yml");
        if (lobbyFile.exists())
            try {
                YamlConfiguration lobby = new YamlConfiguration();
                lobby.load(lobbyFile);
                return (Location) lobby.get("lobby");

            } catch (Exception e) {
                PGUtils.getPlugin(PGUtils.class).logger.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }

        return null;
    }

    public static boolean setLobby(Location pos1) {

        File lobbyFile = new File(PGUtils.getPlugin(PGUtils.class).database, "lobby.yml");
        try {
            if (!lobbyFile.exists())
                lobbyFile.createNewFile();

            YamlConfiguration lobby = new YamlConfiguration();
            lobby.load(lobbyFile);

            lobby.set("lobby", pos1);
            lobby.save(lobbyFile);

            return true;
        } catch (Exception e) {
            PGUtils.getPlugin(PGUtils.class).logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setPortal(Location pos1, Location pos2, Location leaveLocation) {

        File portalFile = new File(PGUtils.getPlugin(PGUtils.class).database, "lobby.yml");
        try {
            if (!portalFile.exists())
                portalFile.createNewFile();

            YamlConfiguration lobby = new YamlConfiguration();
            lobby.load(portalFile);
            ArrayList<Location> locations =  new ArrayList<Location>();
            locations.add(pos1); // 0
            locations.add(pos2); // 1
            locations.add(leaveLocation); // 2
            lobby.set("portal", locations);

            lobby.save(portalFile);

            return true;
        } catch (Exception e) {
            PGUtils.getPlugin(PGUtils.class).logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Location> getPortal() {

        File portalFile = new File(PGUtils.getPlugin(PGUtils.class).database, "lobby.yml");
        if (portalFile.exists())
            try {
                YamlConfiguration portal = new YamlConfiguration();
                portal.load(portalFile);
                return ((ArrayList<Location>) portal.getList("portal"));

            } catch (Exception e) {
                PGUtils.getPlugin(PGUtils.class).logger.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }
        return new ArrayList<Location>();
    }


}
