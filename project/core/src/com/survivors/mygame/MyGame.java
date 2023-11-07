package com.survivors.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;
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
    ScreenViewport viewport;
    World world;
    SpriteBatch batch;
    PhysicsShapeCache physicsShapeCache;
    OrthographicCamera camera;
    ScreenViewport viewportForStage;
    Stage stage;
    public int amountOfCurrency;
    BitmapFont font;
    Image currencyCounterImage;
    ImageButton playButton;
    ImageButton upgradesButton;
    ImageButton settingsButton;
    ImageButton exitButton;
    Texture theFloor;
    public static final Array<CharacterData> ALL_CHARACTER_DATA = new Array<>();
    Box2DDebugRenderer debugRenderer;
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
     *       play-through of a level (aka total time - menu time - pause time), for the
     *       purpose of initiating waves at the proper point of the level
     */
    public float timeElapsedInGame = 0.0f;
    Enemy testEnemy; // ALWAYS DECLARE HERE
    Enemy testEnemy2; // ALWAYS DECLARE HERE
    Enemy testEnemy3; // ALWAYS DECLARE HERE
    Enemy testEnemy4; // ALWAYS DECLARE HERE
    Enemy testEnemy5; // ALWAYS DECLARE HERE
    Enemy testEnemy6; // ALWAYS DECLARE HERE
    private final Array<Enemy> activeEnemies = new Array<>();
    /* Nick: GDX's Pool class for reusing class instances instead of
     *       constantly destroying and recreating them
     */
    private final Pool<Enemy> enemyPool = new Pool<Enemy>() {
        @Override
        protected Enemy newObject() {
            return new Enemy();
        }
    };
    // Nick: Wave data can probably be gotten from a file to pass to WaveList constructor
    private WaveList theWaveList;
    // Currently used to spawn enemies in a random location just outside of player's view
    Random rand = new Random();
    PlayerCharacter playerCharacter;
    ArrayList<Enemy> enemies; // For the grid of each enemy. For testing

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

        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(SCALE_FACTOR);

        theFloor = new Texture("testFloor1.png");

        theFloor.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        debugRenderer = new Box2DDebugRenderer();

        // For the UI and menus
        viewportForStage = new ScreenViewport(camera);
        viewportForStage.setUnitsPerPixel(SCALE_FACTOR);
        stage = new Stage(viewportForStage);

        Gdx.input.setInputProcessor(stage);

        // Load the in-game currency counter
        amountOfCurrency = 0;
        currencyCounterImage = new Image(new Texture(Gdx.files.internal("ITEMS/doubloon.png")));
        currencyCounterImage.setSize(29 * SCALE_FACTOR, 30 * SCALE_FACTOR);
        stage.addActor(currencyCounterImage);
        font = new BitmapFont(Gdx.files.internal("font.fnt"), false);
        font.setUseIntegerPositions(false);
        font.getData().setScale(SCALE_FACTOR, SCALE_FACTOR);

        // Menu buttons below
        // PLAY button
        playButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("MENU BUTTONS/play/default.png")))),
                new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("MENU BUTTONS/play/hover.png"))))
        );
        playButton.setSize(362 * SCALE_FACTOR, 122 * SCALE_FACTOR);
        playButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("CLICKED UP");
                amountOfCurrency++;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("CLICKED DOWN");
                return true;
            }
        });
        stage.addActor(playButton);

        // UPGRADES button
        upgradesButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("MENU BUTTONS/upgrades/default.png")))),
                new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("MENU BUTTONS/upgrades/hover.png"))))
        );
        upgradesButton.setSize(248 * SCALE_FACTOR, 72 * SCALE_FACTOR);
        upgradesButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("CLICKED UP");
                amountOfCurrency += 10;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("CLICKED DOWN");
                return true;
            }
        });
        stage.addActor(upgradesButton);

        // SETTINGS button
        settingsButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("MENU BUTTONS/settings/default.png")))),
                new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("MENU BUTTONS/settings/hover.png"))))
        );
        settingsButton.setSize(224 * SCALE_FACTOR, 72 * SCALE_FACTOR);
        settingsButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("CLICKED UP");
                amountOfCurrency += 100;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("CLICKED DOWN");
                return true;
            }
        });
        stage.addActor(settingsButton);

        // EXIT button
        exitButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("MENU BUTTONS/exit/default.png")))),
                new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("MENU BUTTONS/exit/hover.png"))))
        );
        exitButton.setSize(152 * SCALE_FACTOR, 72 * SCALE_FACTOR);
        exitButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                System.exit(-1);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(exitButton);

        // WAVES
        Array<Wave> temporaryArrayOfWavesBeforeWeMakeAWavelistFile = new Array<>();
        theWaveList = new WaveList(temporaryArrayOfWavesBeforeWeMakeAWavelistFile);

        testEnemy = new Enemy();
        testEnemy.init(Character.CharacterTypeName.BIRD, 3.2f, 4.2f, 0, world, physicsShapeCache);

        testEnemy2 = new Enemy();
        testEnemy2.init(Character.CharacterTypeName.ORANGE_MUSHROOM, 6, 7, 0, world, physicsShapeCache);

        testEnemy3 = new Enemy();
        testEnemy3.init(Character.CharacterTypeName.ORANGE_MUSHROOM, 11, 7, 0, world, physicsShapeCache);
        testEnemy3.setState(Character.CharacterState.MOVING);
        testEnemy3.move(1, 0, 90);

        testEnemy4 = new Enemy();
        testEnemy4.init(Character.CharacterTypeName.ORANGE_MUSHROOM, 16, 7, 0, world, physicsShapeCache);
        testEnemy4.setState(Character.CharacterState.MOVING);
        testEnemy4.faceRight();

        testEnemy5 = new Enemy();
        testEnemy5.init(Character.CharacterTypeName.BIRD, 3, 9, 0, world, physicsShapeCache);
        testEnemy5.setState(Character.CharacterState.MOVING);
        testEnemy5.faceRight();

        testEnemy6 = new Enemy();
        testEnemy6.init(Character.CharacterTypeName.BIRD, 3, 14, 0, world, physicsShapeCache);
        testEnemy6.setState(Character.CharacterState.MOVING);
        testEnemy6.faceRight();
        testEnemy6.move(3, 1, 6); // Movement as a vector. It gets normalized and then scaled

        playerCharacter = new PlayerCharacter(36, 23, world, physicsShapeCache);


        enemies = new ArrayList<>();
        int x = 40;
        int y = -10;
        for (Character.CharacterTypeName name : Character.CharacterTypeName.values()) {
            for (int i = 0; i < 6; i++) {
                Enemy enemyGridEnemy = new Enemy();
                enemyGridEnemy.init(name, x, y, 0, world, physicsShapeCache);
                enemies.add(enemyGridEnemy);
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
    }


    @Override
    public void render() {
        float elapsedTime = stepWorld();
        ScreenUtils.clear(0, 0, 0, 1);
        camera.position.set(playerCharacter.getX(), playerCharacter.getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        viewport.apply();
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
        font.draw(batch, "x " + amountOfCurrency, playerCharacter.getX() - 19.2f, playerCharacter.getY() - 9.7f); // text for currency counter
        batch.end();

        // MOVE THE MENU BUTTONS, or we could prevent the player from being moved
        stage.getActors().get(0).setPosition(playerCharacter.getX() - 21, playerCharacter.getY() - 11); // currencyCounterImage
        stage.getActors().get(1).setPosition(playerCharacter.getX() - 35, playerCharacter.getY() - 7); // playButton
        stage.getActors().get(2).setPosition(playerCharacter.getX() - 35, playerCharacter.getY() - 12); // upgradesButton
        stage.getActors().get(3).setPosition(playerCharacter.getX() - 35, playerCharacter.getY() - 17); // settingsButton
        stage.getActors().get(4).setPosition(playerCharacter.getX() - 35, playerCharacter.getY() - 22); // exitButton

        stage.getViewport().apply();
        stage.act(elapsedTime);
        stage.draw();
        // DEBUG WIREFRAME:
        debugRenderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }


    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        theFloor.dispose();
        physicsShapeCache.dispose();
        stage.dispose();
        font.dispose();
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
            if (E.getState() == DYING || isOutOfCamera(E, playerCharacter) && E.fromOldWave()) {
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
     *       enemy's init() function to assign to it its proper stats and a random
     *       location somewhere outside the player's view.
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
    public static boolean isOutOfCamera(Character C, Character P) {
        return (Math.abs(C.getX() - P.getX()) > viewWidth / 2) || (Math.abs(C.getY() - P.getY()) > viewHeight / 2);
    }
}

