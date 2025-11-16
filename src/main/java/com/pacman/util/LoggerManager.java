package com.pacman.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerManager {
    private static final LoggerManager instance = new LoggerManager();
    private static final Logger logger = LoggerFactory.getLogger(LoggerManager.class);

    private LoggerManager() {}

    public static LoggerManager getInstance() {
        return instance;
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logStateChange(String message) {
        logger.debug("[STATE] " + message);
    }

    public void logCollision(String message) {
        logger.info("[COLLISION] " + message);
    }

    public void logError(String message) {
        logger.error(message);
    }
}
