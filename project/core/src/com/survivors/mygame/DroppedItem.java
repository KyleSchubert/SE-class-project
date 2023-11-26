package com.survivors.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class DroppedItem extends Mobile {
    // Relies on player position
    // Will be tangible

    // Similar ID system to CharacterTypeName
    /* Nick: Now that I think about it, if each enemy is dropping a unique item,
     *       we might just be able to use the CharacterTypeName here instead
     *       of DroppedItemTypeName */
    public enum DroppedItemTypeName {
        // placeholder values here:
        ITEM1, ITEM2, ITEM3, ITEM4, VOID
    }

    DroppedItemTypeName itemType;


    // Test texture before implementing unique textures from each enemy type
    Texture testTexture;
    TextureRegion testRegion;


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

    // Test initialization
    public void test_init(float X, float Y, World world, PhysicsShapeCache physicsShapeCache) {
        makeBody("bird", X, Y, 0, world, physicsShapeCache);
        testTexture = new Texture("amogus.png");
        testRegion = new TextureRegion(testTexture);
    }

    // Test animate
    public void testAnimate(SpriteBatch batch) {
        batch.draw(testRegion, this.getX(), this.getY(), 0, 0, 5, 5, 1, 1, 0);
    }

    // Dispose of the test texture
    public void testDispose() {
        testTexture.dispose();
    }


    // Moves towards the given coordinates at constant speed of 15
    public void moveTowardsLocation(float destX, float destY) {
        float x = destX - this.getX();
        float y = destY - this.getY();
        this.move(x, y, 15);
    }

}
