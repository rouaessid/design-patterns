package com.pacman.state;

import com.pacman.model.Block;

public class IdleState implements CharacterState {
    @Override
    public void update(Block character) {
        // No movement in idle state
    }

    @Override
    public String getName() {
        return "IDLE";
    }
}
