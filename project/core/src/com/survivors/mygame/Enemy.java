package com.survivors.mygame;

import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.survivors.mygame.MyGame.ALL_CHARACTER_DATA;

public class Enemy extends Character {
    // Relies on player position
    // Will be tangible

    /* Nick: Represents the wave number that this enemy spawned in.
     *       Defaults to -1 for newly created enemy instances before
     *       They are initialized from the pool. */
    private int spawnedWave;

    /* Nick: fromOldWave represents whether this enemy is part of an old wave
     *       of enemies, and thus needs to be phased out next time it goes off-screen
     */
    private boolean fromOldWave;

    /* Nick: constructor now always passes VOID typename when creating
     *       a new enemy, as it will default to this state in the pool
     *       while being considered for use
     */
    public Enemy() {
        super();
        fromOldWave = false;
        spawnedWave = -1;
    }

    /* Nick: This method should be called when getting a new enemy from a pool;
     *       it basically does what super() does, but just overwrites the previous
     *       variables stored in this instance instead of initializing the variables
     *       of a new instance */
    public void init(CharacterTypeName characterTypeName, float x, float y, int curWave, World world, PhysicsShapeCache physicsShapeCache) {
        this.setDataIndex(characterTypeName.ordinal());
        this.setCurrentHp(ALL_CHARACTER_DATA.get(this.getDataIndex()).getMaxHp());
        this.makeBody(ALL_CHARACTER_DATA.get(this.getDataIndex()).getInternalName(), x, y, 0, world, physicsShapeCache);
        this.setState(CharacterState.STANDING);
        spawnedWave = curWave;
        fromOldWave = false;
    }

    public int getSpawnedWave() {
        return spawnedWave;
    }

    public void markOldWave() {
        fromOldWave = true;
    }

    public boolean fromOldWave() {
        return fromOldWave;
    }

    // Nick: Automatically called by Pool.free() whenever an enemy instance is freed
    public void reset() {
    }
}
