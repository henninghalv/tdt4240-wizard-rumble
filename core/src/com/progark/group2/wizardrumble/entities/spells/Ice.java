package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Ice extends Spell {

    private Texture fireBallSprite;
    private TextureRegion region;
    private float scale;
    // velocity
    // rotation
    // position
    public final static Texture texture = new Texture("fireball_small.png");
    public final static long cooldown = 1500;


    public Ice(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity){
        super(spellOwnerID, spawnPoint, rotation, velocity, texture,10, 300f, "", cooldown, 1);
        this.velocity = velocity; // Use speed in spell abstract class in addition to this.
        this.rotation = rotation;
        this.position = spawnPoint;
        fireBallSprite = new Texture("fireball.png");
        region = new TextureRegion(fireBallSprite);
        scale = 0.02f;
        //name="Ice";
    }



    @Override
    public void dispose() {

    }
}
