package com.pacman.state;

import com.pacman.PacMan;
import java.awt.Graphics;

public interface GameState {
    void update(PacMan game);
    void draw(PacMan game, Graphics g);
    void handleInput(PacMan game, int keyCode);
}
