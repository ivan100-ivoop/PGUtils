package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PortalManager {

    private final DatabaseManager db;

    public PortalManager(DatabaseManager db) {
        this.db = db;
        db.connect();

        if (!db.tableExists(db.fixName("portals"))) {
            if(!db.isMysql()) {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("portals") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR (255)," +
                        "location_1_x DOUBLE," +
                        "location_1_y DOUBLE," +
                        "location_1_z DOUBLE," +
                        "location_2_x DOUBLE," +
                        "location_2_y DOUBLE," +
                        "location_2_z DOUBLE," +
                        "location_world VARCHAR (255)," +
                        "location_respawn_x DOUBLE," +
                        "location_respawn_y DOUBLE," +
                        "location_respawn_z DOUBLE," +
                        "location_respawn_world VARCHAR (255)," +
                        "UNIQUE (id)" +
                        ");");
            } else {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("portals") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `name` varchar(255) NOT NULL," +
                        "  `location_1_x` double NOT NULL,"+
                        "  `location_1_y` double NOT NULL,"+
                        "  `location_1_z` double NOT NULL,"+
                        "  `location_2_x` double NOT NULL,"+
                        "  `location_2_y` double NOT NULL,"+
                        "  `location_2_z` double NOT NULL,"+
                        "  `location_world` varchar(255) NOT NULL," +
                        "  `location_respawn_x` double NOT NULL,"+
                        "  `location_respawn_y` double NOT NULL,"+
                        "  `location_respawn_z` double NOT NULL,"+
                        "  `location_respawn_world` varchar(255) NOT NULL" +
                        ");");
                db.execute("ALTER TABLE " + db.fixName("portals") + " ADD PRIMARY KEY (`id`);");
                db.execute("ALTER TABLE " + db.fixName("portals") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;");

            }
        }
        db.disconnect();
    }

    public boolean teleportToPortal(Player player, String portalName) {
        List<Location> portalLocations = getPortalLocations(portalName);
        if (portalLocations != null && portalLocations.size() == 3) {
            player.teleport(portalLocations.get(2));
            db.disconnect();
            return true;
        } else {
            player.sendMessage(Messages.messageWithPrefix("missing-portal-message", "&cThe portal locations are not properly defined."));
        }
        db.disconnect();
        return false;
    }

    public boolean savePortalLocations(String portalName, Location loc1, Location loc2, Location loc3) {
        db.connect();
        String insertQuery = "INSERT INTO " + db.fixName("portals") + " (name, location_1_x, location_1_y, location_1_z, location_2_x, location_2_y, location_2_z, location_world, location_respawn_x, location_respawn_y, location_respawn_z, location_respawn_world) VALUES (?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return db.executeInsert(insertQuery, portalName,  loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ(), loc1.getWorld().getName(), loc3.getX(), loc3.getY(), loc3.getZ(), loc3.getWorld().getName());
    }

    public List<Location> getPortalLocations(String portalName) {
        db.connect();
        String selectQuery = "SELECT location_1_x, location_1_y, location_1_z, location_2_x, location_2_y, location_2_z, location_world, location_respawn_x, location_respawn_y, location_respawn_z, location_respawn_world FROM " + db.fixName("portals") + " WHERE name = ?";
        List<Object[]> results = db.executeQuery(selectQuery, portalName);

        if (results != null && results.size() == 1) {
            Object[] data = results.get(0);
            List<Location> portalLocations = new ArrayList<>();

            double loc1X = (double) data[0];
            double loc1Y = (double) data[1];
            double loc1Z = (double) data[2];
            double loc2X = (double) data[3];
            double loc2Y = (double) data[4];
            double loc2Z = (double) data[5];
            String worldName = (String) data[6];

            double respawnX = (double) data[7];
            double respawnY = (double) data[8];
            double respawnZ = (double) data[9];
            String respawnWorldName = (String) data[10];

            portalLocations.add(new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc1X, loc1Y, loc1Z));
            portalLocations.add(new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc2X, loc2Y, loc2Z));
            portalLocations.add(new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(respawnWorldName), respawnX, respawnY, respawnZ));
            db.disconnect();
            return portalLocations;
        }
        db.disconnect();
        return null;
    }

    public boolean inPortal(Location location) {
        db.connect();
        if (db.tableExists(db.fixName("portals"))) {
            String selectAllQuery = "SELECT name, location_1_x, location_1_y, location_1_z, location_2_x, location_2_y, location_2_z, location_world FROM " + db.fixName("portals");
            List<Object[]> results = db.executeQuery(selectAllQuery);

            if (results != null) {
                for (Object[] data : results) {
                    String portalName = (String) data[0];
                    double loc1X = (double) data[1];
                    double loc1Y = (double) data[2];
                    double loc1Z = (double) data[3];

                    double loc2X = (double) data[4];
                    double loc2Y = (double) data[5];
                    double loc2Z = (double) data[6];

                    String worldName = (String) data[7];

                    Location loc1 = new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc1X, loc1Y, loc1Z);
                    Location loc2 = new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc2X, loc2Y, loc2Z);

                    if (isInRegion(location, loc1, loc2)) {
                        db.disconnect();
                        return true;
                    }
                }
            }
        }
        db.disconnect();
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
        meta.setDisplayName(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).getConfig().getString("portal-tool.name", "&5&lPGUtils &e&lTool")));
        meta.setLore(getLoreWithFix(PGUtils.getPlugin(PGUtils.class).getConfig().getStringList("portal-tool.lore")));
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        tool.setItemMeta(meta);

        return tool;
    }
}
