package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.progark.group2.wizardrumble.entities.Entity;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;

import java.io.IOException;

public abstract class Spell extends Entity {
    protected int damage;
    protected float speed;
    protected String statusEffect;
    protected int cooldown;
    protected int castTime;

    private TextureRegion region;
    private float scale;
    private Body b2body;

    public Spell(Vector2 spawnPoint, float rotation, Vector2 velocity, Texture texture, int damage, float speed, String statusEffect, int cooldown, int castTime){
        super(spawnPoint, velocity, rotation, texture, new Vector2(texture.getWidth(), texture.getHeight()), "dynamic");
        this.damage = damage;
        // Define the spell's physical body in the world
        super.defineEntity();
        //System.out.println(super.texture);
        region = new TextureRegion(super.texture);
        scale = 0.2f; // Tweak the scale as necessary
        b2body = super.b2body;
        this.speed = speed;
    }

        // There's a difference in screen coordinates and global coordinates. Where you draw each sprite
    // needs to be "local" while the global coordinates is useful for
    public Vector2 getGlobalPosition(){
        // TODO return global position, if that's even needed
        return null;
    }

    public int getDamage(){
        return damage;
    }

    public Body getB2body() {
        return b2body;
    }

    private void updatePosition(){
        b2body.setLinearVelocity(speed * velocity.x, speed * velocity.y);
        position.x = b2body.getPosition().x - (super.texture.getWidth() / 2f);
        position.y = b2body.getPosition().y - (super.texture.getHeight() / 2f);
    }

    public void destroySpell() throws IOException {
        InGameState.getInstance().addToBodyList(b2body);
        InGameState.getInstance().removeSpell(this);
    }

    @Override
    public void onCollideWithSpell(int damage) {

    }

    @Override
    public void update() {
        updatePosition();
    }


    @Override
    public void render(SpriteBatch sb) {
        sb.draw(region, super.position.x, super.position.y,
                super.texture.getWidth()/2f,
                super.texture.getHeight()/2f,
                super.texture.getWidth(), super.texture.getHeight(),1,1,rotation);
    }

    @Override
    public void dispose() {

    }
}
