package com.github.pgutils.entities.entity_utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.games.TNTRArena;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.tntradditionals.TNTRJump;
import com.github.pgutils.entities.games.tntradditionals.TNTRSpawn;
import com.github.pgutils.entities.games.tntradditionals.TNTRJump;
import com.github.pgutils.entities.games.tntradditionals.TNTRSpawn;
import com.github.pgutils.utils.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class TNTRArenaUtils {

    public static void loadArenas() {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        String arenaSelectSQL = "SELECT name, gameUID, bombTimer, lobbyUID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, bombRatio FROM " + db.fixName("tntr");
        String spawnSelectSQL = "SELECT gameUID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid FROM " + db.fixName("tntr_spawn") + " WHERE gameUID=?";
        String jumpsSelectSQL = "SELECT gameUID, strength, radius, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid, cooldown FROM " + db.fixName("tntr_jump") + " WHERE gameUID=?";

        if (db.tableExists(db.fixName("tntr"))) {
            List<Object[]> results = db.executeQuery(arenaSelectSQL);

            if (results != null) {
                for (Object[] data : results) {
                    String name = (String) data[0]; //name
                    String UID = (String) data[1]; //gameUID
                    int bombTimer = (int) data[2]; //bombTimer
                    String lobbyUID = (String) data[3]; //lobbyUID
                    double x = (double) data[4]; //location_x
                    if (x != -1) {
                        double y = (double) data[5]; //location_y
                        double z = (double) data[6]; //location_z
                        double pitch = (double) data[7]; //location_pitch
                        double yaw = (double) data[8]; //location_yaw
                        String world = (String) data[9]; //location_world

                        int bombRatio = (int) data[10]; //bombRatio

                        TNTRArena jumparena = new TNTRArena();

                        jumparena.setPos(new Location(Bukkit.getWorld(world), x, y, z, new Double(yaw).floatValue(), new Double(pitch).floatValue()));
                        jumparena.setUID(UID);
                        jumparena.setName(name);
                        jumparena.setBombTimer(bombTimer);
                        jumparena.setBombRatio(bombRatio);

                        if (db.tableExists(db.fixName("tntr_jump"))) {
                            List<Object[]> jumps = db.executeQuery(jumpsSelectSQL, UID);

                            if (jumps != null) {
                                for (Object[] jump : jumps) {
                                    double strength = (double) jump[1];
                                    double radius = (double) jump[2];

                                    double jumpX = (double) jump[3];
                                    double jumpY = (double) jump[4];
                                    double jumpZ = (double) jump[5];
                                    double jumpPitch = (double) jump[6];
                                    double jumpYaw = (double) jump[7];
                                    String jumpWorld = (String) jump[8];
                                    String jumpUID = (String) jump[9];

                                    TNTRJump _jump = new TNTRJump(new Location(Bukkit.getWorld(jumpWorld), jumpX, jumpY, jumpZ, new Double(jumpYaw).floatValue(), new Double(jumpPitch).floatValue()), radius, strength);
                                    _jump.setID(jumpUID);
                                    jumparena.addJumpLocation(_jump);
                                }
                            }
                        }

                        if (db.tableExists(db.fixName("tntr_spawn"))) {
                            List<Object[]> spawns = db.executeQuery(spawnSelectSQL, UID);

                            if (spawns != null) {
                                for (Object[] spawn : spawns) {

                                    double spawnX = (double) spawn[1];
                                    double spawnY = (double) spawn[2];
                                    double spawnZ = (double) spawn[3];
                                    double spawnPitch = (double) spawn[4];
                                    double spawnYaw = (double) spawn[5];
                                    String spawnWorld = (String) spawn[6];
                                    String spawnUID = (String) spawn[7];

                                    TNTRSpawn _spawn = new TNTRSpawn(new Location(Bukkit.getWorld(spawnWorld), spawnX, spawnY, spawnZ, new Double(spawnYaw).floatValue(), new Double(spawnPitch).floatValue()));
                                    _spawn.setID(spawnUID);
                                    jumparena.addSpawnLocation(_spawn);
                                }
                            }
                        }

                        if (!lobbyUID.equals("-1") || !lobbyUID.isEmpty()) {
                            Lobby lobby = LobbyUtils.getLobbyByUID(lobbyUID);
                            if (lobby != null) {
                                jumparena.setLobby(lobby);
                                lobby.addPlaySpace(jumparena);
                            }
                        }

                    }
                }
            }
        }
        db.disconnect();
    }

    public static void deleteAllArenas() {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        db.execute("DELETE FROM " + db.fixName("tntr"));
        db.execute("DELETE FROM " + db.fixName("tntr_spawn"));
        db.execute("DELETE FROM " + db.fixName("tntr_jump"));
        db.disconnect();
    }

    public static void deleteArena(String gameUID) {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        db.execute("DELETE FROM " + db.fixName("tntr") + " WHERE gameUID=?", gameUID);
        db.execute("DELETE FROM " + db.fixName("tntr_spawn") + " WHERE gameUID=?", gameUID);
        db.execute("DELETE FROM " + db.fixName("tntr_jump") + " WHERE gameUID=?", gameUID);

        db.disconnect();
    }

    public static boolean updateArenas(String gameUID, String table, String key, Object value) {
        boolean isUpdated = false;
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String selectSQL = "SELECT name, gameUID FROM " + db.fixName(table) + " WHERE gameUID=?";
        String updateSQL = "UPDATE " + db.fixName(table) + " SET " + key + "=? WHERE gameUID=?";
        List<Object[]> results = db.executeQuery(selectSQL, gameUID);
        if (results != null || !results.isEmpty()) {
            isUpdated = db.execute(updateSQL, value, gameUID);
        }
        db.connect();
        return isUpdated;
    }

    public static void saveArenas() {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("tntr") + " (name, gameUID, bombTimer, lobbyUID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, bombRatio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String selectSQL = "SELECT * FROM " + db.fixName("tntr") + " WHERE gameUID=?";

        PlaySpace.playSpaces.stream().filter(playSpace -> playSpace instanceof TNTRArena).forEach(playSpace -> {
            List<Object[]> results = db.executeQuery(selectSQL, playSpace.getUID());
            if (results == null || results.isEmpty()) {
                Location loc = playSpace.getLocation();
                int bombTimer = ((TNTRArena) playSpace).getBombTimer();
                db.execute(
                        insertSQL,
                        playSpace.getName(),
                        playSpace.getUID(),
                        bombTimer,
                        ((playSpace.getLobby() == null) ? "-1" : playSpace.getLobby().getUID()),
                        ((loc == null) ? -1 : loc.getX()),
                        ((loc == null) ? -1 : loc.getY()),
                        ((loc == null) ? -1 : loc.getZ()),
                        ((loc == null) ? -1 : loc.getPitch()),
                        ((loc == null) ? -1 : loc.getYaw()),
                        ((loc == null) ? "-1" : loc.getWorld().getName()),
                        ((TNTRArena) playSpace).getBombRatio()
                );
                TNTRArenaUtils.saveJumps(((TNTRArena) playSpace).getJumps(), playSpace.getUID());
                TNTRArenaUtils.saveSpawns(((TNTRArena) playSpace).getSpawns(), playSpace.getUID());
            }
        });
        db.disconnect();
    }

    public static void saveJumps(List<TNTRJump> jumps, String uid) {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("tntr_jump") + " (gameUID, strength, radius, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid, cooldown) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        for (TNTRJump jump : jumps) {
            Location loc = jump.getPos();
            db.execute(
                    insertSQL,
                    uid,
                    jump.getStrength(),
                    jump.getRadius(),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    loc.getPitch(),
                    loc.getYaw(),
                    loc.getWorld().getName(),
                    jump.getID(),
                    jump.getCooldown()
            );
        }
        db.disconnect();
    }

    public static void saveJump(TNTRJump jump, String uid) {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("tntr_jump") + " (gameUID, strength, radius, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid, cooldown) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Location loc = jump.getPos();
        db.execute(
                    insertSQL,
                    uid,
                    jump.getStrength(),
                    jump.getRadius(),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    loc.getPitch(),
                    loc.getYaw(),
                    loc.getWorld().getName(),
                    jump.getID(),
                    jump.getCooldown()
        );
        db.disconnect();
    }

    public static void saveSpawns(List<TNTRSpawn> spawns, String uid) {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("tntr_spawn") + " (gameUID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        for (TNTRSpawn spawn : spawns) {
            Location loc = spawn.getLocation();
            db.execute(
                    insertSQL,
                    uid,
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    loc.getPitch(),
                    loc.getYaw(),
                    loc.getWorld().getName(),
                    spawn.getID()
            );
        }
        db.disconnect();
    }

    public static void saveSpawn(TNTRSpawn spawn, String uid) {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("tntr_spawn") + " (gameUID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Location loc = spawn.getLocation();
        db.execute(
                insertSQL,
                uid,
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw(),
                loc.getWorld().getName(),
                spawn.getID()
        );
        db.disconnect();
    }

    public static void createTables() {
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        if (!db.tableExists(db.fixName("tntr")) || !db.tableExists(db.fixName("tntr_jump")) || !db.tableExists(db.fixName("tntr_spawn"))) {
            if (!db.isMysql()) {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("tntr") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR(255)," +
                        "gameUID VARCHAR(255)," +
                        "bombTimer INTEGER," +
                        "lobbyUID  VARCHAR(255)," +
                        "location_x DOUBLE," +
                        "location_y DOUBLE," +
                        "location_z DOUBLE," +
                        "location_pitch DOUBLE," +
                        "location_yaw DOUBLE," +
                        "location_world VARCHAR(255)," +
                        "bombRatio INTEGER," +
                        "UNIQUE (id)" +
                        ");");

                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("tntr_jump") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "gameUID VARCHAR(255)," +
                        "radius DOUBLE," +
                        "strength DOUBLE," +
                        "location_x DOUBLE," +
                        "location_y DOUBLE," +
                        "location_z DOUBLE," +
                        "location_pitch DOUBLE," +
                        "location_yaw DOUBLE," +
                        "location_world VARCHAR(255)," +
                        "uid VARCHAR(255)," +
                        "cooldown INTEGER," +
                        "UNIQUE (id)" +
                        ");");

                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("tntr_spawn") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "gameUID VARCHAR(255)," +
                        "location_x DOUBLE," +
                        "location_y DOUBLE," +
                        "location_z DOUBLE," +
                        "location_pitch DOUBLE," +
                        "location_yaw DOUBLE," +
                        "location_world VARCHAR(255)," +
                        "uid VARCHAR(255)," +
                        "UNIQUE (id)" +
                        ");");

            } else {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("tntr") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `name` varchar(255) NOT NULL," +
                        "  `gameUID` varchar(255) NULL," +
                        "  `bombTimer` int(255) NOT NULL," +
                        "  `lobbyUID` varchar(255) NULL," +
                        "  `location_x` double NOT NULL," +
                        "  `location_y` double NOT NULL," +
                        "  `location_z` double NOT NULL," +
                        "  `location_pitch` double NOT NULL," +
                        "  `location_yaw` double NOT NULL," +
                        "  `location_world` varchar(255) NOT NULL," +
                        "  `bombRatio` int(255) NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ");");

                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("tntr_jump") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `gameUID` varchar(255) NULL," +
                        "  `strength` double NOT NULL,`" +
                        "  `radius` double NOT NULL," +
                        "  `location_x` double NOT NULL," +
                        "  `location_y` double NOT NULL," +
                        "  `location_z` double NOT NULL," +
                        "  `location_pitch` double NOT NULL," +
                        "  `location_yaw` double NOT NULL," +
                        "  `location_world` varchar(255) NOT NULL," +
                        "  `uid` varchar(255) NOT NULL," +
                        "  `cooldown` int(255) NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ");");

                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("tntr_spawn") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `gameUID` varchar(255) NULL," +
                        "  `location_x` double NOT NULL," +
                        "  `location_y` double NOT NULL," +
                        "  `location_z` double NOT NULL," +
                        "  `location_pitch` double NOT NULL," +
                        "  `location_yaw` double NOT NULL," +
                        "  `location_world` varchar(255) NOT NULL," +
                        "  `uid` varchar(255) NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ");");

                db.execute("ALTER TABLE " + db.fixName("tntr") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;");
                db.execute("ALTER TABLE " + db.fixName("tntr_jump") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;");
                db.execute("ALTER TABLE " + db.fixName("tntr_spawn") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;");
            }
        }
        db.disconnect();
    }

    public static void delSpawn(String uid) {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        db.execute("DELETE FROM " + db.fixName("tntr_spawn") + " WHERE uid=?", uid);

        db.disconnect();

    }
    public static void delJump(String uid) {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        db.execute("DELETE FROM " + db.fixName("tntr_jump") + " WHERE uid=?", uid);

        db.disconnect();

    }

    public static boolean updateLocation(String uid, Location loc) {
        boolean isUpdated = false;
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String selectSQL = "SELECT gameUID FROM " + db.fixName("tntr") + " WHERE gameUID=?";
        List<Object[]> results = db.executeQuery(selectSQL, uid);
        if (results != null || !results.isEmpty()) {
            String updateLocationSQL = "UPDATE " + db.fixName("tntr") + " SET location_x=?, location_y=?, location_z=?, location_pitch=?, location_yaw=?, location_world=?  WHERE gameUID=?";
            isUpdated = db.execute(
                    updateLocationSQL,
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    loc.getPitch(),
                    loc.getYaw(),
                    loc.getWorld().getName(),
                    uid
            );
        }
        db.connect();
        return isUpdated;
    }

    public static boolean updateGameLobby(String uid, Lobby lobby) {
        boolean isUpdated = false;
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String selectSQL = "SELECT gameUID FROM " + db.fixName("tntr") + " WHERE gameUID=?";
        List<Object[]> results = db.executeQuery(selectSQL, uid);
        if (results != null || !results.isEmpty()) {
            String updateLocationSQL = "UPDATE " + db.fixName("tntr") + " SET lobbyUID=? WHERE gameUID=?";
            isUpdated = db.execute(
                    updateLocationSQL,
                    (lobby != null ? lobby.getUID() : ""),
                    uid
            );
        }
        db.connect();
        return isUpdated;
    }

    public static void saveArena(TNTRArena playSpace) {
        TNTRArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("tntr") + " (name, gameUID, bombTimer, lobbyUID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, bombRatio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String selectSQL = "SELECT * FROM " + db.fixName("tntr") + " WHERE gameUID=?";


        List<Object[]> results = db.executeQuery(selectSQL, playSpace.getUID());
        if (results == null || results.isEmpty()) {
            Location loc = playSpace.getLocation();
            int bombTimer = playSpace.getBombTimer();
            db.execute(
                    insertSQL,
                    playSpace.getName(),
                    playSpace.getUID(),
                    bombTimer,
                    ((playSpace.getLobby() == null) ? "-1" : playSpace.getLobby().getUID()),
                    ((loc == null) ? -1 : loc.getX()),
                    ((loc == null) ? -1 : loc.getY()),
                    ((loc == null) ? -1 : loc.getZ()),
                    ((loc == null) ? -1 : loc.getPitch()),
                    ((loc == null) ? -1 : loc.getYaw()),
                    ((loc == null) ? "-1" : loc.getWorld().getName()),
                    playSpace.getBombRatio()
                );
            TNTRArenaUtils.saveJumps(((TNTRArena) playSpace).getJumps(), playSpace.getUID());
            TNTRArenaUtils.saveSpawns(((TNTRArena) playSpace).getSpawns(), playSpace.getUID());
        }
        db.disconnect();
    }
}