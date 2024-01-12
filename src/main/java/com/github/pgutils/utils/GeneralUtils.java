package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralUtils {
    public static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");


    public static final String fixColors(String message) {
        return GeneralUtils.colorize(GeneralUtils.translateHexColorCodes(message));
    }

    private static final String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static final String translateHexColorCodes(String message) {
        Matcher matcher = GeneralUtils.HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "§x§" + group.charAt(0) + '§' + group.charAt(1) + '§' + group.charAt(2) + '§' + group.charAt(3) + '§' + group.charAt(4) + '§' + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

    public static String hexToMinecraftColor(String hexColor) {
        if (hexColor.startsWith("#") && hexColor.length() == 7) {
            StringBuilder converted = new StringBuilder("§x");
            for (char c : hexColor.substring(1).toCharArray()) {
                converted.append("§").append(c);
            }
            return converted.toString();
        }
        return hexColor;
    }

    public static void runCommand(CommandSender sender, String cmd) {
        Bukkit.getServer().dispatchCommand(sender, cmd);
    }

    public static void playerSelectPlaySpace(Player sender, PlaySpace playSpace) {
        if (PGUtils.selectedPlaySpace.stream().anyMatch(selector -> selector.player.equals(sender))) {
            PGUtils.selectedPlaySpace.stream()
                    .filter(selector -> selector.player.equals(sender))
                    .forEach(selector -> selector.playSpace = playSpace);
        } else {
            PGUtils.selectedPlaySpace.add(new PlayerPlaySpaceSelector(sender, playSpace));
        }
    }

    public static void playerSelectLobby(Player sender, Lobby lobby) {
        if (PGUtils.selectedLobby.stream().anyMatch(selector -> selector.player.equals(sender))) {
            PGUtils.selectedLobby.stream()
                    .filter(selector -> selector.player.equals(sender))
                    .forEach(selector -> selector.lobby = lobby);
        } else {
            PGUtils.selectedLobby.add(new PlayerLobbySelector(sender, lobby));
        }

    }


    public static Lobby isPlayerInGame(Player player) {
        Optional<Lobby> _lobby = Lobby.lobbies.stream()
                .filter(lobby -> lobby.getPlayers().contains(player))
                .findFirst();
        if (!_lobby.isPresent()) {
            return null;
        }
        Lobby lobby = _lobby.get();
        return lobby;
    }

    public static boolean kickPlayerGlobal(Player player) {
        Lobby lobby = GeneralUtils.isPlayerInGame(player);
        if (lobby == null) {
            return false;
        }
        lobby.kickPlayer(player);
        return true;
    }

    public static double speedFunc(double a, double b, double c) {
        if (c == a || c == b) {
            return 0.0;
        }
        double middle = (a + b) / 2.0;
        double distanceToMiddle = Math.abs(c - middle);
        double normalizedValue = 1.0 - distanceToMiddle / ((b - a) / 2.0);
        return Math.max(0.0, Math.min(1.0, normalizedValue));
    }

    public static boolean setRespawnPoint(Location loc1) {
        File respawnFile = new File(PGUtils.getPlugin(PGUtils.class).database, "respawn.yml");
        try {
            respawnFile.createNewFile();
            FileConfiguration spawn = YamlConfiguration.loadConfiguration(respawnFile);
            spawn.set("respawn.world", loc1.getWorld().getName());
            spawn.set("respawn.loc1.x", loc1.getX());
            spawn.set("respawn.loc1.y", loc1.getY());
            spawn.set("respawn.loc1.z", loc1.getZ());

            spawn.save(respawnFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Location getRespawnPoint() {
        File respawnFile = new File(PGUtils.getPlugin(PGUtils.class).database, "respawn.yml");
        try {
            respawnFile.createNewFile();
            FileConfiguration spawn = YamlConfiguration.loadConfiguration(respawnFile);
            double loc1X = spawn.getDouble("respawn.loc1.x");
            double loc1Y = spawn.getDouble("respawn.loc1.y");
            double loc1Z = spawn.getDouble("respawn.loc1.z");
            String worldName = spawn.getString("respawn.world");

            return new Location(PGUtils.getPlugin(PGUtils.class).getServer().getWorld(worldName), loc1X, loc1Y, loc1Z);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int findPriorityLobby() {
        return 1;
    }

    public static String generateUniqueID() {
        String id = "";
        for (int i = 0; i < 10; i++) {
            id += (int) (Math.random() * 10);
        }
        return id;
    }

    public static String generateLoadingBar(int percentage, String barColor, String backgroundColor) {
        String bar = "";
        for (int i = 1; i < 11; i++) {
            if (percentage >= i * 10) {
                bar += barColor + "█";
            } else {
                bar += backgroundColor + "█";
            }
        }
        return bar;
    }

    public static Lobby getLobbyByID(int id) {
        Optional<Lobby> _lobby = Lobby.lobbies.stream()
                .filter(lobby -> lobby.getID() == id)
                .findFirst();
        if (!_lobby.isPresent()) {
            return null;
        }
        Lobby lobby = _lobby.get();
        return lobby;
    }

}
