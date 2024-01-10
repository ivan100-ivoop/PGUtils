package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        while(matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "§x§" + group.charAt(0) + '§' + group.charAt(1) + '§' + group.charAt(2) + '§' + group.charAt(3) + '§' + group.charAt(4) + '§' + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
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

    private static List<String> getLoreWithFix(List<String> lores) {
        ArrayList<String> colored = new ArrayList<String>();
        for(String lore : lores){
            colored.add(GeneralUtils.fixColors(lore));
        }
        return colored;
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
        Lobby _lobby = Lobby.lobbies.stream()
                .filter(lobby -> lobby.getPlayers().contains(player))
                .findFirst()
                .get();
        if(_lobby != null){
            _lobby.removePlayer(player);
            return _lobby;
        }
        return null;
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

    public static boolean setRespawnPoint(Location loc1){
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

    public static Location getRespawnPoint(){
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


}
