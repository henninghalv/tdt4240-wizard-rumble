package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position;
    protected Vector2 velocity;
    protected boolean isCollidable;
    protected Sprite sprite;
    protected float rotation; // Definert som en Vector2.angle()

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public abstract void onCollision();

    public abstract void update();

    public abstract void render(SpriteBatch sb);

    public abstract void dispose();

}
