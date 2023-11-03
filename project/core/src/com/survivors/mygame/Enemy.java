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

    // Nick: inUse represents whether the enemy is currently in use by a pool
    boolean inUse;

    /* Nick: oldwave represents whether this enemy is part of an old wave
     *       of enemies, and thus needs to be phased out next time it goes off-screen
     */
    boolean oldwave = false;

    /* Nick: constructor now always passes VOID typename when creating
     *       a new enemy, as it will default to this state in the pool
     *       while being considered for use
     */
    public Enemy(float x, float y, World world, PhysicsShapeCache physicsShapeCache) {
        super(Character.CharacterTypeName.VOID, x, y, world, physicsShapeCache);
        inUse = false;
        oldwave = false;
    }

    /* Nick: This method should be called when getting a new enemy from a pool;
     *       it is basically super()
     */
    public void init(CharacterTypeName characterTypeName, float x, float y, World world, PhysicsShapeCache physicsShapeCache) {
        this.dataIndex = characterTypeName.ordinal();
        this.makeBody(x, y, 0, world, physicsShapeCache);
        this.setState(CharacterState.STANDING);
        inUse = true;
        oldwave = false;
    }

    // Nick: This method is called by Pool.free() whenever an enemy instance is freed
    public void reset() {
        inUse = false;
    }


}
