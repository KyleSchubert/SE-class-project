package com.survivors.mygame;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.survivors.mygame.MyGame.SCALE_FACTOR;
import static com.survivors.mygame.MyGame.getNextEntityId;

public class Mobile {
    private Body body;

    public final void makeBody(String name, float x, float y, float angle, World world, PhysicsShapeCache physicsShapeCache) {
        if (this.body != null) {
            world.destroyBody(this.body);
        }
        Body body = physicsShapeCache.createBody(name, world, SCALE_FACTOR, SCALE_FACTOR);
        body.setTransform(x, y, angle * MathUtils.degreesToRadians);
        this.body = body;
    }

    public final void makeBody(String name, float x, float y, float originX, float originY, float angle, int isFacingLeft, World world, PhysicsShapeCache physicsShapeCache) {
        if (this.body != null) {
            world.destroyBody(this.body);
        }
        Body body = physicsShapeCache.createBody(name, world, isFacingLeft * SCALE_FACTOR, SCALE_FACTOR);
        angle *= MathUtils.degreesToRadians;
        if (isFacingLeft == 1 && !name.equals("void")) { // TODO: Why do circles not work with this entire function? And why is this if statement actually needed? Is it needed? We'll know after more examples of Attacks.
            angle += 3.1415956f;
        }
        double realX = x - (Math.cos(angle) * isFacingLeft * originX - Math.sin(angle) * originY);
        double realY = y - (Math.cos(angle) * originY + Math.sin(angle) * isFacingLeft * originX);
        body.setTransform((float) realX, (float) realY, angle);
        this.body = body;
    }

    protected final float getX() {
        return this.body.getPosition().x;
    }

    protected final float getY() {
        return this.body.getPosition().y;
    }

    protected final void move(float x, float y, float speedScalar) {
        Vector2 vector2 = new Vector2(x, y).nor().scl(speedScalar);
        this.body.setLinearVelocity(vector2);
    }

    public Body getBody() {
        return body;
    }

    /**
     * @param entityType Example: "enemy", "player", "attack"
     * @param entity     Pass the "this" keyword
     */
    public final void setId(String entityType, Object entity) {
        EntityData entityData = new EntityData(entityType, entity);
        this.body.setUserData(entityData);
    }

    public final EntityData getId() {
        return (EntityData) this.body.getUserData();
    }
}
