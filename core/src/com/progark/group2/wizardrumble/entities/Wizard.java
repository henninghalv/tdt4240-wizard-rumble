package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class Wizard extends Entity {
    protected int playerID;
    protected int health;
    protected int maxHealth;
    protected int DEFAULT_HEALTH = 100;
    private Touchpad leftJoy; // importer touchpad-objektet som Bjørn lager
    private Touchpad rightJoy; // importer touchpad-objektet som Bjørn lager
    private final float ANGLE_OFFSET = 270;


    public Wizard(Vector2 spawnPoint){
        position = spawnPoint;
    }

    public Wizard(int maxHealth, Vector2 spawnPoint){
        this.position = spawnPoint;
        this.maxHealth = maxHealth;
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
