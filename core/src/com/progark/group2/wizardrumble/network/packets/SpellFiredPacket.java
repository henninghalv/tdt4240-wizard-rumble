package com.progark.group2.wizardrumble.network.packets;

import com.badlogic.gdx.math.Vector2;

public class SpellFiredPacket extends Packet {
    private String spellType;
    private Vector2 spawnPoint;
    private Vector2 velocity;
    private float rotation;

    public SpellFiredPacket() {
    }

    public String getSpellType() {
        return spellType;
    }

    public void setSpellType(String spellType) {
        this.spellType = spellType;
    }

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Vector2 spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
