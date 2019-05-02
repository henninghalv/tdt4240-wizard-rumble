package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class FireBall extends Spell {
    public final static Texture texture = new Texture("fireball_small.png");
    public final static long cooldown = 3000;

    public FireBall(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity){
        super(spellOwnerID, spawnPoint, rotation, velocity, texture, 100, 60, "", cooldown, 1);
    }


}
