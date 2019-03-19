package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class Wizard extends Entity {

    public static int DEFAULT_HEALTH = 100;

    protected int playerID;
    protected int health;
    protected int maxHealth;

    protected Wizard(int maxHealth, Vector2 spawnPoint) {
        position = spawnPoint;

        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be set to a positive integer");
        }
        // Replenishes health to full
        this.maxHealth = maxHealth;
        health = maxHealth;
    }

    public void updatePosition(Vector2 direction){
        position.x += direction.x;
        position.y += direction.y;
    }

    public void updateRotation(Vector2 direction){
        // Rotation is set with an offset, since the rotation-argument in SpriteBatch.draw() starts
        // at zero to the right, but we want it to be at zero upwards.
        float ANGLE_OFFSET = 270;
        rotation = (direction.angle() + ANGLE_OFFSET);
    }

    public Vector2 getPosition(){
        return this.position;
    }

    public float getRotation(){
        return this.rotation;
    }

    @Override
    public void onCollision() {

    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void dispose() {

    }
}
