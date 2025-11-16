package com.pacman.state;

import com.pacman.PacMan;
import com.pacman.util.LoggerManager;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuState implements GameState {
    private static final LoggerManager logger = LoggerManager.getInstance();

    @Override
    public void update(PacMan game) {
        // Menu state doesn't need updates
    }

    @Override
    public void draw(PacMan game, Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, game.getBoardWidth(), game.getBoardHeight());

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String title = "PAC-MAN";
        FontMetrics fm = g.getFontMetrics();
        int x = (game.getBoardWidth() - fm.stringWidth(title)) / 2;
        g.drawString(title, x, 100);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String start = "Press SPACE to Start";
        fm = g.getFontMetrics();
        x = (game.getBoardWidth() - fm.stringWidth(start)) / 2;
        g.drawString(start, x, 200);

        String info = "Eat all food to win. Avoid ghosts!";
        fm = g.getFontMetrics();
        x = (game.getBoardWidth() - fm.stringWidth(info)) / 2;
        g.drawString(info, x, 250);
    }

    @Override
    public void handleInput(PacMan game, int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE) {
            logger.logInfo("Game started!");
            game.setState(new PlayingState());
        }
    }
}
