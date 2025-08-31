package net.salmo;

import java.util.logging.Level;

public class Logger {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());

    public static Logger getInstance() {
        return new Logger();
    }

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void severe(String message) {
        logger.log(Level.SEVERE, message);
    }

    public static void fine(String message) {
        logger.log(Level.FINE, message);
    }

    public static void finer(String message) {
        logger.log(Level.FINER, message);
    }

    public static void finest(String message) {
    }
}