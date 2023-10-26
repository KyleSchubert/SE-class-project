package com.survivors.mygame;

import java.util.ArrayList;

public class PlayerCharacter extends Mobile {
    // Will be tangible
    // Requires keyboard control input


    // This will hold the EquippableItems for controlling when they trigger
    // and what their levels/bonuses from stats are (ex: +1 projectile)
    ArrayList<EquippableItem> equipment = new ArrayList<>();
}
