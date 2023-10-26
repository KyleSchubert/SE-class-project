package com.survivors.mygame;

public class Enemy extends Mobile {
    // Relies on player position
    // Will be tangible
    private EnemyData enemyData;

    /**
     * @param enemyData The type of the enemy that will be created.
     * @param x         The x coordinate of the spawning position of the Enemy.
     * @param y         The y coordinate of the spawning position of the Enemy.
     */
    public Enemy(EnemyData enemyData, double x, double y) {
        this.enemyData = enemyData;

    }
}
