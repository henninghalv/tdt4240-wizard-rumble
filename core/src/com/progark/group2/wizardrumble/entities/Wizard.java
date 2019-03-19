package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class Wizard extends Entity {
    private int playerID;
    private int health;
    private int maxHealth;
    private Touchpad leftJoy; // importer touchpad-objektet som Bjørn lager
    private Touchpad rightJoy; // importer touchpad-objektet som Bjørn lager

    public Wizard(Vector2 spawnPoint){
        position = spawnPoint;

    }

    public void attack(){

    }

    public void updatePosition(Vector2 direction){
        position.x += direction.x;
        position.y += direction.y;

    }

    public void takeDamage(int damage){

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
