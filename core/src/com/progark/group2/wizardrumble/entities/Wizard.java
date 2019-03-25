package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class Wizard extends Entity {
    private int playerID;
    private int health;
    private int maxHealth;
    private Touchpad leftJoy; // importer touchpad-objektet som Bjørn lager
    private Touchpad rightJoy; // importer touchpad-objektet som Bjørn lager
    private final float ANGLE_OFFSET = 270;


    public Wizard(Vector2 spawnPoint){
        position = spawnPoint;
    }

    public void attack(){

    }

    public void updatePosition(Vector2 velocity){
        position.x += velocity.x;
        position.y += velocity.y;

    }

    public void takeDamage(int damage){

    }

    public void updateRotation(Vector2 direction){
        // Rotation is set with an offset, since the rotation-argument in SpriteBatch.draw() starts
        // at zero to the right, but we want it to be at zero upwards.
        // This can also be fixed by rotating the image. TODO Look into this when stuff starts to look ready.
        rotation = (direction.angle() + ANGLE_OFFSET);
    }

    public float getRotation(){
        return this.rotation;
    }

    public Vector2 getGlobalPosition(){
        // TODO return global position, if that's even needed
        return null;
    }


    @Override
    public void onCollision() {

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
