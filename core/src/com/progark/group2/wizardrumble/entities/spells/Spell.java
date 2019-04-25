package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.progark.group2.wizardrumble.entities.Entity;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;

import java.io.IOException;

import static com.progark.group2.wizardrumble.Application.SCALE;

public abstract class Spell extends Entity {
    protected int damage;
    protected float speed;
    protected String statusEffect;
    protected int cooldown;
    protected int castTime;
    protected int spellOwnerID;

    private TextureRegion region;
    private float scale;

    public Spell(int spellOwnerID, Vector2 spawnPoint, float rotation, Vector2 velocity, Texture texture, int damage, float speed, String statusEffect, int cooldown, int castTime){
        super(spawnPoint, velocity, rotation, texture,new Vector2(texture.getWidth(),texture.getHeight()), "dynamic");
        this.damage = damage;
        this.speed = speed;
        this.spellOwnerID = spellOwnerID;
        // Define the spell's physical body in the world
        defineRectangleEntity();
        //System.out.println(super.texture);
        region = new TextureRegion(texture);
        scale = 0.2f; // Tweak the scale as necessary
        b2body.setTransform(spawnPoint, (float)Math.toRadians(rotation));
        this.speed = speed;
    }

    public int getDamage(){
        return damage;
    }

    public Body getB2body() {
        return b2body;
    }

    public int getSpellOwnerID() {
        return spellOwnerID;
    }

    private void updatePosition(){
        b2body.setLinearVelocity(speed * velocity.x, speed * velocity.y);
        position.x = b2body.getPosition().x - (texture.getWidth() / 2f);
        position.y = b2body.getPosition().y - (texture.getHeight() / 2f);
    }

    public void destroySpell() throws IOException {
        InGameState.getInstance().addToBodyList(b2body);
        InGameState.getInstance().removeSpell(this);
    }

    @Override
    public void onCollideWithSpell(Spell spell) {

    }

    @Override
    public void update() {
        updatePosition();
    }


    @Override
    public void render(SpriteBatch sb) {
        sb.draw(region, position.x, position.y,
                texture.getWidth()/2f,
                texture.getHeight()/2f,
                texture.getWidth(), texture.getHeight(), SCALE, SCALE, rotation);
    }

    @Override
    public void dispose() {

    }
}
