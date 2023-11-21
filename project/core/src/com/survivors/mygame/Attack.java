package com.survivors.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;
import java.util.HashSet;

import static com.survivors.mygame.MyGame.ALL_ATTACK_DATA;
import static com.survivors.mygame.MyGame.SCALE_FACTOR;

/**
 * Attack:
 * This class is used for all attacks the EquippableItem objects or Enemy objects can use.
 * This also includes the visual effects that do not check for collision or deal damage on their own.
 */
public class Attack extends Mobile {
    public enum AttackTypeName {
        FIREBALL_EFFECT, FIREBALL_HIT, FIREBALL_SKILL_NO_REPEAT_HIT_TESTING, FIREBALL_SKILL,
        DRAGON_SLASH_HIT, DRAGON_SLASH_SKILL,
        SHADOWY_SMACK_HIT, SHADOWY_SMACK_SKILL,
        PURPLE_EXPLOSION_HIT,
        LIGHT_SWORD_EFFECT, LIGHT_SWORD_HIT, LIGHT_SWORD_SKILL,
        LIGHT_BALL_HIT, LIGHT_BALL_SKILL,
        MOON_SPIN_HIT, MOON_SPIN_SKILL,
        FANCY_SWORD_HIT, FANCY_SWORD_SKILL
    }

    public enum AimingDirections {
        FACING, BEHIND, LEFT_RIGHT, UP_DOWN, TOWARD_MOUSE, NONE, CLOSEST_ENEMY
    }

    private final int dataIndex;
    private int damage;
    private int pierceTotal = 0;
    private int pierceLimit;
    private float frameTime = 0;
    private float totalTime = 0;
    private int frame;
    private final int isFacingLeft;
    private final float rotation;
    private boolean toBeDestroyed = false;
    private boolean additionalAttackOnHitMustHappen = false;
    private final HashSet<Integer> alreadyHitTheseEnemies = new HashSet<>();
    // justHitEnemyData is for when a skill hits multiple enemies on the same frame --> the game can then make the additionalAttackOnHit for each of them. Then it is cleared.
    private final ArrayList<JustHitEnemyData> justHitEnemyData = new ArrayList<>();

    public Attack(AttackTypeName attackTypeName, float x, float y, float angle, int isFacingLeft, World world, PhysicsShapeCache physicsShapeCache) {
        this.dataIndex = attackTypeName.ordinal();
        this.damage = ALL_ATTACK_DATA.get(dataIndex).getDamage();
        this.pierceLimit = ALL_ATTACK_DATA.get(dataIndex).getPierceCount();
        if (ALL_ATTACK_DATA.get(dataIndex).hasCollisionBody()) {
            float newAngle = angle;
            if (ALL_ATTACK_DATA.get(dataIndex).isFlipNotRotate()) {
                newAngle = 0;
                if (isFacingLeft == 1) {
                    newAngle = 180;
                }
            }
            this.makeBody(ALL_ATTACK_DATA.get(dataIndex).getInternalCollisionBodyName(), x, y,
                    ALL_ATTACK_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR,
                    (ALL_ATTACK_DATA.get(dataIndex).getDimensionY() - ALL_ATTACK_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR,
                    newAngle, isFacingLeft, // WAS angle + (180 * isFacingLeft)
                    world, physicsShapeCache);
        } else {
            if (ALL_ATTACK_DATA.get(dataIndex).isFlipNotRotate() && !ALL_ATTACK_DATA.get(dataIndex).isAttackingHorizontally()) {
                // only the name changes because I'm using this to bring data into Mobile.java by allowing me to check if the name is not "void"
                // but this still has no body part, just like "void".
                this.makeBody("void2", x, y,
                        ALL_ATTACK_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR,
                        (ALL_ATTACK_DATA.get(dataIndex).getDimensionY() - ALL_ATTACK_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR,
                        angle, isFacingLeft, world, physicsShapeCache);
            } else {
                this.makeBody("void", x, y,
                        ALL_ATTACK_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR,
                        (ALL_ATTACK_DATA.get(dataIndex).getDimensionY() - ALL_ATTACK_DATA.get(dataIndex).getOriginY()) * SCALE_FACTOR,
                        0, isFacingLeft, world, physicsShapeCache);
            }
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
                if (!ALL_ATTACK_DATA.get(dataIndex).isLooping()) {
                    this.toBeDestroyed = true;
                    return;
                }
                this.frame = ALL_ATTACK_DATA.get(dataIndex).getAnimationStartFrameIndex();
            } else {
                this.frame++;
            }
        }

        float xOffset = ALL_ATTACK_DATA.get(dataIndex).getOriginX() * SCALE_FACTOR;
        float realRotation = this.rotation;
        if (this.isFacingLeft == 1 && !ALL_ATTACK_DATA.get(dataIndex).isFlipNotRotate()) { // TODO: Why is this if statement actually needed? (I wrote this comment when I wrote this if statement)
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

    /**
     * Returns the damage amount and deals with pierces.
     *
     * @return returns this.damage, which is the damage value stored in the Attack when it was created.
     */
    public int dealDamage() {
        this.pierceTotal++;
        if (this.pierceTotal > this.pierceLimit) {
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

    public void setAdditionalAttackOnHitMustHappen(boolean additionalAttackOnHitMustHappen) {
        this.additionalAttackOnHitMustHappen = additionalAttackOnHitMustHappen;
    }

    public AttackTypeName getAdditionalAttackOnHit() {
        return ALL_ATTACK_DATA.get(dataIndex).getAdditionalAttackOnHit();
    }

    public boolean hasAlreadyHitThisEnemy(Integer enemyIdToTest) {
        return this.alreadyHitTheseEnemies.contains(enemyIdToTest);
    }

    public void recordHitEnemy(Integer newEnemyId) {
        this.alreadyHitTheseEnemies.add(newEnemyId);
    }

    public void copyIntoAlreadyHitTheseEnemies(HashSet<Integer> theHashSet) {
        this.alreadyHitTheseEnemies.addAll(theHashSet);
    }

    public HashSet<Integer> getAlreadyHitTheseEnemies() {
        return alreadyHitTheseEnemies;
    }

    public void scheduleAdditionalAttack(Integer newEnemyId, float hitEnemyWhoIsAtX, float hitEnemyWhoIsAtY) {
        this.justHitEnemyData.add(new JustHitEnemyData(newEnemyId, hitEnemyWhoIsAtX, hitEnemyWhoIsAtY));
    }

    public ArrayList<JustHitEnemyData> getJustHitEnemyData() {
        return this.justHitEnemyData;
    }

    public void clearJustHitEnemyData() {
        this.justHitEnemyData.clear();
    }
}
