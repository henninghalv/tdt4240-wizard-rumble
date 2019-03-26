package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class WizardEnemy extends Wizard {

    public WizardEnemy(int maxHealth, Vector2 spawnPoint) {
        super(maxHealth, spawnPoint);
    }
}
