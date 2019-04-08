package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Obstacle extends Entity {

    public Obstacle(Polygon polygon) {
        super(new Vector2(polygon.getX(), polygon.getY()), new Vector2(0,0), 0, null, new Vector2(0,0), "static");
        definePolygonEntity(polygon.getVertices());
        fixture.setUserData(this);
    }

    public Obstacle(Rectangle rect){
        super(new Vector2(rect.getX(), rect.getY()), new Vector2(0,0), 0, null, new Vector2(rect.width, rect.height), "static");
        defineRectangleEntity();
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
