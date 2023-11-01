package com.survivors.mygame;

public class Enemy extends Character {
    // Relies on player position
    // Will be tangible

    /**
     * @param characterTypeName The type of the enemy that will be created. This is an enum: EnemyData.EnemyTypeName
     * @param x                 The x coordinate of the spawning position of the Enemy.
     * @param y                 The y coordinate of the spawning position of the Enemy.
     */
    public Enemy(CharacterTypeName characterTypeName, int x, int y) {
        super(characterTypeName, x, y);
    }

}
