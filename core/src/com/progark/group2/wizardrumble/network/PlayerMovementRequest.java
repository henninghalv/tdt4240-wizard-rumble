package com.progark.group2.wizardrumble.network;

import com.badlogic.gdx.math.Vector2;

public class PlayerMovementRequest {

    private Vector2 position;
    private float rotation;

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
}
