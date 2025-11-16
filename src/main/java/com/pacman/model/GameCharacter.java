package com.pacman.model;

import java.awt.Graphics;
import java.awt.Image;

public interface GameCharacter {
    void draw(Graphics g);
    void update();
    void applyEffect();
    void removeEffect();
    int getSpeed();
    void setSpeed(int speed);
    Image getImage();
    void setImage(Image image);
    String getName();
}
