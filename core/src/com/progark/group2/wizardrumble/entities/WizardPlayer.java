package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;

import java.io.IOException;


public class WizardPlayer extends Wizard {
    public WizardPlayer(Vector2 spawnPoint) {
        super(spawnPoint);
    }
}
