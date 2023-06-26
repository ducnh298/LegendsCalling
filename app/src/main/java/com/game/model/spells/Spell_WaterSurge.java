package com.game.model.spells;

public class Spell_WaterSurge extends Spell {
    public Spell_WaterSurge() {
        super("Water Surge", -7, 3);
        description = "Deflecting the monster attack and heal you by " + (-damage) + ".";
    }

    @Override
    public void activeEffect() {
    }
}
