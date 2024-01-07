package com.github.pgutils;

import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.ArrayList;
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
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(GeneralUtils.fixColors("&7Left Click on pos1"));
        lore.add(GeneralUtils.fixColors("&7Right Click on pos1"));

        ItemStack tool = new ItemStack(Material.STICK);
        ItemMeta meta = tool.getItemMeta();

        meta.setCustomModelData(Integer.parseInt("6381260"));
        meta.setDisplayName(GeneralUtils.fixColors("&5&lPGUtils &e&lTool"));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        tool.setItemMeta(meta);

        return tool;
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

    public static ChatColor ColorToChatColor(Color cc) {
        return ChatColor.of("#"+Integer.toHexString(cc.asRGB()));
    }

    public static boolean isPlayerInGame(Player player) {
        Lobby _lobby = Lobby.lobbies.stream()
                .filter(lobby -> lobby.getPlayers().contains(player))
                .findFirst()
                .get();
        if(_lobby != null){
            _lobby.removePlayer(player);
            return true;
        }
        return false;
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

            spawn.save(chestFile);
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
}
