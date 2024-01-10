package com.github.pgutils.entities.entity_utils;

import com.github.pgutils.entities.Lobby;
import com.github.pgutils.enums.LobbyMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;
import java.util.UUID;

public class LobbyUtils {
    public static void saveLobby(Lobby lobby, String fileName) {
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
            bukkitObjectOutputStream.flush();

            byte[] serializedSlotMachineBytes = biteArrayOutputStream.toByteArray();
            serializedSlotMachine = Base64.getEncoder().encodeToString(serializedSlotMachineBytes);

            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(serializedSlotMachine);
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Lobby loadLobby(String fileName) {
        try  {
            File file = new File(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String serializedSlotMachine = bufferedReader.readLine();

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

            return lobby;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static void deleteAllLobbies() {
        String directoryPath = "plugins/PGUtils/saves/lobby/";

        if(!new File(directoryPath).exists()) {
            return;
        }

        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        if (directory.exists() && directory.isDirectory()) {

            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".ser")) {
                    file.delete();
                }
            }
        } else {
            System.out.println("Invalid directory path or the path is not a directory.");
        }
    }

    public static void loadLobbies() {
        String directoryPath = "plugins/PGUtils/saves/lobby/";

        if(!new File(directoryPath).exists()) {
            new File(directoryPath).mkdirs();
        }
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (directory.exists() && directory.isDirectory()) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".ser")) {
                    Lobby loadedLobby = loadLobby(file.getAbsolutePath());
                    if (loadedLobby==null)
                        file.delete();

                }
            }
        } else {
            System.out.println("Invalid directory path or the path is not a directory.");
        }
    }

    public static void saveLobbies() {
        Lobby.lobbies.forEach(lobby -> {
            saveLobby(lobby, "plugins/PGUtils/saves/lobby/" + lobby.getID() + ".ser");
        });
    }

    public static Lobby getLobbyByUID(String lobbyUID) {
        return Lobby.lobbies.stream().filter(lobby -> lobby.getUID().equals(lobbyUID)).findFirst().orElse(null);
    }
}
