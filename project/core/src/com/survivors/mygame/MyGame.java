package com.survivors.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import static com.survivors.mygame.Character.CharacterState.DEAD;
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
    Stage mainMenuStage;
    Stage pauseMenuStage;
    Stage resultsMenuStage;
    Stage levelUpMenuStage;
    Stage upgradesMenuStage;
    Stage settingsMenuStage;
    public int amountOfCurrency;
    BitmapFont font;
    Image currencyCounterImage;
    ImageButton playButton;
    ImageButton upgradesButton;
    ImageButton settingsButton;
    ImageButton exitButton;
    ImageButton resumeButton;
    ImageButton giveUpButton;
    ImageButton pauseSettingsButton;
    ImageButton mainMenuButton;
    ImageButton levelUpConfirmButton1;
    ImageButton levelUpConfirmButton2;
    ImageButton levelUpConfirmButton3;
    ImageButton upgradesBackButton;
    ImageButton resetButton;
    ImageButton settingsBackButton;
    ImageButton settingsConfirmButton;
    Image darkTransparentScreen;
    Image pauseBackground;
    Image resultsBackground;
    Image settingsBackground;
    Image upgradesBackground;
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
    private String timeText = "0:00";
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

    // Menu variables and stuff below:
    public enum MenuState {
        MAIN_MENU, PLAYING, PAUSED, RESULTS, LEVEL_UP, UPGRADES, SETTINGS_BACK, SETTINGS
    }

    protected boolean isGameplayPaused; // [1] is the corresponding action in setMenuState()
    private boolean isDrawGameplayObjects; // [3] is the corresponding action in setMenuState()
    private boolean isDrawMainMenu; // [4] is the corresponding action in setMenuState()
    private boolean isDrawDarkTransparentScreen; // [5] is the corresponding action in setMenuState()
    private boolean isDrawPauseMenu; // [8] is the corresponding action in setMenuState()
    private boolean isDrawResultsMenu; // [9] is the corresponding action in setMenuState()
    private boolean isDrawLevelUpMenu; // [10] is the corresponding action in setMenuState()
    private boolean isDrawUpgradesMenu; // [11] is the corresponding action in setMenuState()
    private boolean isDrawSettingsMenu; // [12] is the corresponding action in setMenuState()
    // End of menu variables and stuff

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
        mainMenuStage = new Stage(viewportForStage);
        pauseMenuStage = new Stage(viewportForStage);
        resultsMenuStage = new Stage(viewportForStage);
        levelUpMenuStage = new Stage(viewportForStage);
        upgradesMenuStage = new Stage(viewportForStage);
        settingsMenuStage = new Stage(viewportForStage);

        Gdx.input.setInputProcessor(mainMenuStage);

        // Load the in-game currency counter
        amountOfCurrency = 0;
        currencyCounterImage = new Image(new Texture(Gdx.files.internal("ITEMS/doubloon.png")));
        currencyCounterImage.setSize(29 * SCALE_FACTOR, 30 * SCALE_FACTOR);
        mainMenuStage.addActor(currencyCounterImage);
        font = new BitmapFont(Gdx.files.internal("font.fnt"), false);
        font.setUseIntegerPositions(false);
        font.getData().setScale(SCALE_FACTOR, SCALE_FACTOR);

        // Menu buttons below
        // PLAY button
        playButton = newImageButtonFrom("play", MenuState.PLAYING);
        mainMenuStage.addActor(playButton);

        // UPGRADES button
        upgradesButton = newImageButtonFrom("upgrades", MenuState.UPGRADES);
        mainMenuStage.addActor(upgradesButton);

        // SETTINGS button
        settingsButton = newImageButtonFrom("settings", MenuState.SETTINGS);
        mainMenuStage.addActor(settingsButton);

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
        mainMenuStage.addActor(exitButton);

        // Dark transparent screen
        darkTransparentScreen = new Image(new Texture(Gdx.files.internal("MENU backgrounds/dark transparent screen.png")));
        darkTransparentScreen.setSize(40 * SCALE_FACTOR * 300, 40 * SCALE_FACTOR * 300);

        // Pause background
        pauseBackground = new Image(new Texture(Gdx.files.internal("MENU backgrounds/pause background.png")));
        pauseBackground.setSize(296 * SCALE_FACTOR, 287 * SCALE_FACTOR);

        // Results background
        resultsBackground = new Image(new Texture(Gdx.files.internal("MENU backgrounds/results background.png")));
        resultsBackground.setSize(429 * SCALE_FACTOR, 844 * SCALE_FACTOR);

        // Settings background
        settingsBackground = new Image(new Texture(Gdx.files.internal("MENU backgrounds/settings background.png")));
        settingsBackground.setSize(771 * SCALE_FACTOR, 814 * SCALE_FACTOR);

        // Upgrades background
        upgradesBackground = new Image(new Texture(Gdx.files.internal("MENU backgrounds/upgrades background.png")));
        upgradesBackground.setSize(873 * SCALE_FACTOR, 814 * SCALE_FACTOR);

        // Pause menu buttons
        // Resume button
        resumeButton = newImageButtonFrom("resume", MenuState.PLAYING);
        pauseMenuStage.addActor(resumeButton);

        // Settings button
        pauseSettingsButton = newImageButtonFrom("settings", MenuState.SETTINGS);
        pauseMenuStage.addActor(pauseSettingsButton);

        // Give up button
        giveUpButton = newImageButtonFrom("give up", MenuState.RESULTS);
        pauseMenuStage.addActor(giveUpButton);

        // Results menu buttons
        // Main menu button
        mainMenuButton = newImageButtonFrom("main menu", MenuState.MAIN_MENU);
        resultsMenuStage.addActor(mainMenuButton);

        // Level up menu buttons
        // TODO: these will need more code in order to grant the player their corresponding reward, of course
        levelUpConfirmButton1 = newImageButtonFrom("confirm", MenuState.PLAYING);
        levelUpConfirmButton2 = newImageButtonFrom("confirm", MenuState.PLAYING);
        levelUpConfirmButton3 = newImageButtonFrom("confirm", MenuState.PLAYING);
        levelUpMenuStage.addActor(levelUpConfirmButton1);
        levelUpMenuStage.addActor(levelUpConfirmButton2);
        levelUpMenuStage.addActor(levelUpConfirmButton3);

        // Upgrades menu buttons
        // Back button
        upgradesBackButton = newImageButtonFrom("back", MenuState.MAIN_MENU);
        upgradesMenuStage.addActor(upgradesBackButton);

        // Reset button (for resetting all upgrades and returning the in-game currency spent on them)
        // TODO: this will need additional code to refund all the upgrades
        resetButton = newImageButtonFrom("reset", MenuState.UPGRADES);
        upgradesMenuStage.addActor(resetButton);

        // Settings menu buttons
        // Back button
        // TODO: this will need additional code to discard the changed settings
        settingsBackButton = newImageButtonFrom("back", MenuState.SETTINGS_BACK);
        settingsMenuStage.addActor(settingsBackButton);

        // confirm button
        // TODO: this will need additional code to save and apply the changed settings
        settingsConfirmButton = newImageButtonFrom("confirm", MenuState.SETTINGS_BACK);
        settingsMenuStage.addActor(settingsConfirmButton);

        // WAVES
        // Array<Wave> temporaryArrayOfWavesBeforeWeMakeAWavelistFile = new Array<>();
        try {
            theWaveList = new WaveList("SampleWaves.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

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

        setMenuState(MenuState.MAIN_MENU);
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
        if (!this.isDrawGameplayObjects) {
            elapsedTime = 0.0f;
        }

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
            //enemy.takeDamage(100);
            enemy.animate(batch, elapsedTime);
        }
        playerCharacter.animate(batch, elapsedTime);

        if (this.isDrawMainMenu) {
            font.draw(batch, "x " + amountOfCurrency, playerCharacter.getX() - 19.2f, playerCharacter.getY() - 9.7f); // text for currency counter
        }
        font.draw(batch, timeText, playerCharacter.getX() - 1.4f, playerCharacter.getY() + 22.6f); // text for time elapsed in game

        if (this.isDrawMainMenu) {
            mainMenuStage.getActors().get(0).setPosition(playerCharacter.getX() - 21, playerCharacter.getY() - 11); // currencyCounterImage
            mainMenuStage.getActors().get(1).setPosition(playerCharacter.getX() - 35, playerCharacter.getY() - 7); // playButton
            mainMenuStage.getActors().get(2).setPosition(playerCharacter.getX() - 35, playerCharacter.getY() - 12); // upgradesButton
            mainMenuStage.getActors().get(3).setPosition(playerCharacter.getX() - 35, playerCharacter.getY() - 17); // settingsButton
            mainMenuStage.getActors().get(4).setPosition(playerCharacter.getX() - 35, playerCharacter.getY() - 22); // exitButton
            mainMenuStage.getViewport().apply();
            mainMenuStage.act(elapsedTime);
            mainMenuStage.draw();
        }
        if (this.isDrawDarkTransparentScreen) {
            // draw the dark transparent screen
            darkTransparentScreen.setPosition(playerCharacter.getX() - 100, playerCharacter.getY() - 100);
            darkTransparentScreen.draw(batch, 1);
        }
        if (this.isDrawPauseMenu) { // JUST for the pause menu background texture
            pauseBackground.setPosition(playerCharacter.getX() - 7.5f, playerCharacter.getY() - 5);
            pauseBackground.draw(batch, 1);
        }
        if (this.isDrawUpgradesMenu) { // JUST for the upgrades menu background texture
            upgradesBackground.setPosition(playerCharacter.getX() - 14, playerCharacter.getY() - 21.3f);
            upgradesBackground.draw(batch, 1);
        }
        if (this.isDrawResultsMenu) { // JUST for the results menu background texture
            resultsBackground.setPosition(playerCharacter.getX() + 11, playerCharacter.getY() - 21.3f);
            resultsBackground.draw(batch, 1);
        }
        if (this.isDrawSettingsMenu) { // JUST for the settings menu background texture
            settingsBackground.setPosition(playerCharacter.getX() - 13.3f, playerCharacter.getY() - 21.3f);
            settingsBackground.draw(batch, 1);
        }
        // TODO: add a level up menu background texture
        batch.end();

        if (this.isDrawPauseMenu) {
            // draw the pause menu
            pauseMenuStage.getActors().get(0).setPosition(playerCharacter.getX() - 6, playerCharacter.getY() + 4.2f); // resume button
            pauseMenuStage.getActors().get(1).setPosition(playerCharacter.getX() - 6, playerCharacter.getY() + 0.1f); // settings button
            pauseMenuStage.getActors().get(2).setPosition(playerCharacter.getX() - 5.5f, playerCharacter.getY() - 4.5f); // give up button
            pauseMenuStage.getViewport().apply();
            pauseMenuStage.act(elapsedTime);
            pauseMenuStage.draw();
        }
        if (this.isDrawLevelUpMenu) {
            // draw the level-up menu
            levelUpMenuStage.getActors().get(0).setPosition(playerCharacter.getX() - 15, playerCharacter.getY() - 16); // confirm button 1
            levelUpMenuStage.getActors().get(1).setPosition(playerCharacter.getX() - 0, playerCharacter.getY() - 16); // confirm button 2
            levelUpMenuStage.getActors().get(2).setPosition(playerCharacter.getX() + 15, playerCharacter.getY() - 16); // confirm button 3
            levelUpMenuStage.getViewport().apply();
            levelUpMenuStage.act(elapsedTime);
            levelUpMenuStage.draw();
        }
        if (this.isDrawUpgradesMenu) {
            // draw the upgrades menu
            upgradesMenuStage.getActors().get(0).setPosition(playerCharacter.getX() - 12, playerCharacter.getY() - 20); // back button
            upgradesMenuStage.getActors().get(1).setPosition(playerCharacter.getX() + 18, playerCharacter.getY() - 20); // reset button
            upgradesMenuStage.getViewport().apply();
            upgradesMenuStage.act(elapsedTime);
            upgradesMenuStage.draw();
        }
        if (this.isDrawResultsMenu) {
            // draw the results menu
            resultsMenuStage.getActors().get(0).setPosition(playerCharacter.getX() + 15, playerCharacter.getY() - 20); // main menu button
            resultsMenuStage.getViewport().apply();
            resultsMenuStage.act(elapsedTime);
            resultsMenuStage.draw();
        }
        if (this.isDrawSettingsMenu) {
            // draw the settings menu
            settingsMenuStage.getActors().get(0).setPosition(playerCharacter.getX() - 12, playerCharacter.getY() - 20); // back button
            settingsMenuStage.getActors().get(1).setPosition(playerCharacter.getX() + 12, playerCharacter.getY() - 20); // confirm button
            settingsMenuStage.getViewport().apply();
            settingsMenuStage.act(elapsedTime);
            settingsMenuStage.draw();
        }
        // DEBUG WIREFRAME:
        debugRenderer.render(world, camera.combined);

        // TODO: remove this. This is just for testing the DEAD state and if the enemies die when their hp goes to 0
        for (int i = enemies.size() - 1; i >= 0; i--) {
            if (enemies.get(i).getState() == DEAD) { // TODO: make things of state DYING not able to be collided with
                world.destroyBody(enemies.get(i).getBody());
                enemies.remove(i);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        mainMenuStage.getViewport().update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }


    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        theFloor.dispose();
        physicsShapeCache.dispose();
        mainMenuStage.dispose();
        pauseMenuStage.dispose();
        resultsMenuStage.dispose();
        levelUpMenuStage.dispose();
        upgradesMenuStage.dispose();
        settingsMenuStage.dispose();
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
            if (!this.isGameplayPaused) {
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
                timeElapsedInGame += STEP_TIME;
                int minutes = (int) timeElapsedInGame / 60;
                int seconds = (int) timeElapsedInGame - minutes * 60;
                if (seconds < 10) {
                    timeText = minutes + ":0" + seconds;
                } else {
                    timeText = minutes + ":" + seconds;
                }

                if (testEnemy3.getX() >= 30) {
                    testEnemy3.move(-2, 1, 90);
                } else if (testEnemy3.getX() <= 10) {
                    testEnemy3.move(2, -1, 90);
                }

                // Do keyChecks here
                playerCharacter.keyCheck();
                // Check for ESCAPE key -- Toggle pause menu
                if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                    setMenuState(MenuState.PAUSED);
                }
                world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            }
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
        for (int i = activeEnemies.size - 1; i >= 0; i--) {
            if (activeEnemies.get(i).getState() == DEAD || isOutOfCamera(activeEnemies.get(i), playerCharacter) && activeEnemies.get(i).fromOldWave()) {
                enemyPool.free(activeEnemies.get(i));
                world.destroyBody(activeEnemies.get(i).getBody());
                activeEnemies.removeIndex(i);
            }
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

    public void setMenuState(MenuState menuState) {
        switch (menuState) {
            case MAIN_MENU:
                // GAME STARTS IN THIS STATE
                // POTENTIAL PREVIOUS STATES: upgrades, settings, results
                Gdx.input.setInputProcessor(mainMenuStage);
                setGameplayPaused(true); // [1] disable movement and pause the timer
                // [2] reset PlayerCharacter.
                playerCharacter.setState(Character.CharacterState.STANDING);
                playerCharacter.faceRight();
                // more stuff, like resetting their stats and items
                //
                setDrawGameplayObjects(true); // [3] enable Character, floor, and skill drawing.
                setDrawMainMenu(true); // [4] enable drawing main menu
                setDrawDarkTransparentScreen(false); // [5] disable drawing the dark transparent screen that makes other menus more visible
                this.timeElapsedInGame = 0f; // [6] reset timer
                // [7] remove all enemies
                activeEnemies.clear();
                theWaveList.curWave = 0;
                // unsure what else we'll need to do to reset the waves
                //
                setDrawPauseMenu(false); // Needed for initialization: [8] disable drawing the pause menu
                setDrawResultsMenu(false); // [9] disable drawing the results menu
                setDrawLevelUpMenu(false); // Needed for initialization: [10] disable drawing the level_up menu
                setDrawUpgradesMenu(false); // [11] disable drawing the upgrades menu
                setDrawSettingsMenu(false); // [12] disable drawing the settings menu
                break;
            case PLAYING:
                // POTENTIAL PREVIOUS STATES: paused, main_menu, level_up
                setGameplayPaused(false); // [1] enable movement and unpause the timer
                setDrawGameplayObjects(true); // [3] enable Character, floor, and skill drawing.
                setDrawMainMenu(false); // [4] disable drawing main menu
                setDrawDarkTransparentScreen(false); // [5] disable drawing the dark transparent screen that makes other menus more visible
                setDrawPauseMenu(false); // [8] disable drawing the pause menu
                setDrawLevelUpMenu(false); // [10] disable drawing the level_up menu
                break;
            case PAUSED:
                // POTENTIAL PREVIOUS STATES: playing, settings
                Gdx.input.setInputProcessor(pauseMenuStage);
                setGameplayPaused(true); // [1] disable movement and pause the timer
                setDrawGameplayObjects(false); // [3] disable Character, floor, and skill drawing.
                setDrawDarkTransparentScreen(true); // [5] enable drawing the dark transparent screen that makes other menus more visible
                setDrawPauseMenu(true); // [8] enable drawing the pause menu
                setDrawSettingsMenu(false); // [12] disable drawing the settings menu
                break;
            case RESULTS:
                // POTENTIAL PREVIOUS STATES: paused (ended round), playing
                Gdx.input.setInputProcessor(resultsMenuStage);
                setGameplayPaused(true); // [1] disable movement and pause the timer
                setDrawGameplayObjects(false); // [3] disable Character, floor, and skill drawing.
                setDrawDarkTransparentScreen(true); // [5] enable drawing the dark transparent screen that makes other menus more visible
                setDrawPauseMenu(false); // [8] disable drawing the pause menu
                setDrawResultsMenu(true); // [9] enable drawing the results menu
                break;
            case LEVEL_UP:
                // POTENTIAL PREVIOUS STATES: playing
                Gdx.input.setInputProcessor(levelUpMenuStage);
                setGameplayPaused(true); // [1] disable movement and pause the timer
                setDrawGameplayObjects(false); // [3] disable Character, floor, and skill drawing.
                setDrawDarkTransparentScreen(true); // [5] enable drawing the dark transparent screen that makes other menus more visible
                setDrawLevelUpMenu(true); // [10] enable drawing the level_up menu
                break;
            case UPGRADES:
                // POTENTIAL PREVIOUS STATES: main_menu
                Gdx.input.setInputProcessor(upgradesMenuStage);
                setDrawDarkTransparentScreen(true); // [5] enable drawing the dark transparent screen that makes other menus more visible
                setDrawUpgradesMenu(true); // [11] enable drawing the upgrades menu
                break;
            case SETTINGS:
                // POTENTIAL PREVIOUS STATES: paused, main_menu
                Gdx.input.setInputProcessor(settingsMenuStage);
                setDrawDarkTransparentScreen(true); // [5] enable drawing the dark transparent screen that makes other menus more visible
                setDrawPauseMenu(false); // [8] enable drawing the pause menu
                setDrawSettingsMenu(true); // [12] enable drawing the settings menu
                break;
            case SETTINGS_BACK: // exclusive to the buttons in the settings menu
                if (isDrawMainMenu) {
                    Gdx.input.setInputProcessor(mainMenuStage);
                    setDrawDarkTransparentScreen(false); // [5] disable drawing the dark transparent screen that makes other menus more visible
                } else {
                    Gdx.input.setInputProcessor(pauseMenuStage);
                    setDrawPauseMenu(true); // [8] enable drawing the pause menu
                }
                setDrawSettingsMenu(false); // [12] disable drawing the settings menu
                break;
        }
    }

    public void setGameplayPaused(boolean gameplayPaused) {
        isGameplayPaused = gameplayPaused;
    }

    public void setDrawGameplayObjects(boolean drawGameplayObjects) {
        this.isDrawGameplayObjects = drawGameplayObjects;
    }

    public void setDrawMainMenu(boolean drawMainMenu) {
        this.isDrawMainMenu = drawMainMenu;
    }

    public void setDrawDarkTransparentScreen(boolean drawDarkTransparentScreen) {
        this.isDrawDarkTransparentScreen = drawDarkTransparentScreen;
    }

    public void setDrawPauseMenu(boolean drawPauseMenu) {
        this.isDrawPauseMenu = drawPauseMenu;
    }

    public void setDrawResultsMenu(boolean drawResultsMenu) {
        this.isDrawResultsMenu = drawResultsMenu;
    }

    public void setDrawLevelUpMenu(boolean drawLevelUpMenu) {
        this.isDrawLevelUpMenu = drawLevelUpMenu;
    }

    public void setDrawUpgradesMenu(boolean drawUpgradesMenu) {
        this.isDrawUpgradesMenu = drawUpgradesMenu;
    }

    public void setDrawSettingsMenu(boolean drawSettingsMenu) {
        this.isDrawSettingsMenu = drawSettingsMenu;
    }

    private ImageButton newImageButtonFrom(String buttonInternalFolderName, final MenuState menuState) {
        Texture notClickedTexture = new Texture(Gdx.files.internal("MENU BUTTONS/" + buttonInternalFolderName + "/default.png"));
        Texture clickedTexture = new Texture(Gdx.files.internal("MENU BUTTONS/" + buttonInternalFolderName + "/hover.png"));
        ImageButton button = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(notClickedTexture)),
                new TextureRegionDrawable(new TextureRegion(clickedTexture))
        );
        button.setSize(notClickedTexture.getWidth() * SCALE_FACTOR, notClickedTexture.getHeight() * SCALE_FACTOR);
        button.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setMenuState(menuState);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        return button;
    }
}

