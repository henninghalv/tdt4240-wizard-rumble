package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Obstacle extends Entity {

    public Obstacle(Rectangle rect) {
        super(new Vector2(rect.getX(), rect.getY()), new Vector2(0,0), 0, null, new Vector2(rect.width, rect.height), "static");
        super.defineEntity();
        fixture.setUserData(this);

    }

    @Override
    public void onCollideWithSpell(int damage) {

    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}
