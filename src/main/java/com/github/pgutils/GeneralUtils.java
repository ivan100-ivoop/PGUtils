package com.github.pgutils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import org.bukkit.Bukkit;

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

    public static void runCommand(CommandSender sender, String cmd) {
        Bukkit.getServer().dispatchCommand(sender, cmd);
    }
}
