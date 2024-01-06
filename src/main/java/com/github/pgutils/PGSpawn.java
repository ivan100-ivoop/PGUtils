package com.github.pgutils;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class PGSpawn {

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

    public static boolean setPortal(Location pos1, Location pos2) {

        File portalFile = new File(PGUtils.getPlugin(PGUtils.class).database, "lobby.yml");
        try {
            if (!portalFile.exists())
                portalFile.createNewFile();

            YamlConfiguration lobby = new YamlConfiguration();
            lobby.load(portalFile);
            ArrayList<Location> locations =  new ArrayList<Location>();
            locations.add(pos1);
            locations.add(pos2);
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
