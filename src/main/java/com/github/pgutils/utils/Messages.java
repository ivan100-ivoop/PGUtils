package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messages {

    public static FileConfiguration getMessages() {
        FileConfiguration messages = new YamlConfiguration();
        File path = new File(PGUtils.getPlugin(PGUtils.class).lang, PGUtils.getPlugin(PGUtils.class).getConfig().getString("lang", "en") + ".yml");
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
        return GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + Messages.getMessages().getString(path, def));
    }

    public static String getMessage(String path, String def, boolean withoutColor) {
        if (withoutColor) {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Messages.getMessages().getString(path, def)));
        }
        return GeneralUtils.fixColors(Messages.getMessages().getString(path, def));
    }
}
