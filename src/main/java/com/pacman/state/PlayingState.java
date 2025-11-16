package com.pacman.state;

import com.pacman.PacMan;
import com.pacman.util.LoggerManager;
import java.awt.*;
import java.awt.event.KeyEvent;

public class PlayingState implements GameState {
    private static final LoggerManager logger = LoggerManager.getInstance();

    @Override
    public void update(PacMan game) {
        game.move();
        if (game.isGameOver()) {
            game.setState(new GameOverState());
        } else if (game.isWin()) {
            game.setState(new WinState());
        }
    }

    @Override
    public void draw(PacMan game, Graphics g) {
        game.drawGameElements(g);

        // Draw score and lives
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + game.getScore(), 10, game.getBoardHeight() + 20);
        g.drawString("Lives: " + game.getLives(), game.getBoardWidth() - 120, game.getBoardHeight() + 20);
    }

    @Override
    public void handleInput(PacMan game, int keyCode) {
        if (keyCode == KeyEvent.VK_UP) {
            game.getPacman().updateDirection('U', game.getWalls());
        } else if (keyCode == KeyEvent.VK_DOWN) {
            game.getPacman().updateDirection('D', game.getWalls());
        } else if (keyCode == KeyEvent.VK_LEFT) {
            game.getPacman().updateDirection('L', game.getWalls());
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            game.getPacman().updateDirection('R', game.getWalls());
        }
    }
}
