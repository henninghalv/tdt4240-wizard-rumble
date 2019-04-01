package com.progark.group2.wizardrumble.network.responses;

import com.badlogic.gdx.math.Vector2;

public class PlayerMovementResponse extends Response {

    private Vector2 position;
    private float rotation;
    private int playerId;

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

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
