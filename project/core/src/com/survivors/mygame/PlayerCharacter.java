package com.survivors.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;

public class PlayerCharacter extends Character {
    private int movementSpeed = 64;
    // Will be tangible
    // Requires keyboard control input

    // This will hold the EquippableItems for controlling when they trigger
    // and what their levels/bonuses from stats are (ex: +1 projectile)
    ArrayList<EquippableItem> equipment = new ArrayList<>();


    /**
     * @param x The x coordinate of the spawning position of the Enemy.
     * @param y The y coordinate of the spawning position of the Enemy.
     */
    public PlayerCharacter(float x, float y, World world, PhysicsShapeCache physicsShapeCache) {
        super(CharacterTypeName.HELMET_PENGUIN, x, y, world, physicsShapeCache);
    }

    public void keyCheck() {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        int leftRightDirection = 0;
        int upDownDirection = 0;
        if (left) {
            this.faceLeft();
            leftRightDirection--;
        }
        if (right) {
            this.faceRight();
            leftRightDirection++;
        }
        if (up) {
            upDownDirection++;
        }
        if (down) {
            upDownDirection--;
        }
        if (leftRightDirection != 0 || upDownDirection != 0) {
            this.setState(CharacterState.MOVING);
        } else {
            this.setState(CharacterState.STANDING);
        }
        move(leftRightDirection, upDownDirection, movementSpeed);
    }
}
