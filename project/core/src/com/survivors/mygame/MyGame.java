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

import java.util.Random;

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

    /* Nick: Represents the size of the area outside the camera at which enemies
     *       can spawn; 0.5 means the area's width and height extends out from the
     *       camera edges by 0.5 * the camera width and height, respectively
     */
    private final float enemySpawnSize = 0.5f;

    /* Nick: this should keep track of how much time has passed in the player's current
     *       playthrough of a level (aka total time - menu time - pause time), for the
     *       purpose of initiating waves at the proper point of the level
     */
    public float timeElapsedInGame = 0.0f;

    /* Nick: Kyle's test enemies
    Enemy testEnemy;  // ALWAYS DECLARE HERE
    Enemy testEnemy2; // ALWAYS DECLARE HERE
    Enemy testEnemy3; // ALWAYS DECLARE HERE
    Enemy testEnemy4; // ALWAYS DECLARE HERE
    Enemy testEnemy5; // ALWAYS DECLARE HERE
    Enemy testEnemy6; // ALWAYS DECLARE HERE
    */

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

    // Nick: Wave data can probably be gotten from a file to pass to WaveList constructor
    private WaveList theWaveList;

    // Currently used to spawn enemies in a random location just outside of player's view
    Random rand = new Random();

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

            /* Nick: each frame (if in a game and unpaused) we want to:
             *       1) check for enemies to remove and return to the pool,
             *       2) attempt to add new enemies to activeEnemies[] from the pool, using current wave as a guide,
             *       3) Check to see if the next wave is ready to begin
             *             a) if next wave is ready, mark all enemies that are from previous waves
             *       4) increment timeElapsedInGame if player is in a level and unpaused
             */

            // Part 1)
            removeStaleEnemies();
            // Part 2)
            addNewEnemies();
            // Part 3)
            // if it is time to advance to the next wave:
            if (theWaveList.advanceWave(timeElapsedInGame)) {
                // mark all active enemies that are from old waves
                int curWave = theWaveList.getCurWave();
                for (Enemy E : activeEnemies) {
                    if (E.getSpawnedWave() < curWave)
                        E.markOldWave();
                }
            }

            // Part 4)
            // SHOULD only increment when player is in a level and unpaused
            timeElapsedInGame += delta;

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


    /* Nick: This method finds all enemies to be removed (and thus freeing up pool space)
     *       by finding each enemy instance that is either dead or both from a previous
     *       wave and offscreen. It uses a list of indices of enemies to remove because
     *       removing by element from an array is unpredictable (removes the first element
     *       in the array equal to the thing we're removing).
     *
     *       In the future I may want to have each enemy tell a global array when said enemy
     *       dies, this way we don't need to loop through every enemy in-game every frame. */
    public void removeStaleEnemies() {

        Array<Integer> indicesToRemove = new Array<>();

        int curIndex = 0;
        for (Enemy E : activeEnemies) {
            if (E.getState() == DYING || isOutofCamera(E, playerCharacter) && E.fromOldWave()) {
                indicesToRemove.add(curIndex);
            }
            curIndex++;
        }

        // removing from right-to-left so indices stay intact
        for (int i = indicesToRemove.size - 1; i >= 0; i--) {
            enemyPool.free(activeEnemies.items[indicesToRemove.items[i]]);
            activeEnemies.removeIndex(indicesToRemove.items[i]);
        }
    }

    /* Nick: this method attempts to add a new enemy to the game from the current wave.
     *       It communicates with a WaveList instance to get the ID of the next enemy
     *       to be added to the game, takes an enemy from the pool, and uses this
     *       enemy's init() function to assign to it it's proper stats and a random
     *       location somewhere outside of the player's view.
     *
     *       In the future I may add an algorithm to add multiple enemies during a single
     *       frame, but not sure how to go about this yet */
    public void addNewEnemies() {
        // get a "new" enemy from the pool
        Enemy newEnemy = enemyPool.obtain();

        // Initialize the enemy's values:
        Character.CharacterTypeName newType = theWaveList.takeEnemy();
        int curWave = theWaveList.getCurWave();
        float newX = playerCharacter.getX();
        float newY = playerCharacter.getY();
        float deltaX = rand.nextFloat(enemySpawnSize * windowWidth) + windowWidth / 2;
        float deltaY = rand.nextFloat(enemySpawnSize * windowHeight) + windowHeight / 2;

        if (rand.nextInt() == 0)
            // newX = (playerX) - (random value to the left of camera)
            newX -= deltaX;
        else
            // newX = (playerX) + (random value to the right of camera)
            newX += deltaX;

        if (rand.nextInt() == 0)
            // newY = (playerY) + (random value below the camera)
            newY -= deltaY;
        else
            // newY = (playerY) - (random value below the camera)
            newY += deltaY;

        // Initialize the new values, overwriting this enemy's previous values
        newEnemy.init(newType, newX, newY, curWave, world, physicsShapeCache);

        // Add the new enemy to activeEnemies[]
        activeEnemies.add(newEnemy);
    }

    // Nick: tells if a given character (C) is out-of-view of the camera, relative to player (P)
    public static boolean isOutofCamera(Character C, Character P) {
        return (Math.abs(C.getX() - P.getX()) > viewWidth / 2) || (Math.abs(C.getY() - P.getY()) > viewHeight / 2);
    }
}

