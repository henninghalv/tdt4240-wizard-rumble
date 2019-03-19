package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.progark.group2.wizardrumble.network.NetworkController;

import java.io.IOException;

public class Wizard extends Entity {
    private int playerID;
    private int health;
    private int maxHealth;
    private Touchpad leftJoy; // importer touchpad-objektet som Bjørn lager
    private Touchpad rightJoy; // importer touchpad-objektet som Bjørn lager

    public Wizard(int maxHealth, Vector2 spawnPoint){
        position = spawnPoint;

        // Replenishes health to full
        this.maxHealth = maxHealth;
        health = maxHealth;
    }

    public void attack(){

    }

    public void updatePosition(Vector2 direction){
        position.x += direction.x;
        position.y += direction.y;

    }

    /**
     * Subtracts the player's health equal to the damage taken
     * @param damage    (int) damage taken
     * @throws IOException  If the deadPlayerRequest fails
     */
    public void takeDamage(int damage) throws IOException {
        health -= damage;

        // If the player died
        if (health <= 0) {
            NetworkController.getInstance().sendPlayerDeadRequest();
        }
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
