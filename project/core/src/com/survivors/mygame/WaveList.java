package com.survivors.mygame;

import com.badlogic.gdx.utils.Array;

/* Nick: This class represents all the enemies in a level, by storing a list of
         the enemy waves that spawn in said level. */

public class WaveList {

    // The list of waves for a given level
    private Array<Wave> waveList;

    // The index of the current wave being added to the game
    int curWave;
    // Represents if all waves have been exhausted
    boolean isEmpty;

    // May add another constructor that builds a list of waves from a text file
    public WaveList(Array<Wave> WL) {
        waveList = WL;
        curWave = 0;
        isEmpty = false;
    }


    /* Calls the current wave's takeEnemy() method. If the current wave is empty,
       this function only returns the VOID character type, as we then wait for time
       to pass for the next wave to start. */
    public Character.CharacterTypeName takeEnemy() {
        if (this.curWave < waveList.size)
            // if current wave is not empty:
            if (!waveList.get(this.curWave).isEmpty())
                // take from current wave
                return waveList.items[curWave].takeEnemy();

        return Character.CharacterTypeName.VOID;
    }

    /*  Moves on to the next wave if the input time (which is the current in-game time)
     *  is greater than/equal to the next wave's time to start. Returns true if the wave
     *  was advanced, and false if not. */
    public boolean advanceWave(float curTime) {
        // if current wave is not the last wave:
        if (curWave < waveList.size - 1) {
            // If it is time for the next wave to start:
            if (curTime >= waveList.items[curWave].getTimeToStart()) {
                // advance to the next wave
                curWave++;
                return true;
            }
        }
        return false;
    }

    public int getCurWave() {
        return curWave + 1;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

}

