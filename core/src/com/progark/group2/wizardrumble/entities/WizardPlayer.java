package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;

import java.io.IOException;


public class WizardPlayer extends Wizard {

    private static WizardPlayer instance = null;

    public WizardPlayer(Vector2 spawnPoint) {
        super(DEFAULT_HEALTH, spawnPoint);
        super.health = maxHealth;
    }

    @Override
    public void onCollideWithSpell(int damage) {
        super.health -= damage;
        try {
            InGameState.getInstance().getInGameHud().getHealthBar().updateHealth(super.health);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Wizard's health: " + super.health);
       try {
           NetworkController.getInstance().sendPlayerTookDamageRequest(damage);
       }
       catch (IOException e){
            System.out.println("Problems sending damage to Server.");
       }

    }

    public static WizardPlayer getInstance() {
        if (instance == null) {
            // TODO: Find a safe position for the exception when a player hasn't been instansiated
            instance = new WizardPlayer(new Vector2(0,0));
        }
        return instance;
    }

}
