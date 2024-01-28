package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messages {
    public static boolean enableAutoSave = false;
    public static FileConfiguration getMessages() {
        FileConfiguration messages = new YamlConfiguration();
        File path = new File(PGUtils.getPlugin(PGUtils.class).loader.lang, PGUtils.getPlugin(PGUtils.class).getConfig().getString("lang", "en") + ".yml");
        if (path.exists()) {
            try {
                messages.load(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return messages;
    }

    public static String messageWithPrefix(String path, String def) {
        if(!Messages.getMessages().contains(path) && Messages.enableAutoSave){
            Messages.updateFile(path, def);
        }
        return GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).loader.prefix + Messages.getMessages().getString(path, def));
    }

    private static void updateFile(String path, String def) {
        FileConfiguration messages = Messages.getMessages();
        messages.set(path, def);
        File file_path = new File(PGUtils.getPlugin(PGUtils.class).loader.lang, PGUtils.getPlugin(PGUtils.class).getConfig().getString("lang", "en") + ".yml");
        if (file_path.exists()) {
            try {
                messages.save(file_path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getMessage(String path, String def, boolean withoutColor) {
        if(!Messages.getMessages().contains(path) && Messages.enableAutoSave){
            Messages.updateFile(path, def);
        }
        if (withoutColor) {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Messages.getMessages().getString(path, def)));
        }
        return GeneralUtils.fixColors(Messages.getMessages().getString(path, def));
    }
}
