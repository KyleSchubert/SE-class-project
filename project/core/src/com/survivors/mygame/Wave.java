package com.survivors.mygame;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;


/* A wave represents a set of enemy types and the number of enemies
 * for each respective type. This is implemented using a map.
 */

public class Wave {

    private ObjectMap<Character.CharacterTypeName, Integer> SubWaves;


    // Types[] and nums[] must be of the same length, and Types[] should have no duplicate values
    public Wave(Array<Character.CharacterTypeName> Types, Array<Integer> Nums) {
        for (int i = 0; i < Types.size; i++) {
            SubWaves.put(Types.get(i), Nums.get(i));
        }
    }

    /* Attempts to "take" an enemy from the wave collection, effectively telling
     * the program it can add one of said enemy to the game world. Returns true if
     * an enemy was taken, and false if the enemy type isn't in this wave, or if
     * this enemy type has been exhausted from this wave.
     */
    public boolean takeEnemy(Character.CharacterTypeName T) {
        if (SubWaves.containsKey(T)) {
            if (SubWaves.get(T) > 0) {
                // not sure if this line will work!
                SubWaves.put(T, SubWaves.get(T) - 1);
                return true;
            } else return false;
        } else return false;
    }

    // returns if every key-value pair has 0 for the value,
    // aka the wave has had every enemy enter the game world
    public boolean isEmpty() {
        for (Integer I : SubWaves.values()) {
            if (I > 0)
                return false;
        }
        return true;
    }

}
