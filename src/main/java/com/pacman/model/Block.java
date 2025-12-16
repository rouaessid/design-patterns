package com.pacman.model;

import com.pacman.state.CharacterState;
import com.pacman.state.IdleState;
import com.pacman.state.RunningState;
import com.pacman.decorator.CharacterDecorator;
import com.pacman.util.LoggerManager;
import java.awt.*;
import java.util.HashSet;

public class Block implements GameCharacter {
    private final LoggerManager logger = LoggerManager.getInstance();

    public int x;
    public int y;
    public int width;
    public int height;
    public Image image;

    public int startX;
    public int startY;
    public char direction = 'R';
    public char nextDirection = 'R'; // Nouvelle direction demandée
    public int velocityX = 0;
    public int velocityY = 0;
    private int baseSpeed;

    // Flag to control if update() applies movement
    // Used for Pacman which is moved by specific collision logic in the game loop
    public boolean manualMovement = false;

    // State Pattern
    private CharacterState characterState;
    // Decorator Pattern
    private GameCharacter decoratedCharacter;

    public Block(Image image, int x, int y, int width, int height, int baseSpeed) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startX = x;
        this.startY = y;
        this.baseSpeed = baseSpeed;
        this.decoratedCharacter = this;
        setState(new IdleState());
        updateVelocity(); // Initialiser la vélocité
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }

    @Override
    public void update() {
        characterState.update(this);

        if (!manualMovement) {
            // Appliquer le mouvement
            x += velocityX;
            y += velocityY;
        }

        if (decoratedCharacter != this) {
            decoratedCharacter.update();
        }
    }

    @Override
    public void applyEffect() {
    }

    @Override
    public void removeEffect() {
    }

    @Override
    public int getSpeed() {
        return baseSpeed;
    }

    @Override
    public void setSpeed(int speed) {
        this.baseSpeed = speed;
        updateVelocity();
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String getName() {
        return "Block";
    }

    // State Pattern
    public void setState(CharacterState newState) {
        String oldState = (characterState != null) ? characterState.getName() : "NULL";
        this.characterState = newState;
        logger.logStateChange(getName() + ": " + oldState + " -> " + newState.getName());
    }

    public CharacterState getState() {
        return characterState;
    }

    // Decorator Pattern
    public void setDecoratedCharacter(GameCharacter decoratedCharacter) {
        this.decoratedCharacter = decoratedCharacter;
    }

    public GameCharacter getDecoratedCharacter() {
        return decoratedCharacter;
    }

    /**
     * Met à jour la direction avec vérification des collisions
     */
    public void updateDirection(char newDirection, HashSet<Block> walls) {
        this.nextDirection = newDirection;
        attemptDirectionChange(walls);
    }

    /**
     * Tente de changer de direction en vérifiant les collisions
     */
    public void attemptDirectionChange(HashSet<Block> walls) {
        // Si la direction demandée est la même que la direction actuelle, rien à faire
        if (nextDirection == direction) {
            return;
        }

        // Sauvegarder l'ancienne direction et position
        char oldDirection = this.direction;
        int oldX = this.x;
        int oldY = this.y;

        // Tester la nouvelle direction
        this.direction = nextDirection;
        updateVelocity();

        // Calculer la prochaine position
        int nextX = this.x + this.velocityX;
        int nextY = this.y + this.velocityY;

        // Vérifier les collisions avec les murs
        boolean willCollide = false;
        for (Block wall : walls) {
            if (collision(nextX, nextY, this.width, this.height, wall)) {
                willCollide = true;
                break;
            }
        }

        if (!willCollide) {
            // La nouvelle direction est valide
            if (this.velocityX != 0 || this.velocityY != 0) {
                if (this.characterState.getName().equals("IDLE")) {
                    setState(new RunningState());
                }
            }
            logger.logInfo("Direction changed to: " + direction);
        } else {
            // La nouvelle direction cause une collision, revenir à l'ancienne
            this.direction = oldDirection;
            updateVelocity();
            logger.logInfo("Direction change blocked by wall. Staying: " + direction);
        }
    }

    /**
     * Met à jour la vélocité basée sur la direction actuelle
     */
    public void updateVelocity() {
        int speed = decoratedCharacter.getSpeed();
        switch (this.direction) {
            case 'U':
                this.velocityX = 0;
                this.velocityY = -speed;
                break;
            case 'D':
                this.velocityX = 0;
                this.velocityY = speed;
                break;
            case 'L':
                this.velocityX = -speed;
                this.velocityY = 0;
                break;
            case 'R':
                this.velocityX = speed;
                this.velocityY = 0;
                break;
            default:
                this.velocityX = 0;
                this.velocityY = 0;
                break;
        }
    }

    /**
     * Vérifie si le bloc peut se déplacer dans sa direction actuelle
     */
    public boolean canMove(HashSet<Block> walls) {
        int nextX = this.x + this.velocityX;
        int nextY = this.y + this.velocityY;

        for (Block wall : walls) {
            if (collision(nextX, nextY, this.width, this.height, wall)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gère le mouvement avec vérification continue des collisions
     */
    public void moveWithCollisionCheck(HashSet<Block> walls) {
        // D'abord essayer de changer de direction si demandé
        if (nextDirection != direction) {
            attemptDirectionChange(walls);
        }

        // Ensuite vérifier si le mouvement actuel est possible
        if (!canMove(walls)) {
            // Arrêter le mouvement en cas de collision
            this.velocityX = 0;
            this.velocityY = 0;
            if (this.characterState.getName().equals("RUNNING")) {
                setState(new IdleState());
            }
        } else {
            // Le mouvement est possible, appliquer le déplacement
            this.x += this.velocityX;
            this.y += this.velocityY;

            // Mettre à jour l'état si nécessaire
            if ((this.velocityX != 0 || this.velocityY != 0) &&
                    this.characterState.getName().equals("IDLE")) {
                setState(new RunningState());
            }
        }
    }

    public void reset() {
        this.x = this.startX;
        this.y = this.startY;
        this.velocityX = 0;
        this.velocityY = 0;
        this.direction = 'R';
        this.nextDirection = 'R';
        setState(new IdleState());
        updateVelocity();

        if (decoratedCharacter != this) {
            if (decoratedCharacter instanceof CharacterDecorator) {
                ((CharacterDecorator) decoratedCharacter).removeEffect();
            }
            decoratedCharacter = this;
        }
    }

    public boolean collision(Block b) {
        return this.x < b.x + b.width &&
                this.x + this.width > b.x &&
                this.y < b.y + b.height &&
                this.y + this.height > b.y;
    }

    public boolean collision(int x, int y, int width, int height, Block b) {
        return x < b.x + b.width &&
                x + width > b.x &&
                y < b.y + b.height &&
                y + height > b.y;
    }

    // Getters et Setters pour la direction suivante
    public char getNextDirection() {
        return nextDirection;
    }

    public void setNextDirection(char nextDirection) {
        this.nextDirection = nextDirection;
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
        updateVelocity();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}