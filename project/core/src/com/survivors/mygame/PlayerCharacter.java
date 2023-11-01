package com.survivors.mygame;

import java.util.ArrayList;

public class PlayerCharacter extends Character {
    // Will be tangible
    // Requires keyboard control input

    // This will hold the EquippableItems for controlling when they trigger
    // and what their levels/bonuses from stats are (ex: +1 projectile)
    ArrayList<EquippableItem> equipment = new ArrayList<>();


    /**
     * @param characterTypeName The type of the enemy that will be created. This is an enum: EnemyData.EnemyTypeName
     * @param x                 The x coordinate of the spawning position of the Enemy.
     * @param y                 The y coordinate of the spawning position of the Enemy.
     */
    public PlayerCharacter(CharacterTypeName characterTypeName, int x, int y) {
        super(characterTypeName, x, y);
    }
}
