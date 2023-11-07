package com.survivors.mygame;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;


/* A wave represents a set of enemy types and the number of enemies
 * for each respective type. This is implemented using two parallel arrays,
 * where EnemyTypes[i] gives the type of the ith enemy in the list, and
 * EnemyNums[i] represents how many of these enemies have yet to be added
 * to the game world.
 */

public class Wave {


    // The in-game time at which this wave should start
    private float timeToStart;
    // List of enemy types for this wave
    private Array<Character.CharacterTypeName> EnemyTypes;
    // List of remaining amounts of enemies from this wave
    private Array<Integer> EnemyNums;
    // Represents the index of the current enemy type being taken and added to the game
    private int curIndex;
    // Represents if this wave's enemies have been exhausted
    private boolean isEmpty;


    // Types[] and nums[] must be of the same length to work properly
    public Wave(float time, Array<Character.CharacterTypeName> Types, Array<Integer> Nums) {
        timeToStart = time;
        EnemyTypes = Types;
        EnemyNums = Nums;
        curIndex = 0;
        isEmpty = false;
    }

    /* Nick: Attempts to "take" an enemy from the wave collection, effectively telling
     * the program it can add one of said enemy to the game world. I initially had a
     * parameter allowing a specific enemy type to be taken, but I rewrote it to just
     * take the next enemy in the list. I figured this wouldn't look much different to
     * the player, and this greatly simplifies the process. It now returns the type of
     * the character returned, or VOID if there are no enemies left.
     */
    public Character.CharacterTypeName takeEnemy() {
        // if current index still has enemies left:
        if (EnemyNums.items[curIndex] > 0) {
            // take an enemy
            EnemyNums.items[curIndex]--;
            return EnemyTypes.items[curIndex];
        } else {
            // increment index
            curIndex++;
            // if new index is valid:
            if (curIndex < EnemyNums.size) {
                // take an enemy
                EnemyNums.items[curIndex]--;
                return EnemyTypes.items[curIndex];
            } else {
                // this wave is now exhausted
                isEmpty = true;
                return Character.CharacterTypeName.VOID;
            }
        }
    }


    public float getTimeToStart() {
        return timeToStart;
    }


    // returns if every enemy type from this wave has been exhausted
    public boolean isEmpty() {
        return isEmpty;
    }

}
