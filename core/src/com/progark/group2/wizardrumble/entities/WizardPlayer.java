package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class WizardPlayer extends Wizard {

    private static WizardPlayer instance = null;

    private Touchpad leftJoy; // importer touchpad-objektet som Bjørn lager
    private Touchpad rightJoy; // importer touchpad-objektet som Bjørn lager

    public WizardPlayer(int maxHealth, Vector2 spawnPoint) {
        super(maxHealth, spawnPoint);
    }

    public WizardPlayer(Vector2 spawnPoint) {
        super(spawnPoint);
    }

    public static WizardPlayer getInstance() {
        if (instance == null) {
            // TODO: Find a safe position for the exception when a player hasn't been instansiated
            instance = new WizardPlayer(new Vector2(0,0));
        }
        return instance;
    }

    public void attack(){

    }
}
