package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;

public class Messages {
    public static String withPrefix(String message){
        return GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + message);
    }
    public static String withoutPrefix(String message){
        return GeneralUtils.fixColors(message);
    }

    public static String messageWithPrefix(String path, String def){
        return GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString(path, def));
    }

    public static String getMessage(String path, String def){
        return PGUtils.getPlugin(PGUtils.class).getConfig().getString(path, def);
    }
}
