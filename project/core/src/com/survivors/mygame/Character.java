package com.survivors.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.survivors.mygame.MyGame.SCALE_FACTOR;

public class Character extends Mobile {
    public enum CharacterState {
        DYING, MOVING, STANDING, ATTACKING
    }

    public enum CharacterTypeName {
        // Nick: Added the VOID typename to denote a newly created enemy in a pool before use
        BIRD, PLANT, STUMP, PIG, ORANGE_MUSHROOM, BLUE_MUSHROOM, ZOMBIE_MUSHROOM, HELMET_PENGUIN, SPEAR_PENGUIN, SMALL_PENGUIN, VOID
    }

    /* Nick: I made characterData public and changable, as pool objects will
     *       have their attributes changed upon being used, instead of having
     *       a new constructor called each time
     */
    public CharacterData characterData; // This exclusively contains the information that will not change about the specific character.
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
        this.characterData = new CharacterData(characterTypeName);
        this.makeBody(x, y, 0, world, physicsShapeCache);
        this.setState(CharacterState.STANDING);
    }

    public CharacterState getState() {
        return state;
    }

    /**
     * @param state The state of the character that you want it to be changed to. Ex: Character.CharacterState.MOVING
     */
    public void setState(CharacterState state) {
        if (!state.equals(this.state)) {
            if (state == CharacterState.ATTACKING && !this.characterData.canAttack) {
                return;
            } else {
                this.state = state;
            }
            prepareFrameStartAndEndIndex();
            this.frame = this.stateFrameEndIndex;
        }
    }

    private void prepareFrameStartAndEndIndex() {
        switch (this.state) {
            case DYING:
                this.stateFrameStartIndex = this.characterData.dyingAnimationStartFrameIndex;
                this.stateFrameEndIndex = this.characterData.dyingAnimationEndFrameIndex;
                break;
            case MOVING:
                this.stateFrameStartIndex = this.characterData.movingAnimationStartFrameIndex;
                this.stateFrameEndIndex = this.characterData.movingAnimationEndFrameIndex;
                break;
            case STANDING:
                this.stateFrameStartIndex = this.characterData.standingAnimationStartFrameIndex;
                this.stateFrameEndIndex = this.characterData.standingAnimationEndFrameIndex;
                break;
            case ATTACKING:
                this.stateFrameStartIndex = this.characterData.attackingAnimationStartFrameIndex;
                this.stateFrameEndIndex = this.characterData.attackingAnimationEndFrameIndex;
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
        this.frameTime += elapsedTime;
        if (this.frame == this.stateFrameEndIndex) {
            if (this.frameTime > this.characterData.allAnimationFrameDelays.get(this.stateFrameStartIndex)) {
                this.frame = this.stateFrameStartIndex;
                this.frameTime -= this.characterData.allAnimationFrameDelays.get(this.stateFrameStartIndex);
            }
        } else {
            if (this.frameTime > this.characterData.allAnimationFrameDelays.get(this.frame + 1)) {
                this.frameTime -= this.characterData.allAnimationFrameDelays.get(++this.frame);
            }
        }

        batch.draw(this.characterData.allAnimationFrames[this.frame],
                this.getX() - this.characterData.originX * isFacingLeft * SCALE_FACTOR,
                this.getY() - (this.characterData.dimensionY - this.characterData.originY) * SCALE_FACTOR,
                0, 0,
                this.characterData.dimensionX, this.characterData.dimensionY,
                isFacingLeft * SCALE_FACTOR, 1 * SCALE_FACTOR, 0);
    }

    public void faceRight() {
        this.isFacingLeft = -1;
    }

    public void faceLeft() {
        this.isFacingLeft = 1;
    }


    /**
     * CharacterData:
     * This exclusively contains the information that will not change about the specific character.
     */
    static class CharacterData {
        private TextureRegion[] allAnimationFrames;
        private final ArrayList<Float> allAnimationFrameDelays = new ArrayList<>();
        private Texture dyingAnimationSpritesheet;
        private Texture movingAnimationSpritesheet;
        private Texture standingAnimationSpritesheet;
        private Texture attackingAnimationSpritesheet;
        private ArrayList<Float> dyingAnimationFrameDelays;
        private ArrayList<Float> movingAnimationFrameDelays;
        private ArrayList<Float> standingAnimationFrameDelays;
        private ArrayList<Float> attackingAnimationFrameDelays;
        private boolean canAttack = false;
        private int originX; // from the left, and libGDX does it form the left
        private int originY; // from the top, BUT libGDX does it from the bottom
        private int dimensionX;
        private int dimensionY;
        private String internalName = "";
        private int totalFrameCount;
        private int dyingAnimationStartFrameIndex;
        private int dyingAnimationEndFrameIndex;
        private int movingAnimationStartFrameIndex;
        private int movingAnimationEndFrameIndex;
        private int standingAnimationStartFrameIndex;
        private int standingAnimationEndFrameIndex;
        private int attackingAnimationStartFrameIndex;
        private int attackingAnimationEndFrameIndex;

        /**
         * I think this should only need the String of the character's type and then
         * take it and compare it against all the valid character types
         * which will then assign specific values to variables in this class like
         * how big the character is, or its corresponding hit box, or how much Max HP it has, etc.
         *
         * @param characterTypeName The word for the type of the character to be created.
         */
        public CharacterData(CharacterTypeName characterTypeName) {
            switch (characterTypeName) {
                case BIRD:
                    this.internalName = "bird";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(13, 0.120f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(6, 0.150f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(4, 0.210f));
                    this.originX = 94;
                    this.originY = 62;
                    this.dimensionX = 188;
                    this.dimensionY = 86;
                    break;
                case PLANT:
                    this.internalName = "plant";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(7, 0.120f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(6, 0.090f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(6, 0.120f));
                    this.originX = 126;
                    this.originY = 103;
                    this.dimensionX = 252;
                    this.dimensionY = 134;
                    break;
                case STUMP:
                    this.internalName = "stump";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.120f, 0.120f, 0.120f, 0.120f, 0.300f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(4, 0.180f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.100f, 0.200f, 0.100f, 0.100f));
                    this.originX = 63;
                    this.originY = 130;
                    this.dimensionX = 126;
                    this.dimensionY = 167;
                    break;
                case PIG:
                    this.internalName = "pig";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.180f, 0.180f, 0.300f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.100f, 0.100f, 0.300f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.180f, 0.150f, 0.180f));
                    this.originX = 48;
                    this.originY = 74;
                    this.dimensionX = 96;
                    this.dimensionY = 108;
                    break;
                case ORANGE_MUSHROOM:
                    this.internalName = "orange mushroom";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.180f, 0.180f, 0.300f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.180f, 0.120f, 0.180f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(2, 0.180f));
                    this.originX = 42;
                    this.originY = 88;
                    this.dimensionX = 81;
                    this.dimensionY = 121;
                    break;
                case BLUE_MUSHROOM:
                    this.internalName = "blue mushroom";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(5, 0.180f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(4, 0.180f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(2, 0.180f));
                    this.originX = 121;
                    this.originY = 126;
                    this.dimensionX = 242;
                    this.dimensionY = 194;
                    break;
                case ZOMBIE_MUSHROOM:
                    this.internalName = "zombie mushroom";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.180f, 0.180f, 0.300f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.180f, 0.200f, 0.120f, 0.180f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(2, 0.180f));
                    this.originX = 40;
                    this.originY = 106;
                    this.dimensionX = 80;
                    this.dimensionY = 157;
                    break;
                case HELMET_PENGUIN:
                    this.internalName = "helmet penguin";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(10, 0.150f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(8, 0.100f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(6, 0.180f));
                    this.originX = 244;
                    this.originY = 194;
                    this.dimensionX = 488;
                    this.dimensionY = 292;
                    break;
                case SPEAR_PENGUIN:
                    this.internalName = "spear penguin";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(7, 0.120f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(6, 0.100f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(8, 0.180f));
                    this.attackingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.300f, 0.210f, 0.180f, 0.90f, 0.90f, 0.120f, 0.300f, 0.300f, 0.300f));
                    this.canAttack = true;
                    this.originX = 247;
                    this.originY = 150;
                    this.dimensionX = 494;
                    this.dimensionY = 236;
                    break;
                case SMALL_PENGUIN:
                    this.internalName = "small penguin";
                    this.dyingAnimationFrameDelays = new ArrayList<>(Arrays.asList(0.150f, 0.150f, 0.150f, 0.150f, 0.300f));
                    this.movingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(6, 0.070f));
                    this.standingAnimationFrameDelays = new ArrayList<>(Collections.nCopies(12, 0.120f));
                    this.originX = 47;
                    this.originY = 47;
                    this.dimensionX = 94;
                    this.dimensionY = 62;
                    break;
                // Nick: placeholder internal name for a void enemy
                case VOID:
                    this.internalName = "void";
                default:
                    System.out.println("Why was an character almost generated with no matching type name? characterTypeName:  " + characterTypeName);
                    return;
            }
            prepareSpritesheets();
            prepareTotalFrameCount();
            prepareAllAnimationFrames();
        }

        private void prepareSpritesheets() {
            if (!internalName.isEmpty()) {
                this.dyingAnimationSpritesheet = new Texture(internalName + "/die.png");
                this.movingAnimationSpritesheet = new Texture(internalName + "/move.png");
                this.standingAnimationSpritesheet = new Texture(internalName + "/stand.png");
                if (this.canAttack) {
                    this.attackingAnimationSpritesheet = new Texture(internalName + "/attack.png");
                }
            }
        }

        private void prepareTotalFrameCount() {
            this.totalFrameCount = this.dyingAnimationFrameDelays.size()
                    + this.movingAnimationFrameDelays.size()
                    + this.standingAnimationFrameDelays.size();
            if (this.canAttack) {
                this.totalFrameCount += this.attackingAnimationFrameDelays.size();
            }
        }

        private void prepareAllAnimationFrames() {
            TextureRegion[] animationFrames = new TextureRegion[this.totalFrameCount];
            int latestIndex = 0;

            // DYING FRAMES
            this.dyingAnimationStartFrameIndex = latestIndex;
            TextureRegion[][] dyingFrames = TextureRegion.split(this.dyingAnimationSpritesheet, this.dimensionX, this.dimensionY);
            for (int i = 0; i < this.dyingAnimationFrameDelays.size(); i++) {
                animationFrames[latestIndex + i] = dyingFrames[0][i];
                allAnimationFrameDelays.add(dyingAnimationFrameDelays.get(i));
            }
            latestIndex += this.dyingAnimationFrameDelays.size() - 1;
            this.dyingAnimationEndFrameIndex = latestIndex;

            // MOVING FRAMES
            this.movingAnimationStartFrameIndex = ++latestIndex;
            TextureRegion[][] movingFrames = TextureRegion.split(this.movingAnimationSpritesheet, this.dimensionX, this.dimensionY);
            for (int i = 0; i < this.movingAnimationFrameDelays.size(); i++) {
                animationFrames[latestIndex + i] = movingFrames[0][i];
                allAnimationFrameDelays.add(movingAnimationFrameDelays.get(i));
            }
            latestIndex += this.movingAnimationFrameDelays.size() - 1;
            this.movingAnimationEndFrameIndex = latestIndex;


            // STANDING FRAMES
            this.standingAnimationStartFrameIndex = ++latestIndex;
            TextureRegion[][] standingFrames = TextureRegion.split(this.standingAnimationSpritesheet, this.dimensionX, this.dimensionY);
            for (int i = 0; i < this.standingAnimationFrameDelays.size(); i++) {
                animationFrames[latestIndex + i] = standingFrames[0][i];
                allAnimationFrameDelays.add(standingAnimationFrameDelays.get(i));
            }
            latestIndex += this.standingAnimationFrameDelays.size() - 1;
            this.standingAnimationEndFrameIndex = latestIndex;

            // ATTACKING FRAMES if available
            if (this.canAttack) {
                this.attackingAnimationStartFrameIndex = ++latestIndex;
                TextureRegion[][] attackingFrames = TextureRegion.split(this.attackingAnimationSpritesheet, this.dimensionX, this.dimensionY);
                for (int i = 0; i < this.attackingAnimationFrameDelays.size(); i++) {
                    animationFrames[latestIndex + i] = attackingFrames[0][i];
                    allAnimationFrameDelays.add(attackingAnimationFrameDelays.get(i));
                }
                latestIndex += this.attackingAnimationFrameDelays.size() - 1;
                this.attackingAnimationEndFrameIndex = latestIndex;
            }

            // DONE
            this.allAnimationFrames = animationFrames;
        }
    }
}
