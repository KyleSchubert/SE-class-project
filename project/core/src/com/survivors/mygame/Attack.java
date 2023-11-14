package com.survivors.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.survivors.mygame.MyGame.ALL_ATTACK_DATA;
import static com.survivors.mygame.MyGame.SCALE_FACTOR;

/**
 * Attack:
 * This class is used for all attacks the EquippableItem objects or Enemy objects can use.
 * This also includes the visual effects that do not check for collision or deal damage on their own.
 */
public class Attack extends Mobile {
    public enum AttackTypeName {
        FIREBALL_EFFECT, FIREBALL_HIT, FIREBALL_SKILL
    }

    public enum AttackDirections {
        FACING, LEFT_RIGHT, UP_DOWN, TOWARD_MOUSE, NONE, CLOSEST_ENEMY
    }

    private int dataIndex;
    private int damage;
    private int pierceTotal = 0;
    private int pierceLimit;
    private float frameTime = 0;
    private float totalTime = 0;
    private int frame;
    private int isFacingLeft;
    private float rotation;
    private boolean toBeDestroyed = false;
    private boolean additionalAttackOnHitMustHappen = false;
    private float hitEnemyWhoIsAtX;
    private float hitEnemyWhoIsAtY;

    public Attack(AttackTypeName attackTypeName, float x, float y, float angle, int isFacingLeft, World world, PhysicsShapeCache physicsShapeCache) {
        this.dataIndex = attackTypeName.ordinal();
        this.damage = ALL_ATTACK_DATA.get(dataIndex).getDamage();
        this.pierceLimit = ALL_ATTACK_DATA.get(dataIndex).getPierceCount();
        if (ALL_ATTACK_DATA.get(dataIndex).hasCollisionBody()) {
            this.makeBody(ALL_ATTACK_DATA.get(dataIndex).getInternalCollisionBodyName(), x, y,
                    ALL_ATTACK_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR,
                    (ALL_ATTACK_DATA.get(dataIndex).getDimensionY() - ALL_ATTACK_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR,
                    angle, isFacingLeft, // WAS angle + (180 * isFacingLeft)
                    world, physicsShapeCache);
        } else {
            this.makeBody("void", x, y,
                    ALL_ATTACK_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR,
                    (ALL_ATTACK_DATA.get(dataIndex).getDimensionY() - ALL_ATTACK_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR,
                    0, isFacingLeft, world, physicsShapeCache);
        }
        this.frame = ALL_ATTACK_DATA.get(dataIndex).getAnimationStartFrameIndex();
        this.isFacingLeft = isFacingLeft;
        this.rotation = angle;
        this.setId("attack", this);
    }

    public void animate(SpriteBatch batch, float elapsedTime) {
        this.frameTime += elapsedTime;
        this.totalTime += elapsedTime;
        if (this.totalTime > ALL_ATTACK_DATA.get(dataIndex).getLifetime()) {
            this.toBeDestroyed = true;
            return;
        }
        if (this.frameTime > ALL_ATTACK_DATA.get(dataIndex).getAnimationFrameDelays().get(this.frame)) {
            this.frameTime -= ALL_ATTACK_DATA.get(dataIndex).getAnimationFrameDelays().get(this.frame);
            if (this.frame == ALL_ATTACK_DATA.get(dataIndex).getAnimationEndFrameIndex()) {
                this.frame = ALL_ATTACK_DATA.get(dataIndex).getAnimationStartFrameIndex();
            } else {
                this.frame++;
            }
        }

        float xOffset = ALL_ATTACK_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR;
        float realRotation = this.rotation;
        if (this.isFacingLeft == 1 && ALL_ATTACK_DATA.get(dataIndex).hasCollisionBody()) { // TODO: Why is this if statement actually needed? (I wrote this comment when I wrote this if statement)
            realRotation = this.rotation + 180;
        }
        float yOffset = (ALL_ATTACK_DATA.get(dataIndex).getDimensionY() - ALL_ATTACK_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR;
        batch.draw(ALL_ATTACK_DATA.get(dataIndex).getAnimationFrames()[this.frame],
                this.getBody().getPosition().x - xOffset,
                this.getBody().getPosition().y - yOffset,
                ALL_ATTACK_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR,
                (ALL_ATTACK_DATA.get(dataIndex).getDimensionY() - ALL_ATTACK_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR,
                ALL_ATTACK_DATA.get(dataIndex).getDimensionX(), ALL_ATTACK_DATA.get(dataIndex).getDimensionY(),
                isFacingLeft * SCALE_FACTOR, SCALE_FACTOR,
                realRotation);
    }

    public void faceRight() {
        this.isFacingLeft = -1;
    }

    public void faceLeft() {
        this.isFacingLeft = 1;
    }

    /**
     * Returns the damage amount and deals with pierces.
     *
     * @return returns this.damage, which is the damage value stored in the Attack when it was created.
     */
    public int dealDamage() {
        this.pierceTotal++;
        if (this.pierceTotal >= this.pierceLimit) {
            this.toBeDestroyed = true;
        }
        // Schedule the additional attack on hit to happen
        if (ALL_ATTACK_DATA.get(dataIndex).isHasAdditionalAttackOnHit()) {
            this.additionalAttackOnHitMustHappen = true;
        }

        return this.damage;
    }

    public boolean isToBeDestroyed() {
        return toBeDestroyed;
    }

    public boolean getAdditionalAttackOnHitMustHappen() {
        return additionalAttackOnHitMustHappen;
    }

    public AttackTypeName getAdditionalAttackOnHit() {
        return ALL_ATTACK_DATA.get(dataIndex).getAdditionalAttackOnHit();
    }

    public float getHitEnemyWhoIsAtX() {
        return hitEnemyWhoIsAtX;
    }

    public void setHitEnemyWhoIsAtX(float hitEnemyWhoIsAtX) {
        this.hitEnemyWhoIsAtX = hitEnemyWhoIsAtX;
    }

    public float getHitEnemyWhoIsAtY() {
        return hitEnemyWhoIsAtY;
    }

    public void setHitEnemyWhoIsAtY(float hitEnemyWhoIsAtY) {
        this.hitEnemyWhoIsAtY = hitEnemyWhoIsAtY;
    }
}
