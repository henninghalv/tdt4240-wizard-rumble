package com.progark.group2.wizardrumble.network.resources;

import com.badlogic.gdx.math.Vector2;

public class Player {

    private String name;
    private int connectionId;
    private int kills;
    private int rank;
    private int timeAliveInMilliseconds;
    private Vector2 position;
    private float rotation;

    public Player() {
    }

    public Player(String name, int connectionId, int kills, int rank, int timeAliveInMilliseconds, Vector2 position, float rotation) {
        this.name = name;
        this.connectionId = connectionId;
        this.kills = kills;
        this.rank = rank;
        this.timeAliveInMilliseconds = timeAliveInMilliseconds;
        this.position = position;
        this.rotation = rotation;

    }

    public String getName() {
        return name;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public int getKills() {
        return kills;
    }

    public int getRank() {
        return rank;
    }

    public int getTimeAliveInMilliseconds() {
        return timeAliveInMilliseconds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setTimeAliveInMilliseconds(int timeAliveInMilliseconds) {
        this.timeAliveInMilliseconds = timeAliveInMilliseconds;
    }

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
