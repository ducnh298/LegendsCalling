package com.game.model.effect;

public class Effect_Poisonous extends Effect {
    public Effect_Poisonous() {
        super("Poisonous", 5, 2);
        descriptionToMonster = "The monster is poisoned, taking " + damage + " damage as the poison courses through its body.";
        descriptionToPlayer = "You are poisoned, taking " + damage + " damage as the poison courses through your body.";
    }
}
