package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.math.Vector2;

public class WizardEnemy extends Wizard {
    public WizardEnemy(int maxHealth, Vector2 spawnPoint) {
        super(maxHealth, spawnPoint);
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
