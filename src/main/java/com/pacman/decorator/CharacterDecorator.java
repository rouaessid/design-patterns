package com.pacman.decorator;

import com.pacman.model.GameCharacter;
import java.awt.*;

public abstract class CharacterDecorator implements GameCharacter {
    protected GameCharacter decoratedCharacter;

    public CharacterDecorator(GameCharacter decoratedCharacter) {
        this.decoratedCharacter = decoratedCharacter;
    }

    @Override
    public void draw(Graphics g) {
        decoratedCharacter.draw(g);
    }

    @Override
    public void update() {
        decoratedCharacter.update();
    }

    @Override
    public int getSpeed() {
        return decoratedCharacter.getSpeed();
    }

    @Override
    public void setSpeed(int speed) {
        decoratedCharacter.setSpeed(speed);
    }

    @Override
    public Image getImage() {
        return decoratedCharacter.getImage();
    }

    @Override
    public void setImage(Image image) {
        decoratedCharacter.setImage(image);
    }

    @Override
    public String getName() {
        return decoratedCharacter.getName();
    }
}
