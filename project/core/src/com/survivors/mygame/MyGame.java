package com.survivors.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;

public class MyGame extends ApplicationAdapter {
    /**
     * Because Box 2D has a speed limit of "2 meters per frame," which is actually
     * very small because a "meter" is only a few pixels, we need to increase the speed limit.
     * The SCALE_FACTOR can let the speed limit go higher by making everything "smaller."
     * There is no other way to increase the speed limit without breaking anything.
     */
    public static final float SCALE_FACTOR = 0.05f;
    ExtendViewport viewport;
    World world;
    SpriteBatch batch;
    PhysicsShapeCache physicsShapeCache;
    OrthographicCamera camera;
    Texture theFloor;
    public static final Array<CharacterData> ALL_CHARACTER_DATA = new Array<>();
    Box2DDebugRenderer debugRenderer;
    private static final float windowWidth = 1440;
    private static final float windowHeight = 920;
    private static final float viewWidth = windowWidth * SCALE_FACTOR;
    private static final float viewHeight = windowHeight * SCALE_FACTOR;
    Enemy testEnemy; // ALWAYS DECLARE HERE
    Enemy testEnemy2; // ALWAYS DECLARE HERE
    Enemy testEnemy3; // ALWAYS DECLARE HERE
    Enemy testEnemy4; // ALWAYS DECLARE HERE
    Enemy testEnemy5; // ALWAYS DECLARE HERE
    Enemy testEnemy6; // ALWAYS DECLARE HERE
    PlayerCharacter playerCharacter;
    ArrayList<Enemy> enemies;

    @Override
    public void create() {
        world = new World(new Vector2(0, 0), true); // new Vector2(0, 0)  -  because we don't want gravity
        physicsShapeCache = new PhysicsShapeCache("physics.xml");
        batch = new SpriteBatch();

        // Loading in each CharacterTypeName's CharacterData into allCharacterData
        for (Character.CharacterTypeName name : Character.CharacterTypeName.values()) {
            ALL_CHARACTER_DATA.add(new CharacterData(name));
        }

        camera = new OrthographicCamera(viewWidth, viewHeight);

        viewport = new ExtendViewport(viewWidth, viewHeight, camera);
        theFloor = new Texture("testFloor1.png");

        theFloor.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        debugRenderer = new Box2DDebugRenderer();
        // NOTE: IN LIBGDX, POINT (0, 0) IS LOCATED AT THE BOTTOM LEFT, FOR THE DEFAULT CAMERA POSITION
        testEnemy = new Enemy(Character.CharacterTypeName.BIRD, 3.2f, 4.2f, world, physicsShapeCache); // THEN INITIALIZE HERE

        testEnemy2 = new Enemy(Character.CharacterTypeName.ORANGE_MUSHROOM, 6, 7, world, physicsShapeCache); // THEN INITIALIZE HERE
        // Default state is   Enemy.EnemyState.STANDING

        testEnemy3 = new Enemy(Character.CharacterTypeName.ORANGE_MUSHROOM, 11, 7, world, physicsShapeCache); // THEN INITIALIZE HERE
        testEnemy3.setState(Character.CharacterState.MOVING); // SET STATES LIKE THIS
        testEnemy3.move(1, 0, 90);

        testEnemy4 = new Enemy(Character.CharacterTypeName.ORANGE_MUSHROOM, 16, 7, world, physicsShapeCache); // THEN INITIALIZE HERE
        testEnemy4.setState(Character.CharacterState.MOVING); // SET STATES LIKE THIS
        testEnemy4.faceRight();

        testEnemy5 = new Enemy(Character.CharacterTypeName.BIRD, 3, 9, world, physicsShapeCache); // THEN INITIALIZE HERE
        testEnemy5.setState(Character.CharacterState.MOVING); // SET STATES LIKE THIS
        testEnemy5.faceRight();

        testEnemy6 = new Enemy(Character.CharacterTypeName.BIRD, 3, 14, world, physicsShapeCache); // THEN INITIALIZE HERE
        testEnemy6.setState(Character.CharacterState.MOVING); // SET STATES LIKE THIS
        testEnemy6.faceRight();
        testEnemy6.move(3, 1, 6); // Movement as a vector. It gets normalized and then scaled

        playerCharacter = new PlayerCharacter(36, 23, world, physicsShapeCache);


        enemies = new ArrayList<>();
        int x = 40;
        int y = -10;
        for (Character.CharacterTypeName name : Character.CharacterTypeName.values()) {
            for (int i = 0; i < 6; i++) {
                enemies.add(new Enemy(name, x, y, world, physicsShapeCache));
                if (i == 1) {
                    enemies.get(enemies.size() - 1).setState(Character.CharacterState.MOVING);
                } else if (i == 2) {
                    enemies.get(enemies.size() - 1).setState(Character.CharacterState.DYING);
                } else if (i == 3) {
                    enemies.get(enemies.size() - 1).faceRight();
                } else if (i == 4) {
                    enemies.get(enemies.size() - 1).setState(Character.CharacterState.MOVING);
                    enemies.get(enemies.size() - 1).faceRight();
                } else if (i == 5) {
                    enemies.get(enemies.size() - 1).setState(Character.CharacterState.DYING);
                    enemies.get(enemies.size() - 1).faceRight();
                }
                if (x < 80) {
                    x += 8;
                } else {
                    y += 6;
                    x = 40;
                }
            }
        }


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
        camera.position.set(playerCharacter.getX(), playerCharacter.getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(theFloor,
                playerCharacter.getX() - 2160 * SCALE_FACTOR, playerCharacter.getY() - 1350 * SCALE_FACTOR,
                0, 0,
                4320, 2700,
                SCALE_FACTOR, SCALE_FACTOR,
                0,
                (int) (playerCharacter.getX() * 1 / SCALE_FACTOR), -(int) (playerCharacter.getY() * 1 / SCALE_FACTOR),
                4320, 2700,
                false, false
        );
        testEnemy.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy2.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy3.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy4.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy5.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        testEnemy6.animate(batch, elapsedTime); // AND DRAW LIKE THIS BETWEEN THE batch.begin() and batch.end()
        for (Enemy enemy : enemies) {
            enemy.animate(batch, elapsedTime);
        }
        playerCharacter.animate(batch, elapsedTime);
        batch.end();

        // DEBUG WIREFRAME:
        debugRenderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }


    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        theFloor.dispose();
        physicsShapeCache.dispose();
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
            if (testEnemy3.getX() >= 30) {
                testEnemy3.move(-2, 1, 90);
            } else if (testEnemy3.getX() <= 10) {
                testEnemy3.move(2, -1, 90);
            }
            // Do keyChecks here
            playerCharacter.keyCheck();
            //
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            return STEP_TIME;
        } else {
            return 0;
        }
    }
    // END SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics
}
