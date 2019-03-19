package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.math.Vector2;

public class WizardEnemy extends Wizard {
    public WizardEnemy(int maxHealth, Vector2 spawnPoint) {
        super(maxHealth, spawnPoint);
    }

    @Override
    public int getPlayerID() {
        return super.getPlayerID();
    }

    @Override
    public void setPlayerID(int playerID) {
        super.setPlayerID(playerID);
    }

    public void setHealth(int health) {
        super.setHealthH(health);
    }
}
