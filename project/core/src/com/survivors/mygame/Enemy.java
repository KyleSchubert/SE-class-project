package com.survivors.mygame;

import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class Enemy extends Character {
    // Relies on player position
    // Will be tangible

    /**
     * @param characterTypeName The type of the enemy that will be created. This is an enum: EnemyData.EnemyTypeName
     * @param x                 The x coordinate of the spawning position of the Enemy.
     * @param y                 The y coordinate of the spawning position of the Enemy.
     */
    public Enemy(CharacterTypeName characterTypeName, float x, float y, World world, PhysicsShapeCache physicsShapeCache) {
        super(characterTypeName, x, y, world, physicsShapeCache);
    }

}
