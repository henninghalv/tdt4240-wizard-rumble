package com.progark.group2.wizardrumble.network.resources;

import com.badlogic.gdx.math.Vector2;

public class Player {

    private String name;
    private int playerSlotId;
    private int kills;
    private int rank;
    private long timeAliveInMilliseconds;
    private boolean isAlive;
    private Vector2 position;
    private float rotation;

    public Player() {
    }

    public Player(String name, int playerSlotId, int kills, int rank, long timeAliveInMilliseconds, Vector2 position, float rotation) {
        this.name = name;
        this.playerSlotId = playerSlotId;
        this.kills = kills;
        this.rank = rank;
        this.timeAliveInMilliseconds = timeAliveInMilliseconds;
        this.isAlive = true;
        this.position = position;
        this.rotation = rotation;

    }

    public String getName() {
        return name;
    }

    public int getPlayerSlotId() {
        return playerSlotId;
    }

    public int getKills() {
        return kills;
    }

    public int getRank() {
        return rank;
    }

    public long getTimeAliveInMilliseconds() {
        return timeAliveInMilliseconds;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerSlotId(int playerSlotId) {
        this.playerSlotId = playerSlotId;
    }

    public void incrementKills() {
        this.kills++;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setTimeAliveInMilliseconds(long timeAliveInMilliseconds) {
        this.timeAliveInMilliseconds = timeAliveInMilliseconds;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
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
