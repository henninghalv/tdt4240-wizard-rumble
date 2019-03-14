package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position;
    protected Vector2 velocity;
    protected boolean isCollidable;
    protected Sprite sprite;
    protected float rotation; // Definert som en Vector2.angle()

    public abstract void onCollision();

    public abstract void update();

    public abstract void render();

    public abstract void dispose();

}
