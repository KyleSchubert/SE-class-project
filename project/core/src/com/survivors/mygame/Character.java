package com.survivors.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    /**
     * @param characterTypeName The type of the character that will be created. This is an enum: CharacterTypeName
     * @param x                 The x coordinate of the spawning position of the Character.
     * @param y                 The y coordinate of the spawning position of the Character.
     */
    public Character(CharacterTypeName characterTypeName, float x, float y, World world, PhysicsShapeCache physicsShapeCache) {
        this.dataIndex = characterTypeName.ordinal();
        if (!ALL_CHARACTER_DATA.get(dataIndex).getInternalName().equals("void")) {
            this.makeBody(ALL_CHARACTER_DATA.get(dataIndex).getInternalName(), x, y, 0, world, physicsShapeCache);
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
        if (this.frame == this.stateFrameEndIndex) {
            if (this.frameTime > ALL_CHARACTER_DATA.get(dataIndex).getAllAnimationFrameDelays().get(this.stateFrameStartIndex)) {
                if (this.state == CharacterState.DYING) {
                    this.state = CharacterState.DEAD; // ONLY settable here
                    return;
                }
                this.frame = this.stateFrameStartIndex;
                this.frameTime -= ALL_CHARACTER_DATA.get(dataIndex).getAllAnimationFrameDelays().get(this.stateFrameStartIndex);
            }
        } else {
            if (this.frameTime > ALL_CHARACTER_DATA.get(dataIndex).getAllAnimationFrameDelays().get(this.frame + 1)) {
                this.frameTime -= ALL_CHARACTER_DATA.get(dataIndex).getAllAnimationFrameDelays().get(++this.frame);
            }
        }

        batch.draw(ALL_CHARACTER_DATA.get(dataIndex).getAllAnimationFrames()[this.frame],
                this.getX() - ALL_CHARACTER_DATA.get(dataIndex).getOriginX() * isFacingLeft * SCALE_FACTOR,
                this.getY() - (ALL_CHARACTER_DATA.get(dataIndex).getDimensionY() - ALL_CHARACTER_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR,
                0, 0,
                ALL_CHARACTER_DATA.get(dataIndex).getDimensionX(), ALL_CHARACTER_DATA.get(dataIndex).getDimensionY(),
                isFacingLeft * SCALE_FACTOR, 1 * SCALE_FACTOR, 0);
    }

    public void faceRight() {
        this.isFacingLeft = -1;
    }

    public void faceLeft() {
        this.isFacingLeft = 1;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }
}
