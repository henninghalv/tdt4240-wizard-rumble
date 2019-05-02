package com.progark.group2.wizardrumble.network.packets;

import com.badlogic.gdx.math.Vector2;
import com.progark.group2.wizardrumble.entities.spells.SpellType;

public class SpellFiredPacket{
    private int gameId;
    private SpellType spellType;
    private Vector2 spawnPoint;
    private Vector2 velocity;
    private float rotation;
    private int spellOwnerId;

    public SpellFiredPacket() {
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public SpellType getSpellType() {
        return spellType;
    }

    public void setSpellType(SpellType spellType) {
        this.spellType = spellType;
    }

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public int getSpellOwnerId() {
        return spellOwnerId;
    }

    public void setSpellOwnerId(int spellOwnerId) {
        this.spellOwnerId = spellOwnerId;
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
