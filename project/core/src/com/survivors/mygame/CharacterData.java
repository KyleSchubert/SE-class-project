package com.survivors.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * CharacterData:
 * This exclusively contains the information that will not change about the specific character.
 */
public class CharacterData {
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
    private boolean ableToAttack = false;
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
    public CharacterData(Character.CharacterTypeName characterTypeName) {
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
                this.ableToAttack = true;
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
                System.out.println("Why was a character almost generated with no matching type name? characterTypeName:  " + characterTypeName);
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
            if (this.ableToAttack) {
                this.attackingAnimationSpritesheet = new Texture(internalName + "/attack.png");
            }
        }
    }

    private void prepareTotalFrameCount() {
        this.totalFrameCount = this.dyingAnimationFrameDelays.size()
                + this.movingAnimationFrameDelays.size()
                + this.standingAnimationFrameDelays.size();
        if (this.ableToAttack) {
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
        if (this.ableToAttack) {
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

    public TextureRegion[] getAllAnimationFrames() {
        return allAnimationFrames;
    }

    public ArrayList<Float> getAllAnimationFrameDelays() {
        return allAnimationFrameDelays;
    }

    public boolean isAbleToAttack() {
        return ableToAttack;
    }

    public int getDimensionX() {
        return dimensionX;
    }

    public int getDimensionY() {
        return dimensionY;
    }

    public int getDyingAnimationStartFrameIndex() {
        return dyingAnimationStartFrameIndex;
    }

    public int getDyingAnimationEndFrameIndex() {
        return dyingAnimationEndFrameIndex;
    }

    public int getMovingAnimationStartFrameIndex() {
        return movingAnimationStartFrameIndex;
    }

    public int getMovingAnimationEndFrameIndex() {
        return movingAnimationEndFrameIndex;
    }

    public int getStandingAnimationStartFrameIndex() {
        return standingAnimationStartFrameIndex;
    }

    public int getStandingAnimationEndFrameIndex() {
        return standingAnimationEndFrameIndex;
    }

    public int getAttackingAnimationStartFrameIndex() {
        return attackingAnimationStartFrameIndex;
    }

    public int getAttackingAnimationEndFrameIndex() {
        return attackingAnimationEndFrameIndex;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }
    public String getInternalName() {
        return internalName;
    }
}
