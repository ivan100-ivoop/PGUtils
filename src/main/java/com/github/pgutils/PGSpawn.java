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

// dumi class for testing!

public class PGSpawn {
    public static ArrayList<Player> joinPlayer = new ArrayList<Player>();

    public static boolean addPlayer(Player player){
        if(!PlayerChestReward.isPlayerHaveChest(player)){
            PlayerChestReward.createEmptyPlayerChest(player);
        }

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

}
