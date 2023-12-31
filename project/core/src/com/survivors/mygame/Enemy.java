package com.survivors.mygame;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;

import static com.survivors.mygame.MyGame.*;

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

    // Represents all the item types this enemy drops
    private ArrayList<DroppedItem.DroppedItemTypeName> DroppedItemTypes;
    // Represents the respective number of each item type this enemy drops
    private ArrayList<Integer> DroppedItemAmounts;

    // For tracking which enemy was collided with by an attack (pierce tracking)
    private Integer enemyId;

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
        // TODO: confirm whether or not enemies have their ORIGINS on the point they are supposed to be spawned at
        x -= ALL_CHARACTER_DATA.get(this.getDataIndex()).getOriginX() * SCALE_FACTOR;
        y -= (ALL_CHARACTER_DATA.get(this.getDataIndex()).getDimensionY() - ALL_CHARACTER_DATA.get(this.getDataIndex()).getOriginY()) * SCALE_FACTOR;
        this.setCurrentHp(ALL_CHARACTER_DATA.get(this.getDataIndex()).getMaxHp());
        this.makeBody(ALL_CHARACTER_DATA.get(this.getDataIndex()).getInternalName(), x, y, 0, world, physicsShapeCache);
        this.setState(CharacterState.STANDING);
        spawnedWave = curWave;
        fromOldWave = false;
        this.setId("enemy", this);
        this.enemyId = getNextEntityId();
    }

    public int getSpawnedWave() {
        return spawnedWave;
    }

    public void markOldWave() {
        this.fromOldWave = true;
    }

    public boolean fromOldWave() {
        return fromOldWave;
    }

    // Nick: Automatically called by Pool.free() whenever an enemy instance is freed
    public void reset() {

    }

    // returns the ID's of this enemy's dropped items
    public ArrayList<DroppedItem.DroppedItemTypeName> getDroppedItemTypes() {
        return DroppedItemTypes;
    }

    public ArrayList<Integer> getDroppedItemAmounts() {
        return DroppedItemAmounts;
    }

    public Integer getEnemyId() {
        return enemyId;
    }
}
