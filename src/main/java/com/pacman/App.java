package com.pacman;

import com.pacman.util.LoggerManager;
import javax.swing.JFrame;

public class App {
    private static final LoggerManager logger = LoggerManager.getInstance();

    public static void main(String[] args) throws Exception {
        logger.logInfo("Starting Pac-Man game...");

        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man - Design Patterns");
        frame.setSize(boardWidth, boardHeight + 50);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        frame.setVisible(true);

        pacmanGame.setFocusable(true);
        pacmanGame.requestFocus();

        logger.logInfo("Game window created and displayed.");
    }
}
