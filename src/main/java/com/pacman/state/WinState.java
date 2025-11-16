package com.pacman.state;

import com.pacman.PacMan;
import com.pacman.util.LoggerManager;
import java.awt.*;
import java.awt.event.KeyEvent;

public class WinState implements GameState {
    private static final LoggerManager logger = LoggerManager.getInstance();

    @Override
    public void update(PacMan game) {
        // No updates in win state
    }

    @Override
    public void draw(PacMan game, Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, game.getBoardWidth(), game.getBoardHeight());

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String win = "YOU WIN!";
        FontMetrics fm = g.getFontMetrics();
        int x = (game.getBoardWidth() - fm.stringWidth(win)) / 2;
        g.drawString(win, x, 150);

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

        logger.logInfo("Game Won. Score: " + game.getScore());
    }

    @Override
    public void handleInput(PacMan game, int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE) {
            game.resetGame();
            game.setState(new MenuState());
        }
    }
}
