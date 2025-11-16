package com.pacman.decorator;

import com.pacman.model.GameCharacter;
import com.pacman.util.LoggerManager;

public class SpeedBoostDecorator extends CharacterDecorator {
    private static final LoggerManager logger = LoggerManager.getInstance();
    private int duration = 150; // 7.5 seconds
    private int speedMultiplier = 2;
    private boolean active = true;

    public SpeedBoostDecorator(GameCharacter decoratedCharacter) {
        super(decoratedCharacter);
    }

    @Override
    public void applyEffect() {
        active = true;
        duration = 150;
        logger.logInfo("Speed boost activated!");
    }

    @Override
    public void removeEffect() {
        active = false;
        logger.logInfo("Speed boost deactivated!");
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
    public int getSpeed() {
        if (active) {
            return super.getSpeed() * speedMultiplier;
        }
        return super.getSpeed();
    }

    public boolean isActive() {
        return active;
    }
}
