package com.survivors.mygame;

import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/* Nick: This class represents all the enemies in a level, by storing a list of
         the enemy waves that spawn in said level. */

public class WaveList {

    // The list of waves for a given level
    private Array<Wave> waveList;

    // The index of the current wave being added to the game
    int curWave;
    // Represents if all waves have been exhausted
    boolean isEmpty;

    // Regex for matching (enemyType, number) pairs from a wave file
    static String enemyPairRegex = "([a-zA-Z]+) +([0-9]+)";
    static Pattern PairPattern = Pattern.compile(enemyPairRegex);
    
    // May add another constructor that builds a list of waves from a text file
    public WaveList(Array<Wave> WL) {
        waveList = WL;
        curWave = 0;
        isEmpty = false;
    }

    // Builds a wave list from a text file
    public WaveList(String waveFilePath) throws FileNotFoundException {

        // Attempt to open the text file:
        File waveFile = new File(waveFilePath);
        if (!waveFile.exists())
            throw new FileNotFoundException("Attempted and failed to open this file: " + waveFilePath);

        // Create a scanner to go through each line of the file
        Scanner scanner = new Scanner(waveFile);

        // The first line of a WaveList file is the number of waves
        // For some reason the first line is "SampleWaves.txt" ?
        int nWaves = Integer.parseInt(scanner.nextLine());
        // Create (nWaves) indices in waveList
        waveList = new Array<Wave>();

        // Iterate through each remaining wave in the file
        for (int i = 0; i < nWaves; i++) {

            // skip the empty line
            scanner.nextLine();

            // the first line is this wave's timeToStart attribute
            float newTime = Float.parseFloat(scanner.nextLine());

            // the next line is the number of (enemyType, enemyNum) pairs in this wave
            int nPairs = Integer.parseInt(scanner.nextLine());
            Array<Character.CharacterTypeName> newTypes = new Array<>();
            Array<Integer> newNums = new Array<>();

            // the next nTypes lines give the respective (enemyType, number) pairs:
            for (int j = 0; j < nPairs; j++) {

                // applies the regex to split the pair
                Matcher M = PairPattern.matcher(scanner.nextLine());
                M.find();

                String newType = M.group(1);
                String newNum = M.group(2);

                newTypes.add(Character.CharacterTypeName.valueOf(newType));
                newNums.add(Integer.parseInt(newNum));
            }

            // Initialize the next wave in waveList[]
            waveList.add(new Wave(newTime, newTypes, newNums));
        }

        scanner.close();

        curWave = 0;
        isEmpty = false;
    }


    /* Calls the current wave's takeEnemy() method. If the current wave is empty,
       this function only returns the VOID character type, as we then wait for time
       to pass for the next wave to start. */
    public Character.CharacterTypeName takeEnemy() {
        if (this.curWave < waveList.size) {
            // if current wave is not empty:
            if (!waveList.get(this.curWave).isEmpty()) {
                // take from current wave
                return waveList.get(this.curWave).takeEnemy();
            }
        }

        return Character.CharacterTypeName.VOID;
    }

    /*  Moves on to the next wave if the input time (which is the current in-game time)
     *  is greater than/equal to the next wave's time to start. Returns true if the wave
     *  was advanced, and false if not. */
    public boolean advanceWave(float curTime) {
        // if current wave is not the last wave:
        if (curWave < waveList.size - 1) {
            // If it is time for the next wave to start:
            if (curTime >= waveList.get(curWave).getTimeToStart()) {
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

