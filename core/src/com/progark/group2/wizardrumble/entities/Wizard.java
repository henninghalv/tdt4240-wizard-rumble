package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;

import java.io.IOException;
import java.util.HashMap;


public abstract class Wizard extends Entity {


    public final static int DEFAULT_HEALTH = 100;

    private final static float ANGLE_OFFSET = 270;
    private int speed = 35;
    protected int health;
    private HashMap<String, Texture> directions = new HashMap<String, Texture>();


    public Wizard(Vector2 spawnPoint) {
        super(spawnPoint, new Vector2(0,0), 0, new Texture("wizard_front.png"), new Vector2(new Texture("wizard_front.png").getWidth(), new Texture("wizard_front.png").getHeight()), "dynamic");
        super.defineRectangleEntity();
        this.position = spawnPoint;
        this.health = DEFAULT_HEALTH;
        directions.put("up", new Texture("wizard_back.png"));
        directions.put("down", new Texture("wizard_front.png"));
        directions.put("left", new Texture("wizard_leftfacing.png"));
        directions.put("right", new Texture("wizard_rightfacing.png"));
    }

    public void updatePosition(Vector2 direction) {
        // Stops the wizard from moving when you're not using the left joystick.
        if (direction == new Vector2(0, 0)) {
            b2body.setLinearVelocity(0, 0);
        }
        b2body.setLinearVelocity(speed * direction.x, speed * direction.y);
        position.x = b2body.getPosition().x - (texture.getWidth() / 2f);
        position.y = b2body.getPosition().y - (texture.getHeight() / 2f);

    }

    public void updateRotation(Vector2 direction){
        // Rotation is set with an offset, since the rotation-argument in SpriteBatch.draw() starts
        // at zero to the right, but we want it to be at zero upwards.
        // This can also be fixed by rotating the image. TODO Look into this when stuff starts to look ready.
        rotation = (direction.angle() + ANGLE_OFFSET);
    }

    public void updateBodyPosition(Vector2 position){
        b2body.setTransform(position.x + (super.texture.getWidth() / 2f), position.y + (super.texture.getWidth() / 2f), 0);
    }

    public float getRotation(){
        return this.rotation;
    }

    public Texture getSprite(){
        return texture;
    }

    public int getHealth(){ return health; }

    public Body getB2body(){ return b2body; }

    public Texture getDirection(){
        if(rotation > 600 || rotation < 300){
            return directions.get("right");
        }
        else if(rotation > 500){
            return directions.get("down");
        }
        else if(rotation > 400){
            return directions.get("left");
        }
        else{
            return directions.get("up");
        }
    }

    @Override
    public void onCollideWithSpell(int damage) {
        health -= damage;
        if(this instanceof WizardPlayer){
            try {
                InGameState.getInstance().getInGameHud().getHealthBar().updateHealth(health);
                if(health <= 0){
                    NetworkController.getInstance().playerDied();
                    NetworkController.getInstance().getPlayer().setAlive(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
