package com.survivors.mygame;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.badlogic.gdx.files.FileHandle;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static com.badlogic.gdx.Gdx.files;

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

    // Builds a wave list from a text file
    public WaveList(String waveFilePath) throws FileNotFoundException {

        curWave = 0;
        isEmpty = false;

        // Attempt to open the text file:
        FileHandle gdxFile = files.getFileHandle(waveFilePath, Files.FileType.Classpath);
        if (!gdxFile.exists()) {
            throw new FileNotFoundException("Attempted and failed to open this file: " + waveFilePath);
        }

        // fileLines[0] is the first line, fileLines[1] is the second line...
        String allFileContents = gdxFile.readString();
        String[] fileLines = allFileContents.split("\\r?\\n");

        // Keeps track of where we are in the file
        int lineIndex = 0;

        // skip empty or commented lines
        while (!isValidLine(fileLines[lineIndex]))
            lineIndex++;

        // The first valid line of a WaveList file should always be the number of waves
        int nWaves = Integer.parseInt(fileLines[lineIndex]);
        lineIndex++;

        // Initialize this instance's array of waves
        waveList = new Array<Wave>();

        // Iterate through each remaining wave in the file
        for (int i = 0; i < nWaves; i++) {

            // skip empty or commented lines
            while (!isValidLine(fileLines[lineIndex]))
                lineIndex++;

            // This line should have the time at which this wave starts in seconds
            float newTime = Float.parseFloat(fileLines[lineIndex]);
            lineIndex++;

            // skip empty or commented lines
            while (!isValidLine(fileLines[lineIndex]))
                lineIndex++;

            // The next line should have the number of enemy types in this wave
            int nPairs = Integer.parseInt(fileLines[lineIndex]);
            lineIndex++;

            // The arrays to hold this wave's data
            Array<Character.CharacterTypeName> newTypes = new Array<>();
            Array<Integer> newNums = new Array<>();

            // The next (nTypes) valid lines give the respective (enemyType, enemyNumber) pairs:
            while (nPairs > 0) {

                // skip empty or commented lines
                if (!isValidLine(fileLines[lineIndex])) {
                    lineIndex++;
                    continue;
                }

                // Extract the enemy type and number from this line using enemyPairRegex
                Matcher M = PairPattern.matcher(fileLines[lineIndex]);
                M.find();

                String newType = M.group(1);
                String newNum = M.group(2);

                newTypes.add(Character.CharacterTypeName.valueOf(newType));
                newNums.add(Integer.parseInt(newNum));

                nPairs--;
                lineIndex++;
            }

            // Initialize the next wave in waveList[]
            waveList.add(new Wave(newTime, newTypes, newNums));
        }

        System.out.println("hehe");
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

    // returns true if s is not a comment or an empty line
    public static boolean isValidLine(String s) {

        if (s.isEmpty())
            return false;

        int i = 0;
        while (i < s.length()) {
            char curChar = s.charAt(i);
            if (curChar == '*')
                return false;
            else if (curChar != ' ')
                return true;
            i++;
        }

        return false;
    }

}