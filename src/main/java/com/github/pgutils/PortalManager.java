package com.github.pgutils;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PortalManager {

    private final FileConfiguration config;
    private final File configFile;

    public PortalManager() {
        this.configFile = new File(PGUtils.getPlugin(PGUtils.class).database, "portal.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        if (!this.configFile.exists()) {
            try {
                this.configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean teleportToPortal(Player player, String portalName) {
        List<Location> portalLocations = getPortalLocations(portalName);
        if (portalLocations != null && portalLocations.size() >= 1) {
            Location teleportLocation = portalLocations.get(0).add(portalLocations.get(1)).multiply(0.5); // Teleport to the center of the portal
            player.teleport(teleportLocation);
            return true;
        } else {
            player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-portal-message", "&cThe portal locations are not properly defined.")));
        }
        return false;
    }

    public boolean savePortalLocations(String portalName, Location loc1, Location loc2) {
        String path = "portals." + portalName;

        config.set(path + ".world", loc1.getWorld().getName());
        config.set(path + ".loc1.x", loc1.getX());
        config.set(path + ".loc1.y", loc1.getY());
        config.set(path + ".loc1.z", loc1.getZ());

        config.set(path + ".loc2.x", loc2.getX());
        config.set(path + ".loc2.y", loc2.getY());
        config.set(path + ".loc2.z", loc2.getZ());

        return saveConfig();
    }

    public List<Location> getPortalLocations(String portalName) {
        List<Location> portalLocations = new ArrayList<>();
        String path = "portals." + portalName;

        String worldName = config.getString(path + ".world");
        if (worldName == null) {
            return portalLocations; // Return an empty list if the portal is not found
        }

        double loc1X = config.getDouble(path + ".loc1.x");
        double loc1Y = config.getDouble(path + ".loc1.y");
        double loc1Z = config.getDouble(path + ".loc1.z");

        double loc2X = config.getDouble(path + ".loc2.x");
        double loc2Y = config.getDouble(path + ".loc2.y");
        double loc2Z = config.getDouble(path + ".loc2.z");

        portalLocations.add(new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc1X, loc1Y, loc1Z));
        portalLocations.add(new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc2X, loc2Y, loc2Z));

        return portalLocations;
    }

    public boolean inPortal(Location location) {
        if(config.isConfigurationSection("portals")) {
            for (String portalName : config.getConfigurationSection("portals").getKeys(false)) {
                List<Location> portalLocations = getPortalLocations(portalName);
                if (portalLocations != null && isInRegion(location, portalLocations.get(0), portalLocations.get(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInRegion(Location location, Location loc1, Location loc2) {
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        return location.getBlockX() >= minX && location.getBlockX() <= maxX &&
                location.getBlockY() >= minY && location.getBlockY() <= maxY &&
                location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }

    private boolean saveConfig() {
        try {
            config.save(configFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
