package com.pacman;

import com.pacman.composite.Level;
import com.pacman.decorator.CharacterDecorator;
import com.pacman.decorator.ShieldDecorator;
import com.pacman.factory.EntityFactory;
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

    private EntityFactory entityFactory;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
    // Other images are handled by factory for creation, but we keep pacman
    // directionals for updatePacmanImage

    private Timer gameLoop;
    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> powerFoods;
    private HashSet<Block> ghosts;
    private Block pacman;
    private GameCharacter renderablePacman; // The top-level decorated character for rendering/updates
    private Level currentLevel;
    private GameState gameState;
    private Random random = new Random();
    private char[] directions = { 'U', 'D', 'L', 'R' };
    private int lives = 3;
    private int score = 0;
    private boolean isGameOver = false;
    private boolean isWin = false;

    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "XO       X       OX",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "XXX  X       X  XXX",
            "XXXX X XXrXX X XXXX",
            "X       bpo       X",
            "XXXX X XXXXX X XXXX",
            "XXX  X       X  XXX",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "XO   X   X   X   OX",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    public PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        entityFactory = new EntityFactory();
        loadImages(); // Only load directional images for Pacman updates
        loadMap();
        setState(new MenuState());

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    private void loadImages() {
        try {
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
                    Block wall = entityFactory.createWall(x, y, tileSize);
                    walls.add(wall);
                    currentLevel.addEntity(wall);
                } else if (tileMapChar == 'b' || tileMapChar == 'o' || tileMapChar == 'p' || tileMapChar == 'r') {
                    String type = "red";
                    if (tileMapChar == 'b')
                        type = "blue";
                    else if (tileMapChar == 'o')
                        type = "orange";
                    else if (tileMapChar == 'p')
                        type = "pink";

                    Block ghost = entityFactory.createGhost(type, x, y, tileSize, BASE_SPEED);
                    ghosts.add(ghost);
                    currentLevel.addEntity(ghost);
                    ghost.updateDirection(directions[random.nextInt(4)], walls);
                } else if (tileMapChar == 'P') {
                    pacman = entityFactory.createPacman(x, y, tileSize, BASE_SPEED);
                    pacman.manualMovement = true; // Prevent double movement in update()
                    renderablePacman = pacman;
                    currentLevel.addEntity(renderablePacman);
                } else if (tileMapChar == ' ') {
                    Block food = entityFactory.createFood(x + 14, y + 14);
                    foods.add(food);
                    currentLevel.addEntity(food);
                } else if (tileMapChar == 'O') {
                    Block powerFood = entityFactory.createPowerFood(x + 8, y + 8);
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

        // Reset decorators on reset
        if (renderablePacman != pacman) {
            currentLevel.removeEntity(renderablePacman);
            renderablePacman = pacman;
            currentLevel.addEntity(renderablePacman);
        }

        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)], walls);
        }
    }

    public void move() {
        // Update the renderable pacman (ticks timers etc)
        // This will call super.update() which calls pacman.update().
        // Since pacman.manualMovement is true, pacman.update() won't move x,y,
        // preventing double movement.
        renderablePacman.update();

        // Check for expired shield
        if (renderablePacman instanceof ShieldDecorator) {
            ShieldDecorator shield = (ShieldDecorator) renderablePacman;
            if (!shield.isActive()) {
                currentLevel.removeEntity(renderablePacman);
                // Unwrap: ideally get the decorated character.
                // Assuming simple 1-level wrap for now or accessing protected if we made public
                // getter
                if (renderablePacman instanceof CharacterDecorator) {
                    renderablePacman = ((CharacterDecorator) renderablePacman).getDecoratedCharacter();
                } else {
                    renderablePacman = pacman; // Fallback
                }
                currentLevel.addEntity(renderablePacman);
                logger.logInfo("Shield expired and removed from level.");
            }
        }

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
            currentLevel.removeEntity(foodEaten);
        }

        // Collision avec les power-ups
        Block powerFoodEaten = null;
        for (Block powerFood : powerFoods) {
            if (pacman.collision(powerFood)) {
                powerFoodEaten = powerFood;
                score += 50;

                // Logic to apply shield
                boolean alreadyShielded = (renderablePacman instanceof ShieldDecorator)
                        && ((ShieldDecorator) renderablePacman).isActive();

                if (!alreadyShielded) {
                    // Wrap the CURRENT renderable (could be SpeedBoosted etc)
                    ShieldDecorator shield = new ShieldDecorator(renderablePacman);
                    screenSwapPacman(shield);
                    shield.applyEffect();
                } else {
                    // Already have shield, just reset duration
                    ((ShieldDecorator) renderablePacman).applyEffect();
                }
                break;
            }
        }
        if (powerFoodEaten != null) {
            powerFoods.remove(powerFoodEaten);
            currentLevel.removeEntity(powerFoodEaten);
        }

        // Collision avec les fantômes
        for (Block ghost : ghosts) {
            if (pacman.collision(ghost)) {
                logger.logCollision("Pacman collided with Ghost.");

                ShieldDecorator shield = getActiveShield(renderablePacman);

                if (shield != null) {
                    shield.removeEffect();
                    // IMPORTANT: Reset the ghost to prevent immediate re-collision in next frame
                    ghost.reset();
                    ghost.updateDirection(directions[random.nextInt(4)], walls);
                    logger.logInfo("Ghost repelled by shield and reset.");
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

    // Helper to swap entities in the level
    private void screenSwapPacman(GameCharacter newHelper) {
        currentLevel.removeEntity(renderablePacman);
        renderablePacman = newHelper;
        currentLevel.addEntity(renderablePacman);
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
        // Vérifier la collision avec les murs en utilisant une marge pour éviter de se
        // bloquer
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
        currentLevel.draw(g); // Calls draw on renderablePacman (Shield -> Block)

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
    public void keyTyped(KeyEvent e) {
    }

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
    public void keyReleased(KeyEvent e) {
    }

    // Getters
    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getTileSize() {
        return tileSize;
    }

    public Block getPacman() {
        return pacman;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isWin() {
        return isWin;
    }

    public HashSet<Block> getWalls() {
        return walls;
    }

    public HashSet<Block> getFoods() {
        return foods;
    }

    public HashSet<Block> getGhosts() {
        return ghosts;
    }

    public HashSet<Block> getPowerFoods() {
        return powerFoods;
    }

    private ShieldDecorator getActiveShield(GameCharacter character) {
        GameCharacter current = character;
        while (current instanceof CharacterDecorator) {
            if (current instanceof ShieldDecorator) {
                ShieldDecorator shield = (ShieldDecorator) current;
                if (shield.isActive()) {
                    return shield;
                }
            }
            current = ((CharacterDecorator) current).getDecoratedCharacter();
        }
        return null;
    }

    public void setState(GameState newState) {
        this.gameState = newState;
    }
}