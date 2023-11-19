package com.survivors.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * AttackData:
 * This exclusively contains the information that will not change about the specific Attack.
 */
public class AttackData {
    private int dimensionX;
    private int dimensionY;
    private int originX; // from the left, and libGDX does it form the left
    private int originY; // from the top, BUT libGDX does it from the bottom
    private ArrayList<Float> animationFrameDelays = new ArrayList<>();
    private TextureRegion[] animationFrames;
    private boolean isLooping = false; // Whether the animation should loop or just play once and stay on the final frame until removed
    private boolean hasCollisionBody = false; // Attack reads this and can use internalCollisionBodyName to make the body if true
    private String internalCollisionBodyName = ""; // For the PhysicsEditor body
    private int animationStartFrameIndex = 0;
    private int animationEndFrameIndex; // (default is set in makeAnimationFrames() and should be animationFrames.size()-1)
    private float lifetime; // (default should be the sum of animationFrameDelays)
    private int pierceCount = 999999999; // Attacks keeps traveling until lifetime exceeded OR pierce count exceeded
    private boolean isProjectile = false; // If it is a projectile, it will move.
    private int projectileCount;
    private int projectileSpeed;
    private int damage;
    private boolean rotatableProjectile = false;
    private boolean flipNotRotate = false; // Ex: FIREBALL_EFFECT uses this
    private boolean hasAdditionalAttackOnHit = false;
    private Attack.AttackTypeName additionalAttackOnHit;
    private Attack.AimingDirections aimingPattern;
    // TODO: figure out sounds
    // private someclass soundEffect;

    public AttackData(Attack.AttackTypeName attackTypeName) {
        switch (attackTypeName) {
            case FIREBALL_EFFECT:
                this.dimensionX = 389;
                this.dimensionY = 295;
                this.originX = 210;
                this.originY = 176;
                this.animationFrameDelays = new ArrayList<>(Arrays.asList(0.030f, 0.090f, 0.090f, 0.090f, 0.090f));
                makeAnimationFrames("fireball/char effect.png");
                this.lifetime = 0.390f;
                this.aimingPattern = Attack.AimingDirections.FACING;
                this.flipNotRotate = true;
                break;
            case FIREBALL_HIT:
                this.dimensionX = 160;
                this.dimensionY = 130;
                this.originX = this.dimensionX / 2;
                this.originY = this.dimensionY / 2;
                this.animationFrameDelays = new ArrayList<>(Arrays.asList(0.090f, 0.090f, 0.090f, 0.090f, 0.090f));
                makeAnimationFrames("fireball/hit.png");
                this.lifetime = 0.450f;
                this.aimingPattern = Attack.AimingDirections.NONE;
                break;
            case FIREBALL_SKILL:
                this.dimensionX = 104;
                this.dimensionY = 63;
                this.originX = 40;
                this.originY = 32;
                this.animationFrameDelays = new ArrayList<>(Arrays.asList(0.120f, 0.120f, 0.120f));
                makeAnimationFrames("fireball/skill.png");
                this.isLooping = true;
                this.hasCollisionBody = true;
                this.internalCollisionBodyName = "fireball";
                this.lifetime = 3f;
                this.pierceCount = 1;
                this.isProjectile = true;
                this.projectileCount = 3;
                this.projectileSpeed = 32;
                this.damage = 40;
                this.rotatableProjectile = true;
                this.hasAdditionalAttackOnHit = true;
                this.additionalAttackOnHit = Attack.AttackTypeName.FIREBALL_HIT;
                this.aimingPattern = Attack.AimingDirections.LEFT_RIGHT;
                break;
            case FIREBALL_SKILL_NO_REPEAT_HIT_TESTING:
                this.dimensionX = 104;
                this.dimensionY = 63;
                this.originX = 40;
                this.originY = 32;
                this.animationFrameDelays = new ArrayList<>(Arrays.asList(0.120f, 0.120f, 0.120f));
                makeAnimationFrames("fireball/skill.png");
                this.isLooping = true;
                this.hasCollisionBody = true;
                this.internalCollisionBodyName = "fireball";
                this.lifetime = 0.4f;
                this.pierceCount = 0;
                this.isProjectile = true;
                this.projectileCount = 1;
                this.projectileSpeed = 16;
                this.damage = 40;
                this.rotatableProjectile = true;
                this.hasAdditionalAttackOnHit = true;
                this.additionalAttackOnHit = Attack.AttackTypeName.FIREBALL_HIT;
                this.aimingPattern = Attack.AimingDirections.LEFT_RIGHT;
                break;
            case DRAGON_SLASH_HIT:
                this.dimensionX = 302;
                this.dimensionY = 246;
                this.originX = this.dimensionX / 2;
                this.originY = this.dimensionY / 2;
                this.animationFrameDelays = new ArrayList<>(Arrays.asList(0.090f, 0.090f, 0.090f, 0.090f, 0.090f, 0.090f));
                makeAnimationFrames("dragon slash/hit.png");
                this.lifetime = 0.540f;
                this.aimingPattern = Attack.AimingDirections.NONE;
                break;
            case DRAGON_SLASH_SKILL:
                this.dimensionX = 330;
                this.dimensionY = 216;
                this.originX = 289;
                this.originY = 147;
                this.animationFrameDelays = new ArrayList<>(Arrays.asList(0.120f, 0.120f, 0.120f, 0.120f));
                makeAnimationFrames("dragon slash/skill.png");
                this.hasCollisionBody = true;
                this.internalCollisionBodyName = "dragon slash";
                this.lifetime = 0.480f;
                this.damage = 150;
                this.hasAdditionalAttackOnHit = true;
                this.additionalAttackOnHit = Attack.AttackTypeName.DRAGON_SLASH_HIT;
                this.aimingPattern = Attack.AimingDirections.FACING;
                this.flipNotRotate = true;
                break;
            default:
                System.out.println("Why was an ATTACK almost generated with no matching type name? attackTypeName:  " + attackTypeName);
        }
    }

