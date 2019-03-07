package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    private Vector2 position;
    private Vector2 velocity;
    private boolean isCollidable;
    private Sprite sprite;

    public abstract void onCollision();

    public abstract void update();

    public abstract void render();

    public abstract void dispose();

}
