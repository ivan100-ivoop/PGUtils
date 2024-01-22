package com.github.pgutils.entities.entity_utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.enums.LobbyMode;
import com.github.pgutils.utils.DatabaseManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class LobbyUtils {
    public static void saveLobby(Lobby lobby, String fileName) {
        LobbyUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;

        String serializedSlotMachine;
        try{
            ByteArrayOutputStream biteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(biteArrayOutputStream);
            bukkitObjectOutputStream.writeObject(lobby.getLocation());
            bukkitObjectOutputStream.writeObject(lobby.getMode());
            bukkitObjectOutputStream.writeObject(lobby.getMinPlayers());
            bukkitObjectOutputStream.writeObject(lobby.getMaxPlayers());
            bukkitObjectOutputStream.writeObject(lobby.isLocked());
            bukkitObjectOutputStream.writeObject(lobby.getUID());
            bukkitObjectOutputStream.writeObject(lobby.getName());
            bukkitObjectOutputStream.flush();

            byte[] serializedSlotMachineBytes = biteArrayOutputStream.toByteArray();
            serializedSlotMachine = Base64.getEncoder().encodeToString(serializedSlotMachineBytes);
            db.connect();
            db.execute("INSERT INTO " + db.fixName("lobby") + " ( data, name ) VALUES (?, ?)", serializedSlotMachine, fileName);
            db.disconnect();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteAllLobbies() {
        LobbyUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();
        db.execute("DELETE FROM " + db.fixName("lobby"));
        db.disconnect();
    }

    public static void loadLobbies() {
        LobbyUtils.createTables();
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();
        try{
            if (db.tableExists(db.fixName("lobby"))) {
                String selectAllQuery = "SELECT data FROM " + db.fixName("lobby");
                List<Object[]> results = db.executeQuery(selectAllQuery);

                if (results != null) {
                    for (Object[] data : results) {
                        String serializedSlotMachine = (String) data[0];

                        byte[] deserializedSlotMachine = Base64.getDecoder().decode(serializedSlotMachine);

                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(deserializedSlotMachine);
                        BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

                        Lobby lobby = new Lobby();

                        lobby.setPos((Location) bukkitObjectInputStream.readObject());
                        lobby.setMode((LobbyMode) bukkitObjectInputStream.readObject());
                        lobby.setMinPlayers((int) bukkitObjectInputStream.readObject());
                        lobby.setMaxPlayers((int) bukkitObjectInputStream.readObject());
                        lobby.setLocked((boolean) bukkitObjectInputStream.readObject());
                        lobby.setUID((String) bukkitObjectInputStream.readObject());
                        lobby.setName((String) bukkitObjectInputStream.readObject());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        db.disconnect();
    }

    public static void saveLobbies() {
        LobbyUtils.deleteAllLobbies();
        for (int i = Lobby.lobbies.size() - 1; i >= 0; i--) {
            Lobby lobby = Lobby.lobbies.get(i);
            saveLobby(lobby, lobby.getUID());
        }
    }

    public static Lobby getLobbyByUID(String lobbyUID) {
        return Lobby.lobbies.stream().filter(lobby -> lobby.getUID().equals(lobbyUID)).findFirst().orElse(null);
    }

    public static void createTables(){
        DatabaseManager db = PGUtils.getPlugin(PGUtils.class).sqlDB;
        db.connect();
        if (!db.tableExists(db.fixName("lobby"))) {
            if(!db.isMysql()) {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("lobby") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR (255)," +
                        "data TEXT," +
                        "UNIQUE (id)" +
                        ");");
            } else {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("lobby") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `name` varchar(255) NOT NULL," +
                        "  `data` text NOT NULL," +
                        ");");
                db.execute("ALTER TABLE " + db.fixName("lobby") + " ADD PRIMARY KEY (`id`);");
                db.execute("ALTER TABLE " + db.fixName("lobby") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;");

            }
        }
        db.disconnect();
    }

}