    /**
     * Sets  this.animationFrames  and also sets  this.animationEndFrameIndex  to its default.
     *
     * @param folderAndFileName Ex: "fireball/char effect.png"
     */
    private void makeAnimationFrames(String folderAndFileName) {
        int frameCount = this.animationFrameDelays.size();
        this.animationFrames = new TextureRegion[frameCount];
        TextureRegion[][] tempFrames = TextureRegion.split(new Texture("equipment ATTACKS/" + folderAndFileName), this.dimensionX, this.dimensionY);
        System.arraycopy(tempFrames[0], 0, this.animationFrames, 0, frameCount);
        this.animationEndFrameIndex = frameCount - 1;
    }

    public int getDimensionX() {
        return dimensionX;
    }

    public int getDimensionY() {
        return dimensionY;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    public ArrayList<Float> getAnimationFrameDelays() {
        return animationFrameDelays;
    }

    public TextureRegion[] getAnimationFrames() {
        return animationFrames;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public boolean hasCollisionBody() {
        return hasCollisionBody;
    }

    public String getInternalCollisionBodyName() {
        return internalCollisionBodyName;
    }

    public int getAnimationStartFrameIndex() {
        return animationStartFrameIndex;
    }

    public int getAnimationEndFrameIndex() {
        return animationEndFrameIndex;
    }

    public float getLifetime() {
        return lifetime;
    }

    public int getPierceCount() {
        return pierceCount;
    }

    public boolean isProjectile() {
        return isProjectile;
    }

    public int getProjectileCount() {
        return projectileCount;
    }

    public int getProjectileSpeed() {
        return projectileSpeed;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isHasAdditionalAttackOnHit() {
        return hasAdditionalAttackOnHit;
    }

    public Attack.AttackTypeName getAdditionalAttackOnHit() {
        return additionalAttackOnHit;
    }

    public Attack.AimingDirections getAimingPattern() {
        return aimingPattern;
    }

    public boolean isRotatableProjectile() {
        return rotatableProjectile;
    }

    public boolean isFlipNotRotate() {
        return flipNotRotate;
    }

    public boolean isAttackingHorizontally() {
        return this.aimingPattern == Attack.AimingDirections.FACING || this.aimingPattern == Attack.AimingDirections.BEHIND || this.aimingPattern == Attack.AimingDirections.LEFT_RIGHT;
    }
}
