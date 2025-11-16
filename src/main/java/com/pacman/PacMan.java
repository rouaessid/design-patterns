package com.pacman;

import com.pacman.composite.Level;
import com.pacman.decorator.ShieldDecorator;
import com.pacman.model.Block;
import com.pacman.model.GameCharacter;
import com.pacman.state.GameState;
import com.pacman.state.MenuState;
import com.pacman.util.LoggerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    private final LoggerManager logger = LoggerManager.getInstance();

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;
    private final int BASE_SPEED = tileSize / 8;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image scaredGhostImage;
    private Image powerFoodImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X O      X      O X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "O    X       X    O",
            "XXXX X XXrXX X XXXX",
            "       bpo          ",
            "XXXX X XXXXX X XXXX",
            "O    X       X    O",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X O               X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> powerFoods;
    private HashSet<Block> ghosts;
    private Block pacman;
    private Level currentLevel;
    private GameState gameState;
    private Timer gameLoop;
    private char[] directions = {'U', 'D', 'L', 'R'};
    private Random random = new Random();
    private int score = 0;
    private int lives = 3;
    private boolean isGameOver = false;
    private boolean isWin = false;

    public PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        loadImages();
        loadMap();
        setState(new MenuState());

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    private void loadImages() {
        try {
            wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();
            blueGhostImage = new ImageIcon(getClass().getResource("/blueGhost.png")).getImage();
            orangeGhostImage = new ImageIcon(getClass().getResource("/orangeGhost.png")).getImage();
            pinkGhostImage = new ImageIcon(getClass().getResource("/pinkGhost.png")).getImage();
            redGhostImage = new ImageIcon(getClass().getResource("/redGhost.png")).getImage();
            scaredGhostImage = new ImageIcon(getClass().getResource("/scaredGhost.png")).getImage();
            powerFoodImage = new ImageIcon(getClass().getResource("/powerFood.png")).getImage();

            pacmanUpImage = new ImageIcon(getClass().getResource("/pacmanUp.png")).getImage();
            pacmanDownImage = new ImageIcon(getClass().getResource("/pacmanDown.png")).getImage();
            pacmanLeftImage = new ImageIcon(getClass().getResource("/pacmanLeft.png")).getImage();
            pacmanRightImage = new ImageIcon(getClass().getResource("/pacmanRight.png")).getImage();
        } catch (Exception e) {
            logger.logError("Error loading images: " + e.getMessage());
        }
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        powerFoods = new HashSet<>();
        ghosts = new HashSet<>();
        currentLevel = new Level();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize, 0);
                    walls.add(wall);
                    currentLevel.addEntity(wall);
                } else if (tileMapChar == 'b' || tileMapChar == 'o' || tileMapChar == 'p' || tileMapChar == 'r') {
                    Image ghostImage = null;
                    if (tileMapChar == 'b') ghostImage = blueGhostImage;
                    else if (tileMapChar == 'o') ghostImage = orangeGhostImage;
                    else if (tileMapChar == 'p') ghostImage = pinkGhostImage;
                    else if (tileMapChar == 'r') ghostImage = redGhostImage;

                    Block ghost = new Block(ghostImage, x, y, tileSize, tileSize, BASE_SPEED);
                    ghosts.add(ghost);
                    currentLevel.addEntity(ghost);
                    ghost.updateDirection(directions[random.nextInt(4)], walls);
                } else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize, BASE_SPEED);
                    currentLevel.addEntity(pacman);
                } else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4, 0);
                    foods.add(food);
                    currentLevel.addEntity(food);
                } else if (tileMapChar == 'O') {
                    Block powerFood = new Block(powerFoodImage, x + 8, y + 8, 16, 16, 0);
                    powerFoods.add(powerFood);
                    currentLevel.addEntity(powerFood);
                }
            }
        }
        logger.logInfo("Level map loaded.");
    }

    public void resetGame() {
        lives = 3;
        score = 0;
        isGameOver = false;
        isWin = false;
        loadMap();
        resetPositions();
    }

    public void resetPositions() {
        pacman.reset();
        pacman.setDirection('R');
        pacman.setImage(pacmanRightImage);
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)], walls);
        }
    }

    public void move() {
        // Gestion du mouvement de Pac-Man avec le nouveau système
        pacman.moveWithCollisionCheck(walls);
        updatePacmanImage();

        // Mise à jour des fantômes
        for (Block ghost : ghosts) {
            ghost.update();
            if (checkWallCollision(ghost)) {
                ghost.x -= ghost.velocityX;
                ghost.y -= ghost.velocityY;
                ghost.updateDirection(directions[random.nextInt(4)], walls);
            }
        }

        // Collision avec la nourriture
        Block foodEaten = null;
        for (Block food : foods) {
            if (pacman.collision(food)) {
                foodEaten = food;
                score += 10;
                logger.logInfo("Food eaten. Score: " + score);
                break;
            }
        }
        if (foodEaten != null) {
            foods.remove(foodEaten);
        }

        // Collision avec les power-ups
        Block powerFoodEaten = null;
        for (Block powerFood : powerFoods) {
            if (pacman.collision(powerFood)) {
                powerFoodEaten = powerFood;
                score += 50;
                GameCharacter currentCharacter = pacman.getDecoratedCharacter();
                if (!(currentCharacter instanceof ShieldDecorator)) {
                    ShieldDecorator shield = new ShieldDecorator(currentCharacter);
                    pacman.setDecoratedCharacter(shield);
                    shield.applyEffect();
                }
                break;
            }
        }
        if (powerFoodEaten != null) {
            powerFoods.remove(powerFoodEaten);
        }

        // Collision avec les fantômes
        for (Block ghost : ghosts) {
            if (pacman.collision(ghost)) {
                logger.logCollision("Pacman collided with Ghost.");
                GameCharacter currentCharacter = pacman.getDecoratedCharacter();
                if (currentCharacter instanceof ShieldDecorator) {
                    ((ShieldDecorator) currentCharacter).removeEffect();
                    pacman.setDecoratedCharacter(pacman);
                } else {
                    lives -= 1;
                    if (lives == 0) {
                        isGameOver = true;
                        return;
                    }
                    resetPositions();
                }
            }
        }

        // Vérification de la victoire
        if (foods.isEmpty() && powerFoods.isEmpty()) {
            isWin = true;
        }
    }

    private void updatePacmanImage() {
        switch (pacman.getDirection()) {
            case 'U':
                pacman.setImage(pacmanUpImage);
                break;
            case 'D':
                pacman.setImage(pacmanDownImage);
                break;
            case 'L':
                pacman.setImage(pacmanLeftImage);
                break;
            case 'R':
                pacman.setImage(pacmanRightImage);
                break;
        }
    }

    private boolean checkWallCollision(Block block) {
        // Vérifier la collision avec les murs en utilisant une marge pour éviter de se bloquer
        int margin = 2;
        for (Block wall : walls) {
            if (block.collision(block.x + block.velocityX, block.y + block.velocityY,
                    block.width - margin, block.height - margin, wall)) {
                return true;
            }
        }
        return false;
    }

    public void drawGameElements(Graphics g) {
        currentLevel.draw(g);

        // Dessiner la nourriture
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        // Dessiner les power-ups
        for (Block powerFood : powerFoods) {
            powerFood.draw(g);
        }

        // Afficher le score et les vies
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, boardWidth - 80, 20);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameState.draw(this, g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameState.update(this);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        gameState.handleInput(this, e.getKeyCode());

        // Gestion des touches de direction pour Pac-Man
        if (pacman != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    pacman.setNextDirection('U');
                    break;
                case KeyEvent.VK_DOWN:
                    pacman.setNextDirection('D');
                    break;
                case KeyEvent.VK_LEFT:
                    pacman.setNextDirection('L');
                    break;
                case KeyEvent.VK_RIGHT:
                    pacman.setNextDirection('R');
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    // Getters
    public int getBoardWidth() { return boardWidth; }
    public int getBoardHeight() { return boardHeight; }
    public int getTileSize() { return tileSize; }
    public Block getPacman() { return pacman; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isWin() { return isWin; }
    public HashSet<Block> getWalls() { return walls; }
    public HashSet<Block> getFoods() { return foods; }
    public HashSet<Block> getGhosts() { return ghosts; }
    public HashSet<Block> getPowerFoods() { return powerFoods; }
    public void setState(GameState newState) { this.gameState = newState; }
}