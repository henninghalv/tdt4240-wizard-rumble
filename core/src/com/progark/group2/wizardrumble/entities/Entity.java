package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.progark.group2.wizardrumble.entities.spells.Spell;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;

import static com.progark.group2.wizardrumble.Application.SCALE;

public abstract class Entity {
    protected Vector2 position;
    protected Vector2 velocity;
    protected Texture texture;
    protected Vector2 size;
    protected float rotation; // Defined as Vector2.angle()
    protected Fixture fixture;
    protected Body b2body;
    protected String bodyType;

    protected Entity(Vector2 position, Vector2 velocity, float rotation, Texture texture, Vector2 size, String bodyType) {
        this.position = position;
        this.velocity = velocity;
        this.rotation = rotation;
        this.texture = texture;
        this.size = size;
        this.bodyType = bodyType;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    // Makes a b2body for all entities
    protected void defineRectangleEntity() {
        BodyDef bdef = new BodyDef();
        bdef.position.set((position.x + (1/2f * size.x)) * SCALE, (position.y + (1/2f * size.y)) * SCALE);
        if (bodyType.equals("dynamic")){
            bdef.type = BodyDef.BodyType.DynamicBody;
        }
        else{
            bdef.type = BodyDef.BodyType.StaticBody;
        }
        b2body = InGameState.world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((size.x / 2f) * SCALE, (size.y / 2f) * SCALE);

        fdef.shape = shape;
        fixture = b2body.createFixture(fdef);
        fixture.setUserData(this);
    }

    protected void definePolygonEntity(float[] vertices){
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x , position.y);
        if (bodyType.equals("dynamic")){
            bdef.type = BodyDef.BodyType.DynamicBody;
        }
        else{
            bdef.type = BodyDef.BodyType.StaticBody;
        }
        b2body = InGameState.world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        fdef.shape = shape;
        fixture = b2body.createFixture(fdef);
        fixture.setUserData(this);
    }

    public Vector2 getSize(){
        return size;
    }

    public abstract void onCollideWithSpell(Spell spell);

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public abstract void update();

    public abstract void render(SpriteBatch sb);

    public abstract void dispose();

}
