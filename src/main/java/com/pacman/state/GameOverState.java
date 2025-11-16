package com.pacman.state;

import com.pacman.PacMan;
import com.pacman.util.LoggerManager;
import java.awt.*;
import java.awt.event.KeyEvent;

public class GameOverState implements GameState {
    private static final LoggerManager logger = LoggerManager.getInstance();

    @Override
    public void update(PacMan game) {
        // No updates in game over state
    }

    @Override
    public void draw(PacMan game, Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, game.getBoardWidth(), game.getBoardHeight());

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String gameOver = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int x = (game.getBoardWidth() - fm.stringWidth(gameOver)) / 2;
        g.drawString(gameOver, x, 150);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String score = "Final Score: " + game.getScore();
        fm = g.getFontMetrics();
        x = (game.getBoardWidth() - fm.stringWidth(score)) / 2;
        g.drawString(score, x, 220);

        String restart = "Press SPACE to Menu";
        fm = g.getFontMetrics();
        x = (game.getBoardWidth() - fm.stringWidth(restart)) / 2;
        g.drawString(restart, x, 280);

        logger.logInfo("Game Over. Score: " + game.getScore());
    }

    @Override
    public void handleInput(PacMan game, int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE) {
            game.resetGame();
            game.setState(new MenuState());
        }
    }
}
