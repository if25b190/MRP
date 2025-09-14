package me.duong.mrp;

public final class Logger {
    public static void debug(String message, Object... args) {
        System.getLogger("MRP").log(System.Logger.Level.DEBUG, "[DEBUG] " + String.format(message, args));
    }
    public static void info(String message, Object... args) {
        System.getLogger("MRP").log(System.Logger.Level.INFO, "[INFO] " + String.format(message, args));
    }
    public static void warning(String message, Object... args) {
        System.getLogger("MRP").log(System.Logger.Level.WARNING, "[WARNING] " + String.format(message, args));
    }
    public static void error(String message, Object... args) {
        System.getLogger("MRP").log(System.Logger.Level.ERROR, "[ERROR] " + String.format(message, args));
    }
}
