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

    public void move(Vector2 direction){


        //updatePlayerRotation(direction);
        /*
        if (leftJoy.isTouched()){
            float angle = direction.angle();
            Vector2 movement = new Vector2((float) Math.sin(angle), (float) Math.cos(angle)); // Kan hende man må flippe sin og cos.
            position.x += movement.x;
            position.y += movement.y;
        }
        */

        //jank shit under here. Not to be used for real.
        position.x += direction.x;
        position.y += direction.y;

    }

    public void takeDamage(int damage){

    }

    public void updatePlayerRotation(Vector2 direction){
        if (rightJoy.isTouched()){
            Vector2 v = new Vector2(rightJoy.getKnobPercentX(), rightJoy.getKnobPercentY());
            rotation = v.angle(); // Test hvordan angle fungerer
        } else {
            rotation = direction.angle();
        }
        // runningframe.setRotation(angle);
    }

    public Vector2 getPosition(){
        return this.position;
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
