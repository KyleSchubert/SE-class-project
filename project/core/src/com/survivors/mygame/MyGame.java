package com.survivors.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGame extends ApplicationAdapter {
    World world;
    SpriteBatch batch;
    Enemy testEnemy; // ALWAYS DECLARE HERE
    Enemy testEnemy2; // ALWAYS DECLARE HERE
    Enemy testEnemy3; // ALWAYS DECLARE HERE
    Enemy testEnemy4; // ALWAYS DECLARE HERE
    Enemy testEnemy5; // ALWAYS DECLARE HERE
    Enemy testEnemy6; // ALWAYS DECLARE HERE

    @Override
    public void create() {
        world = new World(new Vector2(0, 0), true); // new Vector2(0, 0)  -  because we don't want gravity
        batch = new SpriteBatch();
        // NOTE: IN LIBGDX, POINT (0, 0) IS LOCATED AT THE BOTTOM LEFT, FOR THE DEFAULT CAMERA POSITION
        testEnemy = new Enemy(Enemy.EnemyTypeName.BIRD, 60, 80); // THEN INITIALIZE HERE

        testEnemy2 = new Enemy(Enemy.EnemyTypeName.ORANGE_MUSHROOM, 120, 140); // THEN INITIALIZE HERE
        // Default state is   Enemy.EnemyState.STANDING

        testEnemy3 = new Enemy(Enemy.EnemyTypeName.ORANGE_MUSHROOM, 220, 140); // THEN INITIALIZE HERE
        testEnemy3.setState(Enemy.EnemyState.MOVING); // SET STATES LIKE THIS

        testEnemy4 = new Enemy(Enemy.EnemyTypeName.ORANGE_MUSHROOM, 320, 140); // THEN INITIALIZE HERE
        testEnemy4.setState(Enemy.EnemyState.DYING); // SET STATES LIKE THIS

        testEnemy5 = new Enemy(Enemy.EnemyTypeName.BIRD, 60, 180); // THEN INITIALIZE HERE
        testEnemy5.setState(Enemy.EnemyState.DYING); // SET STATES LIKE THIS

        testEnemy6 = new Enemy(Enemy.EnemyTypeName.BIRD, 60, 280); // THEN INITIALIZE HERE
        testEnemy6.setState(Enemy.EnemyState.MOVING); // SET STATES LIKE THIS
        /*
        The enemy images are set up in such a way that the origin of each monster will always
            be on the bottom and middle of the actual enemy drawing part of each sprite
        ESSENTIALLY, the origin is the center of the bottom of the "feet" of each enemy, even if it doesn't have feet.
        For example, the orange mushroom is animated to jump a bit when it moves.
            Its origin will be in the bottom middle, so for its jumping frame it
            will be slightly below and actually detached from its body by appearance.
         */
    }

    @Override
    public void render() {
        float elapsedTime = stepWorld();
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        testEnemy.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy2.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy3.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy4.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy5.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy6.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
    }

    // START SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics
    public static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;

    float accumulator = 0;

    private float stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);
        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            return STEP_TIME;
        } else {
            return 0;
        }
    }
    // END SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics
}
