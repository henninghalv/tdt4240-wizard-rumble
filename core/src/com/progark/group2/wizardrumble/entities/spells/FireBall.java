package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class FireBall extends Spell {
    public final static Texture texture = new Texture("fireball_small.png");
    // Size shit
    private float width = 20;
    private float height = 20;

    public FireBall(Vector2 spawnPoint, float rotation, Vector2 velocity){

        super(spawnPoint, rotation, velocity, texture, 10, 200, "", 5, 1);

    }


}
