package com.github.pgutils.entities.entity_utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.enums.LobbyMode;
import com.github.pgutils.utils.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class LobbyUtils {

    public static void deleteAllLobbies() {
        LobbyUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();
        db.execute("DELETE FROM " + db.fixName("lobby"));
        db.disconnect();
    }

    public static void deleteLobby(String lobbyUID) {
        LobbyUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();
        db.execute("DELETE FROM " + db.fixName("lobby") + " WHERE lobbyUID=?", lobbyUID);
        db.disconnect();
    }

    public static void loadLobbies() {
        LobbyUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();
        if (db.tableExists(db.fixName("lobby"))) {
            String selectAllQuery = "SELECT name, lobbyUID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, max_players, min_players, mode, locked FROM " + db.fixName("lobby");
            List<Object[]> results = db.executeQuery(selectAllQuery);

            if (results != null) {
                for (Object[] data : results) {
                    String name = (String) data[0];
                    String UID = (String) data[1];

                    double x = (double) data[2];
                    double y = (double) data[3];
                    double z = (double) data[4];
                    double pitch = (double) data[5];
                    double yaw = (double) data[6];
                    String world = (String) data[7];

                    int max_players = (int) data[8];
                    int min_players = (int) data[9];

                    String mode = ((String) data[10]).toUpperCase();
                    boolean isLocked = ((int) data[11] == 1);

                    Lobby lobby = new Lobby();
                    lobby.setPos(new Location(Bukkit.getWorld(world), x, y, z, new Double(yaw).floatValue(), new Double(pitch).floatValue()));
                    lobby.setMode(LobbyMode.valueOf(mode));
                    lobby.setMinPlayers(min_players);
                    lobby.setMaxPlayers(max_players);
                    lobby.setLocked(isLocked);
                    lobby.setUID(UID);
                    lobby.setName(name);
                }
            }
        }
        db.disconnect();
    }

    public static boolean updateLobby(String key, Object value, String lobbyUID) {
        boolean isUpdated = false;
        LobbyUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();
        String selectSQL = "SELECT name, lobbyUID FROM " + db.fixName("lobby") + " WHERE lobbyUID=?";
        String updateSQL = "UPDATE " + db.fixName("lobby") + " SET " + key + "=? WHERE lobbyUID=?";
        List<Object[]> results = db.executeQuery(selectSQL, lobbyUID);
        if (results != null || !results.isEmpty()) {
            isUpdated = db.execute(updateSQL, value, lobbyUID);
        }
        db.connect();
        return isUpdated;
    }

    public static void saveLobbies() {
        LobbyUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();

        String insertSQL = "INSERT INTO " + db.fixName("lobby") + " (name, lobbyUID, location_x, location_y, location_z, location_pitch, location_yaw, location_world, max_players, min_players, mode, locked) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String selectSQL = "SELECT name, lobbyUID FROM " + db.fixName("lobby") + " WHERE lobbyUID=?";

        for (int i = Lobby.lobbies.size() - 1; i >= 0; i--) {
            Lobby lobby = Lobby.lobbies.get(i);
            List<Object[]> results = db.executeQuery(selectSQL, lobby.getUID());
            if (results == null || results.isEmpty()) {
                Location loc = lobby.getLocation();
                db.execute(
                        insertSQL,
                        lobby.getName(),
                        lobby.getUID(),
                        loc.getX(),
                        loc.getY(),
                        loc.getZ(),
                        loc.getPitch(),
                        loc.getYaw(),
                        loc.getWorld().getName(),
                        lobby.getMaxPlayers(),
                        lobby.getMinPlayers(),
                        lobby.getMode().toString(),
                        lobby.isLocked()
                );
            }
        }


        db.disconnect();
    }

    public static Lobby getLobbyByUID(String lobbyUID) {
        return Lobby.lobbies.stream().filter(lobby -> lobby.getUID().equals(lobbyUID)).findFirst().orElse(null);
    }

    public static void createTables() {
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();

        if (!db.tableExists(db.fixName("lobby"))) {
            if (!db.isMysql()) {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("lobby") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR(255)," +
                        "lobbyUID VARCHAR(255)," +
                        "location_x DOUBLE," +
                        "location_y DOUBLE," +
                        "location_z DOUBLE," +
                        "location_pitch DOUBLE," +
                        "location_yaw DOUBLE," +
                        "location_world VARCHAR(255)," +
                        "max_players INTEGER," +
                        "min_players INTEGER," +
                        "mode VARCHAR(255)," +
                        "locked BOOLEAN," +
                        "UNIQUE (id)" +
                        ");");
            } else {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("lobby") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `name` varchar(255) NOT NULL," +
                        "  `lobbyUID` varchar(255) NULL," +
                        "  `location_x` double NOT NULL," +
                        "  `location_y` double NOT NULL," +
                        "  `location_z` double NOT NULL," +
                        "  `location_pitch` double NOT NULL," +
                        "  `location_yaw` double NOT NULL," +
                        "  `location_world` varchar(255) NOT NULL," +
                        "  `max_players` int(11) NOT NULL," +
                        "  `min_players` int(11) NOT NULL," +
                        "  `mode` varchar(255) NOT NULL," +
                        "  `locked` bool NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ");");
                db.execute("ALTER TABLE " + db.fixName("lobby") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;");
            }
        }
        db.disconnect();
    }
}
