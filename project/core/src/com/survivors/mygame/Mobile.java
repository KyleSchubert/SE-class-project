package com.survivors.mygame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.survivors.mygame.MyGame.SCALE_FACTOR;

public class Mobile {
    private Body body;

    public final void makeBody(String name, float x, float y, float angle, World world, PhysicsShapeCache physicsShapeCache) {
        if (this.body != null) {
            world.destroyBody(this.body);
        }
        // TODO: TEMPORARILY making all bodies just be the bird's body. Will change once we draw the shapes in PhysicsEditor
        Body body = physicsShapeCache.createBody(name, world, SCALE_FACTOR, SCALE_FACTOR);
        body.setTransform(x, y, angle);
        this.body = body;
    }

    protected final float getX() {
        return this.body.getPosition().x;
    }

    protected final float getY() {
        return this.body.getPosition().y;
    }

    protected final void move(int x, int y, float speedScalar) {
        Vector2 vector2 = new Vector2(x, y).nor().scl(speedScalar);
        this.body.setLinearVelocity(vector2);
    }
}
