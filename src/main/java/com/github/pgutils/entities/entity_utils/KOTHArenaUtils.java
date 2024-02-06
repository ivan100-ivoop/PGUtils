package com.github.pgutils.entities.entity_utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import com.github.pgutils.utils.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class KOTHArenaUtils {

    public static void loadArenas() {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        String arenaSelectSQL = "SELECT name, gameUID, teams_amount, location_x, location_y, location_z, location_pitch, location_yaw, location_world, lobbyUID, match_time, initial_points_active FROM " + db.fixName("koth");
        String spawnSelectSQL = "SELECT gameUID, teamID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid FROM " + db.fixName("koth_spawn") + " WHERE gameUID=?";
        String pointsSelectSQL = "SELECT gameUID, radius, capture_time, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid FROM " + db.fixName("koth_point") + " WHERE gameUID=?";

        if (db.tableExists(db.fixName("koth"))) {
            List<Object[]> results = db.executeQuery(arenaSelectSQL);

            if (results != null) {
                for (Object[] data : results) {
                    String name = (String) data[0]; //name
                    String UID = (String) data[1]; //gameUID
                    int teams_amount = (int) data[2]; //teams_amount

                    double x = (double) data[3]; //location_x
                    if (x != -1) {
                        double y = (double) data[4]; //location_y
                        double z = (double) data[5]; //location_z
                        double pitch = (double) data[6]; //location_pitch
                        double yaw = (double) data[7]; //location_yaw
                        String world = (String) data[8]; //location_world
                        String lobbyUID = (String) data[9]; //lobbyUID
                        int match_time = (int) data[10]; //match_time
                        int initial_points_active = (int) data[11]; //initial_points_active

                        KOTHArena kotharena = new KOTHArena();

                        kotharena.setPos(new Location(Bukkit.getWorld(world), x, y, z, new Double(yaw).floatValue(), new Double(pitch).floatValue()));
                        kotharena.setUID(UID);
                        kotharena.setName(name);
                        kotharena.setTeamsAmount(teams_amount);
                        kotharena.setMatchTime(match_time);
                        kotharena.setInitialPointsActive(initial_points_active);

                        if (db.tableExists(db.fixName("koth_point"))) {
                            List<Object[]> points = db.executeQuery(pointsSelectSQL, UID);

                            if (points != null) {
                                for (Object[] point : points) {
                                    double radius = (double) point[1];
                                    int capture_time = (int) point[2];

                                    double pointX = (double) point[3];
                                    double pointY = (double) point[4];
                                    double pointZ = (double) point[5];
                                    double pointPitch = (double) point[6];
                                    double pointYaw = (double) point[7];
                                    String pointWorld = (String) point[8];
                                    String pointUID = (String) point[9];

                                    KOTHPoint _point = new KOTHPoint();
                                    _point.setID(pointUID);
                                    _point.setRadius(radius);
                                    _point.setCaptureTime(capture_time);
                                    _point.setLocation(new Location(Bukkit.getWorld(pointWorld), pointX, pointY, pointZ, new Double(pointYaw).floatValue(), new Double(pointPitch).floatValue()));
                                    _point.setArena(kotharena);
                                    kotharena.addCapturePoint(_point);
                                    _point.setup();
                                }
                            }
                        }

                        if (db.tableExists(db.fixName("koth_spawn"))) {
                            List<Object[]> spawns = db.executeQuery(spawnSelectSQL, UID);

                            if (spawns != null) {
                                for (Object[] spawn : spawns) {
                                    int teamID = (int) spawn[1];

                                    double spawnX = (double) spawn[2];
                                    double spawnY = (double) spawn[3];
                                    double spawnZ = (double) spawn[4];
                                    double spawnPitch = (double) spawn[5];
                                    double spawnYaw = (double) spawn[6];
                                    String spawnWorld = (String) spawn[7];
                                    String spawnUID = (String) spawn[8];


                                    KOTHSpawn _spawn = new KOTHSpawn();
                                    _spawn.setID(spawnUID);
                                    _spawn.setTeamID(teamID);
                                    _spawn.setArena(kotharena);
                                    _spawn.setPos(new Location(Bukkit.getWorld(spawnWorld), spawnX, spawnY, spawnZ, new Double(spawnYaw).floatValue(), new Double(spawnPitch).floatValue()));
                                    kotharena.addSpawn(_spawn);
                                }
                            }
                        }

                        if (!lobbyUID.equals("-1") || !lobbyUID.isEmpty()) {
                            Lobby lobby = LobbyUtils.getLobbyByUID(lobbyUID);
                            if (lobby != null) {
                                kotharena.setLobby(lobby);
                                lobby.addPlaySpace(kotharena);
                            }
                        }

                    }
                }
            }
        }
        db.disconnect();
    }

    public static void deleteAllArenas() {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        db.execute("DELETE FROM " + db.fixName("koth"));
        db.execute("DELETE FROM " + db.fixName("koth_spawn"));
        db.execute("DELETE FROM " + db.fixName("koth_point"));
        db.disconnect();
    }

    public static void deleteArena(String gameUID) {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        db.execute("DELETE FROM " + db.fixName("koth") + " WHERE gameUID=?", gameUID);
        db.execute("DELETE FROM " + db.fixName("koth_spawn") + " WHERE gameUID=?", gameUID);
        db.execute("DELETE FROM " + db.fixName("koth_point") + " WHERE gameUID=?", gameUID);

        db.disconnect();
    }

    public static boolean updateArenas(String gameUID, String table, String key, Object value) {
        boolean isUpdated = false;
        KOTHArenaUtils.createTables();
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
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("koth") + " (name, gameUID, lobbyUID, teams_amount, location_x, location_y, location_z, location_pitch, location_yaw, location_world, match_time, initial_points_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String selectSQL = "SELECT * FROM " + db.fixName("koth") + " WHERE gameUID=?";

        PlaySpace.playSpaces.stream().filter(playSpace -> playSpace instanceof KOTHArena).forEach(playSpace -> {
            List<Object[]> results = db.executeQuery(selectSQL, playSpace.getUID());
            if (results == null || results.isEmpty()) {
                Location loc = playSpace.getLocation();
                int TeamAmount = ((KOTHArena) playSpace).getTeamsAmount();
                db.execute(
                        insertSQL,
                        playSpace.getName(),
                        playSpace.getUID(),
                        ((playSpace.getLobby() == null) ? "-1" : playSpace.getLobby().getUID()),
                        TeamAmount,
                        ((loc == null) ? -1 : loc.getX()),
                        ((loc == null) ? -1 : loc.getY()),
                        ((loc == null) ? -1 : loc.getZ()),
                        ((loc == null) ? -1 : loc.getPitch()),
                        ((loc == null) ? -1 : loc.getYaw()),
                        ((loc == null) ? "-1" : loc.getWorld().getName()),
                        ((KOTHArena) playSpace).getMatchTime(),
                        ((KOTHArena) playSpace).getInitialPointsActive()
                );
                KOTHArenaUtils.savePoints(((KOTHArena) playSpace).getPoints(), playSpace.getUID());
                KOTHArenaUtils.saveSpawns(((KOTHArena) playSpace).getSpawns(), playSpace.getUID());
            }
        });
        db.disconnect();
    }

    public static void savePoints(List<KOTHPoint> points, String uid) {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("koth_point") + " (gameUID, UUID, radius, capture_time, points_awarding, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        for (KOTHPoint point : points) {
            Location loc = point.getLocation();
            db.execute(
                    insertSQL,
                    uid,
                    point.getRadius(),
                    point.getCaptureTime(),
                    point.getPointsAwarding(),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    loc.getPitch(),
                    loc.getYaw(),
                    loc.getWorld().getName(),
                    point.getID()
            );
        }
        db.disconnect();
    }

    public static void savePoint(KOTHPoint point, String uid) {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("koth_point") + " (gameUID, radius, capture_time, points_awarding, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Location loc = point.getLocation();
        db.execute(
                insertSQL,
                uid,
                point.getRadius(),
                point.getCaptureTime(),
                point.getPointsAwarding(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw(),
                loc.getWorld().getName(),
                point.getID()
        );
        db.disconnect();
    }

    public static void saveSpawns(List<KOTHSpawn> spawns, String uid) {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("koth_spawn") + " (gameUID, teamID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        for (KOTHSpawn spawn : spawns) {
            Location loc = spawn.getLocation();
            db.execute(
                    insertSQL,
                    uid,
                    spawn.getTeamID(),
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

    public static void saveSpawn(KOTHSpawn spawn, String uid) {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("koth_spawn") + " (gameUID, teamID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Location loc = spawn.getLocation();
        db.execute(
                insertSQL,
                uid,
                spawn.getTeamID(),
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

        if (!db.tableExists(db.fixName("koth")) || !db.tableExists(db.fixName("koth_point")) || !db.tableExists(db.fixName("koth_spawn"))) {
            if (!db.isMysql()) {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("koth") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR(255)," +
                        "gameUID VARCHAR(255)," +
                        "lobbyUID  VARCHAR(255)," +
                        "teams_amount INTEGER," +
                        "location_x DOUBLE," +
                        "location_y DOUBLE," +
                        "location_z DOUBLE," +
                        "location_pitch DOUBLE," +
                        "location_yaw DOUBLE," +
                        "location_world VARCHAR(255)," +
                        "match_time INTEGER," +
                        "initial_points_active INTEGER," +
                        "UNIQUE (id)" +
                        ");");

                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("koth_point") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "gameUID VARCHAR(255)," +
                        "radius DOUBLE," +
                        "capture_time INTEGER," +
                        "points_awarding INTEGER," +
                        "location_x DOUBLE," +
                        "location_y DOUBLE," +
                        "location_z DOUBLE," +
                        "location_pitch DOUBLE," +
                        "location_yaw DOUBLE," +
                        "location_world VARCHAR(255)," +
                        "uid VARCHAR(255)," +
                        "UNIQUE (id)" +
                        ");");

                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("koth_spawn") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "gameUID VARCHAR(255)," +
                        "teamID INTEGER," +
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
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("koth") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `name` varchar(255) NOT NULL," +
                        "  `gameUID` varchar(255) NULL," +
                        "  `lobbyUID` varchar(255) NULL," +
                        "  `teams_amount` int(255) NOT NULL," +
                        "  `location_x` double NOT NULL," +
                        "  `location_y` double NOT NULL," +
                        "  `location_z` double NOT NULL," +
                        "  `location_pitch` double NOT NULL," +
                        "  `location_yaw` double NOT NULL," +
                        "  `location_world` varchar(255) NOT NULL," +
                        "  'match_time' int(255) NOT NULL," +
                        "  'initial_points_active' int(255) NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ");");

                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("koth_point") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `gameUID` varchar(255) NULL," +
                        "  `radius` double NOT NULL," +
                        "  `capture_time` int(255) NOT NULL," +
                        "  `points_awarding` int(255) NOT NULL," +
                        "  `location_x` double NOT NULL," +
                        "  `location_y` double NOT NULL," +
                        "  `location_z` double NOT NULL," +
                        "  `location_pitch` double NOT NULL," +
                        "  `location_yaw` double NOT NULL," +
                        "  `location_world` varchar(255) NOT NULL," +
                        "  `uid` varchar(255) NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ");");

                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("koth_spawn") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `gameUID` varchar(255) NULL," +
                        "  `teamID` int(255) NOT NULL," +
                        "  `location_x` double NOT NULL," +
                        "  `location_y` double NOT NULL," +
                        "  `location_z` double NOT NULL," +
                        "  `location_pitch` double NOT NULL," +
                        "  `location_yaw` double NOT NULL," +
                        "  `location_world` varchar(255) NOT NULL," +
                        "  `uid` varchar(255) NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ");");

                db.execute("ALTER TABLE " + db.fixName("koth") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;");
                db.execute("ALTER TABLE " + db.fixName("koth_point") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;");
                db.execute("ALTER TABLE " + db.fixName("koth_spawn") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;");
            }
        }
        db.disconnect();
    }

    public static void delSpawn(String uid) {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        db.execute("DELETE FROM " + db.fixName("koth_spawn") + " WHERE uid=?", uid);

        db.disconnect();

    }
    public static void delPoint(String uid) {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();

        db.execute("DELETE FROM " + db.fixName("koth_point") + " WHERE uid=?", uid);

        db.disconnect();

    }

    public static boolean updateLocation(String uid, Location loc) {
        boolean isUpdated = false;
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String selectSQL = "SELECT gameUID FROM " + db.fixName("koth") + " WHERE gameUID=?";
        List<Object[]> results = db.executeQuery(selectSQL, uid);
        if (results != null || !results.isEmpty()) {
            String updateLocationSQL = "UPDATE " + db.fixName("koth") + " SET location_x=?, location_y=?, location_z=?, location_pitch=?, location_yaw=?, location_world=?  WHERE gameUID=?";
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
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String selectSQL = "SELECT gameUID FROM " + db.fixName("koth") + " WHERE gameUID=?";
        List<Object[]> results = db.executeQuery(selectSQL, uid);
        if (results != null || !results.isEmpty()) {
            String updateLocationSQL = "UPDATE " + db.fixName("koth") + " SET lobbyUID=? WHERE gameUID=?";
            isUpdated = db.execute(
                    updateLocationSQL,
                    (lobby != null ? lobby.getUID() : ""),
                    uid
            );
        }
        db.connect();
        return isUpdated;
    }

    public static void saveArena(KOTHArena playSpace) {
        KOTHArenaUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).loader.sqlDB;
        db.connect();
        String insertSQL = "INSERT INTO " + db.fixName("koth") + " (name, gameUID, lobbyUID, teams_amount, location_x, location_y, location_z, location_pitch, location_yaw, location_world, match_time, initial_points_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String selectSQL = "SELECT * FROM " + db.fixName("koth") + " WHERE gameUID=?";


        List<Object[]> results = db.executeQuery(selectSQL, playSpace.getUID());
        if (results == null || results.isEmpty()) {
            Location loc = playSpace.getLocation();
            int TeamAmount = ((KOTHArena) playSpace).getTeamsAmount();
            db.execute(
                    insertSQL,
                    playSpace.getName(),
                    playSpace.getUID(),
                    ((playSpace.getLobby() == null) ? "-1" : playSpace.getLobby().getUID()),
                    TeamAmount,
                    ((loc == null) ? -1 : loc.getX()),
                    ((loc == null) ? -1 : loc.getY()),
                    ((loc == null) ? -1 : loc.getZ()),
                    ((loc == null) ? -1 : loc.getPitch()),
                    ((loc == null) ? -1 : loc.getYaw()),
                    ((loc == null) ? "-1" : loc.getWorld().getName()),
                    playSpace.getMatchTime(),
                    playSpace.getInitialPointsActive()
            );
            KOTHArenaUtils.savePoints(playSpace.getPoints(), playSpace.getUID());
            KOTHArenaUtils.saveSpawns(playSpace.getSpawns(), playSpace.getUID());
        }

        db.disconnect();
    }
}