package com.pacman.composite;

import com.pacman.model.GameCharacter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Level implements GameCharacter {
    private List<GameCharacter> entities = new ArrayList<>();

    public void addEntity(GameCharacter entity) {
        entities.add(entity);
    }

    public void removeEntity(GameCharacter entity) {
        entities.remove(entity);
    }

    @Override
    public void draw(Graphics g) {
        for (GameCharacter entity : entities) {
            entity.draw(g);
        }
    }

    @Override
    public void update() {
        for (GameCharacter entity : entities) {
            entity.update();
        }
    }

    @Override
    public void applyEffect() {
        for (GameCharacter entity : entities) {
            entity.applyEffect();
        }
    }

    @Override
    public void removeEffect() {
        for (GameCharacter entity : entities) {
            entity.removeEffect();
        }
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public void setSpeed(int speed) { }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public void setImage(Image image) { }

    @Override
    public String getName() {
        return "Level";
    }
}
