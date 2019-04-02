package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.progark.group2.wizardrumble.states.InGameState;

public abstract class Entity {
    protected Vector2 position;
    protected Vector2 velocity;
    protected Texture texture;
    protected Vector2 size;
    protected float rotation; // Definert som en Vector2.angle()
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



    protected void defineEntity() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x + (1/2f * size.x), position.y + (1/2f * size.y));
        if (bodyType.equals("dynamic")){
            bdef.type = BodyDef.BodyType.DynamicBody;
        }
        else{
            bdef.type = BodyDef.BodyType.StaticBody;
        }
        b2body = InGameState.world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / 2f, size.y / 2f);

        fdef.shape = shape;
        fixture = b2body.createFixture(fdef);
        fixture.setUserData(this);
    }
    public Vector2 getSize(){
        return size;
    }

    public abstract void onCollideWithSpell(int damage);

    public abstract void update();

    public abstract void render(SpriteBatch sb);

    public abstract void dispose();

}
