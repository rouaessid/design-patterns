package com.pacman.state;

import com.pacman.model.Block;

public interface CharacterState {
    void update(Block character);
    String getName();
}
