package com.survivors.mygame;

import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class DroppedItem extends Mobile {
    // Relies on player position
    // Will be tangible

    // Similar ID system to CharacterTypeName:
    public enum DroppedItemTypeName {
        // placeholder values here:
        ITEM1, ITEM2, ITEM3, ITEM4, VOID
    }

    DroppedItemTypeName itemType;


    public DroppedItem() {
        super();
        // initially set to void like the Enemy and character constructors
        itemType = DroppedItemTypeName.VOID;
    }

    // called on a newly obtained DroppedItem from a DroppedItem pool
    public void init(float newX, float newY, DroppedItemTypeName newType, World world, PhysicsShapeCache physicsShapeCache) {
        /* Nick: Unsure if Kyle would like to store item types like we are with
         *       ALL_CHARACTER_DATA in MyGame, so I just have the first argument
         *       as newType.toString() for now */
        makeBody(newType.toString(), newX, newY, 0, world, physicsShapeCache);
    }


}
