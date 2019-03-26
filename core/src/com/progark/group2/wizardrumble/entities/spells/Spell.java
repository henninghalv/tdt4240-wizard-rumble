package com.progark.group2.wizardrumble.entities.spells;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.progark.group2.wizardrumble.entities.Entity;
import com.progark.group2.wizardrumble.states.InGameState;

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
        super(spawnPoint, velocity, rotation, texture);
        region = new TextureRegion(super.texture);
        scale = 0.2f; // Tweak the scale as necessary

        // Define the spell's physical body in the world
        super.defineEntity();
    }

    private void defineSpell() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x + (1/2f * super.texture.getWidth()), position.y + (1/2f * super.texture.getHeight()));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = InGameState.world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(super.texture.getWidth() / 2f, super.texture.getHeight() / 2f);

        fdef.shape = shape;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData("Spell");
    }

        // There's a difference in screen coordinates and global coordinates. Where you draw each sprite
    // needs to be "local" while the global coordinates is useful for
    public Vector2 getGlobalPosition(){
        // TODO return global position, if that's even needed
        return null;
    }

    private void updatePosition(){
        position.x += velocity.x;
        position.y += velocity.y;
    }

    @Override
    public void onCollision() {

        /*if (WizardPlayer.getInstance() == hit.target) {
            NetworkController.getInstance().sendPlayerTookDamageRequest();
        }*/
        // TODO: If colliding object is the Wizard Player instance,
        // TODO: Send request to server about taking damage

        // TODO: Destroy/Dispose/move out of sight (ObjectPool) spell instance
    }

    @Override
    public void update() {
        updatePosition();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.draw(region, getPosition().x, getPosition().y,
                super.texture.getWidth()/2f,
                super.texture.getHeight()/2f,
                super.texture.getWidth(), super.texture.getHeight(),scale,scale,rotation);
    }

    @Override
    public void dispose() {

    }
}
