package com.survivors.mygame;

import com.badlogic.gdx.physics.box2d.*;

import static com.survivors.mygame.MyGame.purpleExplosionHitTracker;

public class CustomContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA == null || fixtureB == null) return;
        if (fixtureA.getBody().getUserData() == null || fixtureB.getBody().getUserData() == null) return;

        if (this.checkFor(fixtureA, fixtureB, "enemy", "attack")) {
            Pair<EntityData> pair = getEntityOfType(fixtureA, fixtureB, "attack");
            Enemy enemy = (Enemy) pair.b().entity();
            if (enemy.getState() == Character.CharacterState.DYING) {
                return;
            }
            Attack attack = (Attack) pair.a().entity();
            if (attack.getDataIndex() == Attack.AttackTypeName.PURPLE_EXPLOSION_HIT.ordinal()) {
                if (purpleExplosionHitTracker.contains(enemy.getEnemyId())) {
                    return;
                } else {
                    purpleExplosionHitTracker.add(enemy.getEnemyId());
                }
            } else {
                if (attack.hasAlreadyHitThisEnemy(enemy.getEnemyId())) {
                    return;
                }
                attack.recordHitEnemy(enemy.getEnemyId());
            }
            attack.scheduleAdditionalAttack(enemy.getEnemyId(), enemy.getTrueX(), enemy.getAttackingY());
            enemy.takeDamage(attack.dealDamage());
        }

        // System.out.println(fixtureA.getBody().getUserData() + " <-- touched --> " + fixtureB.getBody().getUserData());

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    /**
     * Checks for contact between the two types of entities by reading information from the fixtures.
     *
     * @param fixtureA    comes from contact.getFixtureA()
     * @param fixtureB    comes from contact.getFixtureB()
     * @param entityType1 The first type that you are looking for. A type like "enemy", "player", "attack"
     * @param entityType2 The first type that you are looking for. A type like "enemy", "player", "attack"
     * @return returns a boolean about if contact occurred between those two types of Entities.
     */
    private boolean checkFor(Fixture fixtureA, Fixture fixtureB, String entityType1, String entityType2) {
        EntityData entityData1 = (EntityData) fixtureA.getBody().getUserData();
        EntityData entityData2 = (EntityData) fixtureB.getBody().getUserData();
        String realEntityType1 = entityData1.entityType();
        String realEntityType2 = entityData2.entityType();
        return (realEntityType1.equals(entityType1) && realEntityType2.equals(entityType2))
                || (realEntityType1.equals(entityType2) && realEntityType2.equals(entityType1));
    }

    // Data structure that is only useful for the getEntityOfType() function
    public record Pair<EntityData>(EntityData a, EntityData b) {
    }

    /**
     * @param fixtureA             comes from contact.getFixtureA()
     * @param fixtureB             comes from contact.getFixtureB()
     * @param lookingForEntityType The type of entity that you are looking for. A type like "enemy", "player", "attack"
     * @return Returns a pair in order where the first entity is the one with the type you're looking for and the other is not.
     */
    private Pair<EntityData> getEntityOfType(Fixture fixtureA, Fixture fixtureB, String lookingForEntityType) {
        EntityData entityData1 = (EntityData) fixtureA.getBody().getUserData();
        EntityData entityData2 = (EntityData) fixtureB.getBody().getUserData();
        if (entityData1.entityType().equals(lookingForEntityType)) {
            return new Pair<>(entityData1, entityData2);
        } else {
            return new Pair<>(entityData2, entityData1);
        }
    }
}
