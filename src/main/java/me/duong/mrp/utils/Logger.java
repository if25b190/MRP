package me.duong.mrp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Logger {
    public static void debug(String message, Object... args) {
        log(System.Logger.Level.DEBUG, message, args);
    }

    public static void info(String message, Object... args) {
        log(System.Logger.Level.INFO, message, args);
    }

    public static void warning(String message, Object... args) {
        log(System.Logger.Level.WARNING, message, args);
    }

    public static void error(String message, Object... args) {
        log(System.Logger.Level.ERROR, message, args);
    }

    private static void log(System.Logger.Level level, String message, Object... args) {
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss-S").format(new Date());
        String output = String.format("%s - [%s] %s", date, level.name(), String.format(message, args));
        if (level != System.Logger.Level.ERROR && level != System.Logger.Level.WARNING) {
            System.out.println(output);
        } else {
            System.err.println(output);
        }
    }
}
