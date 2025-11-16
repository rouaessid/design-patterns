package com.pacman.state;

import com.pacman.model.Block;

public class RunningState implements CharacterState {
    @Override
    public void update(Block character) {
        // Movement is handled by velocityX and velocityY
    }

    @Override
    public String getName() {
        return "RUNNING";
    }
}
