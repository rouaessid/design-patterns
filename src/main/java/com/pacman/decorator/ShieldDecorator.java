package com.pacman.decorator;

import com.pacman.model.GameCharacter;
import com.pacman.util.LoggerManager;
import java.awt.*;

public class ShieldDecorator extends CharacterDecorator {
    private static final LoggerManager logger = LoggerManager.getInstance();
    private int duration = 200; // 10 seconds (200 * 50ms)
    private boolean active = true;
    private static final int BLINK_INTERVAL = 10;
    private int blinkCounter = 0;

    public ShieldDecorator(GameCharacter decoratedCharacter) {
        super(decoratedCharacter);
    }

    @Override
    public void applyEffect() {
        active = true;
        duration = 200;
        logger.logInfo("Shield activated!");
    }

    @Override
    public void removeEffect() {
        active = false;
        logger.logInfo("Shield deactivated!");
    }

    @Override
    public void update() {
        super.update();
        if (active) {
            duration--;
            if (duration <= 0) {
                removeEffect();
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (active) {
            blinkCounter++;
            if ((blinkCounter / BLINK_INTERVAL) % 2 == 0) {
                // Draw shield circle
                if (decoratedCharacter instanceof com.pacman.model.Block) {
                    com.pacman.model.Block block = (com.pacman.model.Block) decoratedCharacter;
                    g.setColor(new Color(0, 255, 0, 100));
                    int padding = 5;
                    g.drawOval(block.x - padding, block.y - padding,
                            block.width + 2 * padding, block.height + 2 * padding);
                }
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public int getDuration() {
        return duration;
    }
}
