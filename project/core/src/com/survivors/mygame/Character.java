package com.survivors.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.survivors.mygame.MyGame.ALL_CHARACTER_DATA;
import static com.survivors.mygame.MyGame.SCALE_FACTOR;


public class Character extends Mobile {
    public enum CharacterState {
        DYING, MOVING, STANDING, ATTACKING, DEAD
    }

    public enum CharacterTypeName {
        // Nick: Added the VOID typename to denote a newly created enemy in a pool before use
        BIRD, PLANT, STUMP, PIG, ORANGE_MUSHROOM, BLUE_MUSHROOM, ZOMBIE_MUSHROOM, HELMET_PENGUIN, SPEAR_PENGUIN, SMALL_PENGUIN, VOID
    }

    /* Nick: I made characterData public and changeable, as pool objects will
     *       have their attributes changed upon being used, instead of having
     *       a new constructor called each time
     */
    private int dataIndex;
    private CharacterState state;
    private float frameTime = 0;
    private int frame = 0;
    /**
     * All frames are in the same structure accessible by index, so from stateFrameStartIndex TO stateFrameEndIndex,
     * the frames and frame delays of the specific character's current state (like STANDING or MOVING) can be found.
     * It uses start and end so that the animations can be looped while keeping all states' data in the same data structure.
     */
    private int stateFrameStartIndex = 0;
    private int stateFrameEndIndex = 0;
    private int isFacingLeft = 1;
    private int currentHp;

