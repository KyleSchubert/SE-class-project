package com.survivors.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;

// Nick's imports
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static com.survivors.mygame.Character.CharacterState.DYING;

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

    // Nick: moved these to global range to be used efficiently in isOutOfCamera()
    private static final float windowWidth = 1440;
    private static final float windowHeight = 920;
    private static final float viewWidth = windowWidth * SCALE_FACTOR;
    private static final float viewHeight = windowHeight * SCALE_FACTOR;

    /* Nick: Kyle's test enemies
    Enemy testEnemy;  // ALWAYS DECLARE HERE
    Enemy testEnemy2; // ALWAYS DECLARE HERE
    Enemy testEnemy3; // ALWAYS DECLARE HERE
    Enemy testEnemy4; // ALWAYS DECLARE HERE
    Enemy testEnemy5; // ALWAYS DECLARE HERE
    Enemy testEnemy6; // ALWAYS DECLARE HERE
    */

    // Nick: GDX's Array class
    private Array<Enemy> activeEnemies = new Array<>();

    /* Nick: GDX's Pool class for reusing class instances instead of
     *       constantly destroying and recreating them
     */
    private final Pool<Enemy> enemyPool = new Pool<Enemy>() {
        @Override
        protected Enemy newObject() {
            return new Enemy(0.0f, 0.0f, world, physicsShapeCache);
        }
    };


    PlayerCharacter playerCharacter;

    @Override
    public void create() {
        world = new World(new Vector2(0, 0), true); // new Vector2(0, 0)  -  because we don't want gravity
        physicsShapeCache = new PhysicsShapeCache("physics.xml");
        batch = new SpriteBatch();

        // Loading in each CharacterTypeName's CharacterData into allCharacterData
        for (Character.CharacterTypeName name : Character.CharacterTypeName.values()) {
            ALL_CHARACTER_DATA.add(new CharacterData(name));
        }

        /* Nick: moved these declarations to global range within MyGame (see above)
         * float windowWidth  = Gdx.graphics.getWidth();
         * float windowHeight = Gdx.graphics.getHeight();
         */

        camera = new OrthographicCamera(viewWidth, viewHeight);

        viewport = new ExtendViewport(viewWidth, viewHeight, camera);
        theFloor = new Texture("testFloor1.png");

        theFloor.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        playerCharacter = new PlayerCharacter(36, 23, world, physicsShapeCache);
        /*
        The enemy images are set up in such a way that the origin of each monster will always
            be on the bottom and middle of the actual enemy drawing part of each sprite
        ESSENTIALLY, the origin is the center of the bottom of the "feet" of each enemy, even if it doesn't have feet.
        For example, the orange mushroom is animated to jump a bit when it moves.
            Its origin will be in the bottom middle, so for its jumping frame it
            will be slightly below and actually detached from its body by appearance.
         */
    }

    /* Nick: checks for enemies that can be removed from activeEnemies[] and thus
     *       returned to the pool, whether they are dead or offscreen & from an old wave
     */
    public void update() {
        for (int i = 0; i < activeEnemies.size; i++) {
            if (activeEnemies.get(i).getState() == DYING || isOutofCamera(activeEnemies.get(i), playerCharacter) && activeEnemies.get(i).oldwave) {
                activeEnemies.removeIndex(i);
            }
        }

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

        // Nick: animate each enemy in current list pulled from the pool
        for (Enemy E : activeEnemies) {
            E.animate(batch, elapsedTime);
        }


        playerCharacter.animate(batch, elapsedTime);
        batch.end();
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
            /* Nick: commented out this test enemy code
            if (testEnemy3.getX() >= 30) {
                testEnemy3.move(-2, 1, 90);
            } else if (testEnemy3.getX() <= 10) {
                testEnemy3.move(2, -1, 90);
            }
            */
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


    // Nick: tells if a given character (C) is out-of-view of the camera, relative to player (P)
    public static boolean isOutofCamera(Character C, Character P) {
        return (Math.abs(C.getX() - P.getX()) > viewWidth / 2) || (Math.abs(C.getY() - P.getY()) > viewHeight / 2);
    }

    //  viewWidth
    //  viewHeight

}

