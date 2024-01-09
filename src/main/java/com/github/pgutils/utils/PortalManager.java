package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        if (portalLocations != null && portalLocations.size() >= 2) {
            player.teleport(portalLocations.get(2));
            return true;
        } else {
            player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-portal-message", "&cThe portal locations are not properly defined.")));
        }
        return false;
    }
    public boolean savePortalLocations(String portalName, Location loc1, Location loc2, Location loc3) {
        String path = "portals." + portalName;

        config.set(path + ".world", loc1.getWorld().getName());
        config.set(path + ".loc1.x", loc1.getX());
        config.set(path + ".loc1.y", loc1.getY());
        config.set(path + ".loc1.z", loc1.getZ());

        config.set(path + ".loc2.x", loc2.getX());
        config.set(path + ".loc2.y", loc2.getY());
        config.set(path + ".loc2.z", loc2.getZ());
        config.set(path + ".respawn.x", loc3.getX());
        config.set(path + ".respawn.y", loc3.getY());
        config.set(path + ".respawn.z", loc3.getZ());

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

        double loc3X = config.getDouble(path + ".respawn.x");
        double loc3Y = config.getDouble(path + ".respawn.y");
        double loc3Z = config.getDouble(path + ".respawn.z");

        portalLocations.add(new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc1X, loc1Y, loc1Z));
        portalLocations.add(new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc2X, loc2Y, loc2Z));
        portalLocations.add(new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc3X, loc3Y, loc3Z));


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

    private boolean isInRegion(Location target, Location loc1, Location loc2) {

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
                targetZ >= minZ + 1 && targetZ <= maxZ  + 1;

        return inPortal;
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

    private static List<String> getLoreWithFix(List<String> lores) {
        ArrayList<String> colored = new ArrayList<String>();
        for(String lore : lores){
            colored.add(GeneralUtils.fixColors(lore));
        }
        return colored;
    }

    public static ItemStack getTool() {
        ItemStack tool = new ItemStack(Material.getMaterial(PGUtils.getPlugin(PGUtils.class).getConfig().getString("portal-tool.material", "STICK")));
        ItemMeta meta = tool.getItemMeta();
        meta.setCustomModelData(Integer.parseInt("6381260"));
        meta.setDisplayName(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).getConfig().getString("portal-tool.name", "&5&lPGUtils &e&lTool")));
        meta.setLore(getLoreWithFix(PGUtils.getPlugin(PGUtils.class).getConfig().getStringList("portal-tool.lore")));
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        tool.setItemMeta(meta);

        return tool;
    }
}