    /**
     * @param characterTypeName The type of the character that will be created. This is an enum: CharacterTypeName
     * @param x                 The x coordinate of the spawning position of the Character.
     * @param y                 The y coordinate of the spawning position of the Character.
     */
    public Character(CharacterTypeName characterTypeName, float x, float y, World world, PhysicsShapeCache physicsShapeCache) {
        x -= ALL_CHARACTER_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR;
        y -= (ALL_CHARACTER_DATA.get(dataIndex).getDimensionY() - ALL_CHARACTER_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR;
        this.dataIndex = characterTypeName.ordinal();
        if (!ALL_CHARACTER_DATA.get(dataIndex).getInternalName().equals("void")) {
            this.makeBody(ALL_CHARACTER_DATA.get(dataIndex).getInternalName(), x, y, 0, world, physicsShapeCache);
            this.currentHp = ALL_CHARACTER_DATA.get(dataIndex).getMaxHp();
        }
        this.setState(CharacterState.STANDING);
    }

    public Character() {
        this.dataIndex = CharacterTypeName.VOID.ordinal();
    }

    public CharacterState getState() {
        return state;
    }

    /**
     * Also, don't set to the DEAD state here. It happens automatically
     *
     * @param state The state of the character that you want it to be changed to. Ex: Character.CharacterState.MOVING
     */
    public void setState(CharacterState state) {
        if (!state.equals(this.state)) {
            if (state == CharacterState.ATTACKING && !ALL_CHARACTER_DATA.get(dataIndex).isAbleToAttack()) {
                return;
            } else {
                this.state = state;
            }
            prepareFrameStartAndEndIndex();
            this.frame = this.stateFrameStartIndex;
        }
    }

    private void prepareFrameStartAndEndIndex() {
        switch (this.state) {
            case DYING:
                this.stateFrameStartIndex = ALL_CHARACTER_DATA.get(dataIndex).getDyingAnimationStartFrameIndex();
                this.stateFrameEndIndex = ALL_CHARACTER_DATA.get(dataIndex).getDyingAnimationEndFrameIndex();
                break;
            case MOVING:
                this.stateFrameStartIndex = ALL_CHARACTER_DATA.get(dataIndex).getMovingAnimationStartFrameIndex();
                this.stateFrameEndIndex = ALL_CHARACTER_DATA.get(dataIndex).getMovingAnimationEndFrameIndex();
                break;
            case STANDING:
                this.stateFrameStartIndex = ALL_CHARACTER_DATA.get(dataIndex).getStandingAnimationStartFrameIndex();
                this.stateFrameEndIndex = ALL_CHARACTER_DATA.get(dataIndex).getStandingAnimationEndFrameIndex();
                break;
            case ATTACKING:
                this.stateFrameStartIndex = ALL_CHARACTER_DATA.get(dataIndex).getAttackingAnimationStartFrameIndex();
                this.stateFrameEndIndex = ALL_CHARACTER_DATA.get(dataIndex).getAttackingAnimationEndFrameIndex();
                break;
        }
    }

    /**
     * Only call this from MyGame.java and specifically in the section between batch.begin() and batch.end().
     * You can change the animation by changing the Character's state with .setState(CharacterState state)
     *
     * @param batch       Always input the one SpriteBatch called "batch" in the MyGame.java file.
     * @param elapsedTime Always input the one Float called "elapsedTime" in the MyGame.java file's render() section.
     */
    public void animate(SpriteBatch batch, float elapsedTime) {
        if (ALL_CHARACTER_DATA.get(dataIndex).getInternalName().equals("void")) {
            return;
        }
        this.frameTime += elapsedTime;
        if (this.frameTime > ALL_CHARACTER_DATA.get(dataIndex).getAllAnimationFrameDelays().get(this.frame)) {
            this.frameTime -= ALL_CHARACTER_DATA.get(dataIndex).getAllAnimationFrameDelays().get(this.frame);
            if (this.frame == this.stateFrameEndIndex) {
                if (this.state == CharacterState.DYING) {
                    this.state = CharacterState.DEAD; // ONLY settable here
                    return;
                }
                this.frame = this.stateFrameStartIndex;
            } else {
                this.frame++;
            }
        }

        // TODO: Either this needs to be redone to be similar to the Attack version of things, or this needs to be optimized to have the difference pre-calculated.
        float realX = this.getX();
        if (this.isFacingLeft == -1) { // if facing right
            realX += ALL_CHARACTER_DATA.get(dataIndex).getDimensionX() * SCALE_FACTOR;

            PolygonShape test = (PolygonShape) this.getBody().getFixtureList().get(0).getShape();
            Vector2 vertexToReadFrom = new Vector2();
            test.getVertex(0, vertexToReadFrom);
            float rightest = vertexToReadFrom.x;
            float leftest = vertexToReadFrom.x;
            for (int i = 1; i < test.getVertexCount(); i++) {
                test.getVertex(i, vertexToReadFrom);
                if (vertexToReadFrom.x > rightest) {
                    rightest = vertexToReadFrom.x;
                }
                if (vertexToReadFrom.x < leftest) {
                    leftest = vertexToReadFrom.x;
                }
            }
            float originFromRight = rightest - ALL_CHARACTER_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR + SCALE_FACTOR;
            float originFromLeft = ALL_CHARACTER_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR - leftest + SCALE_FACTOR;

            float difference = originFromRight - originFromLeft;

            realX += difference;
        }
        batch.draw(ALL_CHARACTER_DATA.get(dataIndex).getAllAnimationFrames()[this.frame],
                realX,
                this.getY(),
                0,
                0,
                ALL_CHARACTER_DATA.get(dataIndex).getDimensionX(), ALL_CHARACTER_DATA.get(dataIndex).getDimensionY(),
                isFacingLeft * SCALE_FACTOR, 1 * SCALE_FACTOR, 0);
    }

    public void faceRight() {
        this.isFacingLeft = -1;
    }

    public void faceLeft() {
        this.isFacingLeft = 1;
    }

    public int getIsFacingLeft() {
        return this.isFacingLeft;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    /**
     * Call this when a character should take damage. It also handles when they should die.
     * Enemies have at least 1000 Hp. Essentially, it's just (normal Hp number)*100.
     * Taking damage should always happen before animating, so that enemies with the DYING state can become DEAD.
     *
     * @param amountOfDamage the amount of damage the character will take.
     */
    public void takeDamage(int amountOfDamage) {
        this.currentHp -= amountOfDamage;
        if (this.currentHp <= 0) {
            this.setState(CharacterState.DYING);
        }
    }

    public float getTrueX() {
        return this.getX() + ALL_CHARACTER_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR;
    }

    public float getTrueY() {
        return this.getY() + (ALL_CHARACTER_DATA.get(dataIndex).getDimensionY() - ALL_CHARACTER_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR;
    }

    public float getAttackingY() {
        float bodyHeight;

        PolygonShape test = (PolygonShape) this.getBody().getFixtureList().get(0).getShape();
        Vector2 vertexToReadFrom = new Vector2();
        test.getVertex(0, vertexToReadFrom);
        float bottomest = vertexToReadFrom.y;
        float toppest = vertexToReadFrom.y;
        for (int i = 1; i < test.getVertexCount(); i++) {
            test.getVertex(i, vertexToReadFrom);
            if (vertexToReadFrom.y > toppest) {
                toppest = vertexToReadFrom.y;
            }
            if (vertexToReadFrom.y < bottomest) {
                bottomest = vertexToReadFrom.y;
            }
        }
        bodyHeight = toppest - bottomest;

        return this.getTrueY() + bodyHeight / 2;
    }
}
