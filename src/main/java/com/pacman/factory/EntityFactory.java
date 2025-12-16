package com.pacman.factory;

import com.pacman.model.Block;
import com.pacman.util.LoggerManager;

import javax.swing.ImageIcon;
import java.awt.Image;

public class EntityFactory {
    private static final LoggerManager logger = LoggerManager.getInstance();
    
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image powerFoodImage;
    private Image pacmanRightImage;
    
    public EntityFactory() {
        loadImages();
    }
    
    private void loadImages() {
        try {
            wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();
            blueGhostImage = new ImageIcon(getClass().getResource("/blueGhost.png")).getImage();
            orangeGhostImage = new ImageIcon(getClass().getResource("/orangeGhost.png")).getImage();
            pinkGhostImage = new ImageIcon(getClass().getResource("/pinkGhost.png")).getImage();
            redGhostImage = new ImageIcon(getClass().getResource("/redGhost.png")).getImage();
            powerFoodImage = new ImageIcon(getClass().getResource("/powerFood.png")).getImage();
            pacmanRightImage = new ImageIcon(getClass().getResource("/pacmanRight.png")).getImage();
        } catch (Exception e) {
            logger.logError("Error loading images in Factory: " + e.getMessage());
        }
    }

    public Block createWall(int x, int y, int tileSize) {
        return new Block(wallImage, x, y, tileSize, tileSize, 0);
    }
    
    public Block createGhost(String type, int x, int y, int tileSize, int baseSpeed) {
        Image ghostImage = null;
        switch (type) {
            case "blue": ghostImage = blueGhostImage; break;
            case "orange": ghostImage = orangeGhostImage; break;
            case "pink": ghostImage = pinkGhostImage; break;
            case "red": ghostImage = redGhostImage; break;
            default: ghostImage = redGhostImage; break;
        }
        return new Block(ghostImage, x, y, tileSize, tileSize, baseSpeed);
    }
    
    public Block createPacman(int x, int y, int tileSize, int baseSpeed) {
        return new Block(pacmanRightImage, x, y, tileSize, tileSize, baseSpeed);
    }
    
    public Block createFood(int x, int y) {
        // Food has no image in this simple implementation, just a small rect
        return new Block(null, x, y, 4, 4, 0);
    }
    
    public Block createPowerFood(int x, int y) {
        return new Block(powerFoodImage, x, y, 16, 16, 0);
    }
}
