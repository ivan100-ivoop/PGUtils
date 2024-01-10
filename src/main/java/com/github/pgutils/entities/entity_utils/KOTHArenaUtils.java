package com.github.pgutils.entities.entity_utils;

import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;

public class KOTHArenaUtils {
    public static void saveKOTHArena(KOTHArena kotharena, String fileName) {
        String serializedSlotMachine;
        try{
            ByteArrayOutputStream biteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(biteArrayOutputStream);

            bukkitObjectOutputStream.writeObject(kotharena.getLocation());
            bukkitObjectOutputStream.writeObject(kotharena.getUID());
            bukkitObjectOutputStream.writeObject(kotharena.getTeamsAmount());
            bukkitObjectOutputStream.writeObject(kotharena.getLobby().getUID());

            File directory = new File(fileName + kotharena.getUID());
            if (!directory.exists()) {
                directory.mkdirs();
            }

            for (int i = 0; i < kotharena.getPoints().size(); i++) {
                saveKOTHPoint(kotharena.getPoints().get(i), fileName + kotharena.getUID() + "/Point"+i+".ser");
            }
            for (int i = 0; i < kotharena.getSpawns().size(); i++) {
                saveKOTHSpawn(kotharena.getSpawns().get(i), fileName + kotharena.getUID() + "/Spawn"+i+".ser");
            }
            bukkitObjectOutputStream.flush();

            byte[] serializedSlotMachineBytes = biteArrayOutputStream.toByteArray();
            serializedSlotMachine = Base64.getEncoder().encodeToString(serializedSlotMachineBytes);

            FileWriter fileWriter = new FileWriter(fileName+kotharena.getUID()+".ser");
            fileWriter.write(serializedSlotMachine);
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static KOTHArena loadKOTHArena(String fileName) {
        try  {
            File file = new File(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String serializedSlotMachine = bufferedReader.readLine();

            byte[] deserializedSlotMachine = Base64.getDecoder().decode(serializedSlotMachine);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(deserializedSlotMachine);
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

            KOTHArena kotharena = new KOTHArena();

            kotharena.setPos((Location) bukkitObjectInputStream.readObject());
            kotharena.setUID((String) bukkitObjectInputStream.readObject());
            kotharena.setTeamsAmount((int) bukkitObjectInputStream.readObject());
            String lobbyUID = (String) bukkitObjectInputStream.readObject();
            Lobby lobby = LobbyUtils.getLobbyByUID(lobbyUID);
            if(lobby != null) {
                kotharena.setLobby(lobby);
                lobby.addPlaySpace(kotharena);
            }


            String directoryPath = "plugins/PGUtils/saves/kotharena/" + kotharena.getUID() + "/";
            File directory = new File(directoryPath);
            File[] files = directory.listFiles();
            if (directory.exists() && directory.isDirectory()) {
                for (File file1 : files) {
                    if (file1.isFile() && file1.getName().startsWith("Point") && file1.getName().endsWith(".ser")) {
                        KOTHPoint loadedKOTHPoint = loadKOTHPoint(file1.getAbsolutePath(), kotharena);
                        if (loadedKOTHPoint==null)
                            file1.delete();
                        else
                            kotharena.addCapturePoint(loadedKOTHPoint);
                    }
                }
            } else {
                System.out.println("Invalid directory path or the path is not a directory. : " + directoryPath);
            }

            File directory2 = new File(directoryPath);
            File[] files2 = directory2.listFiles();
            if (directory2.exists() && directory2.isDirectory()) {
                for (File file1 : files2) {
                    if (file1.isFile() && file1.getName().startsWith("Spawn") && file1.getName().endsWith(".ser")) {
                        KOTHSpawn loadedKOTHSpawn = loadKOTHSpawn(file1.getAbsolutePath(), kotharena);
                        if (loadedKOTHSpawn==null)
                            file1.delete();
                        else
                            kotharena.addSpawn(loadedKOTHSpawn);
                    }
                }
            } else {
                System.out.println("Invalid directory path or the path is not a directory. : " + directoryPath);
            }

            return kotharena;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteAllArenas() {
        String directoryPath = "plugins/PGUtils/saves/kotharena/";

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

    public static void loadArenas() {
        String directoryPath = "plugins/PGUtils/saves/kotharena/";

        if(!new File(directoryPath).exists()) {
            new File(directoryPath).mkdirs();
        }
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (directory.exists() && directory.isDirectory()) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".ser")) {
                    KOTHArena loadedKOTHArena = loadKOTHArena(file.getAbsolutePath());
                    if (loadedKOTHArena==null)
                        file.delete();

                }
            }
        } else {
            System.out.println("Invalid directory path or the path is not a directory.");
        }
    }

    public static KOTHPoint loadKOTHPoint(String fileName, KOTHArena kothArena) {
        try  {
            File file = new File(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String serializedSlotMachine = bufferedReader.readLine();

            byte[] deserializedSlotMachine = Base64.getDecoder().decode(serializedSlotMachine);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(deserializedSlotMachine);
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

            KOTHPoint kothPoint = new KOTHPoint();

            kothPoint.setLocation((Location) bukkitObjectInputStream.readObject());
            kothPoint.setRadius((double) bukkitObjectInputStream.readObject());
            kothPoint.setCaptureTime((int) bukkitObjectInputStream.readObject());
            kothPoint.setPointsAwarding((int) bukkitObjectInputStream.readObject());
            kothPoint.setArena(kothArena);

            kothPoint.setup();

            return kothPoint;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static KOTHSpawn loadKOTHSpawn(String fileName, KOTHArena kothArena) {
        try  {
            File file = new File(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String serializedSlotMachine = bufferedReader.readLine();

            byte[] deserializedSlotMachine = Base64.getDecoder().decode(serializedSlotMachine);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(deserializedSlotMachine);
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

            KOTHSpawn kothSpawn = new KOTHSpawn();

            kothSpawn.setPos((Location) bukkitObjectInputStream.readObject());
            kothSpawn.setTeamID((int) bukkitObjectInputStream.readObject());
            kothSpawn.setArena(kothArena);

            return kothSpawn;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void saveArenas() {
        PlaySpace.playSpaces.stream().filter(playSpace -> playSpace instanceof KOTHArena).forEach(playSpace -> {
            saveKOTHArena((KOTHArena) playSpace, "plugins/PGUtils/saves/kotharena/");
        });
    }

    public static void saveKOTHPoint(KOTHPoint kothPoint, String fileName) {
        String serializedSlotMachine;
        try{
            ByteArrayOutputStream biteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(biteArrayOutputStream);

            bukkitObjectOutputStream.writeObject(kothPoint.getLocation());
            bukkitObjectOutputStream.writeObject(kothPoint.getRadius());
            bukkitObjectOutputStream.writeObject(kothPoint.getCaptureTime());
            bukkitObjectOutputStream.writeObject(kothPoint.getPointsAwarding());

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

    public static void saveKOTHSpawn(KOTHSpawn kothSpawn, String fileName) {

        String serializedSlotMachine;
        try{
            ByteArrayOutputStream biteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(biteArrayOutputStream);

            bukkitObjectOutputStream.writeObject(kothSpawn.getPos());
            bukkitObjectOutputStream.writeObject(kothSpawn.getTeamID());

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

}
