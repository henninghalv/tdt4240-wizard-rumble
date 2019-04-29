package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.progark.group2.wizardrumble.entities.spells.Spell;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;

import java.io.IOException;
import java.util.HashMap;


public abstract class Wizard extends Entity {

    public final static int DEFAULT_HEALTH = 100;
    private final static float ANGLE_OFFSET = 270;

    private int speed = 25;
    protected int health;
    private HashMap<String, Texture> directions = new HashMap<String, Texture>();
    private Texture playerDeadTexture = new Texture("grave.png");


    public Wizard(Vector2 spawnPoint, int id) {
        super(spawnPoint, new Vector2(0,0), 0, new Texture("wizard/blue/wizard_front.png"), new Vector2(new Texture("wizard_front.png").getWidth(), new Texture("wizard_front.png").getHeight()), "dynamic");
        String path = "";
        if(id == 1) path = "wizard/black/";
        else if(id == 2) path = "wizard/green/";
        else if(id == 3) path = "wizard/purple/";
        else if(id == 4) path = "wizard/yellow/";
        else if(id == 5) path = "wizard/red/";
        else path = "wizard/blue/";
        super.texture = new Texture(path + "wizard_front.png");
        super.defineRectangleEntity();
        this.health = DEFAULT_HEALTH;
        directions.put("up", new Texture(path + "wizard_back.png"));
        directions.put("down", new Texture(path + "wizard_front.png"));
        directions.put("left", new Texture(path + "wizard_leftfacing.png"));
        directions.put("right", new Texture(path + "wizard_rightfacing.png"));
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
        b2body.setTransform(position.x + (texture.getWidth() / 2f), position.y + (texture.getWidth() / 2f), 0);
    }

    public float getRotation(){
        return this.rotation;
    }

    public Texture getPlayerDeadSprite() { return playerDeadTexture; }

    public int getHealth(){ return health; }

    public Body getB2body(){ return b2body; }

    public Texture getPlayerSprite(){
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
    public void onCollideWithSpell(Spell spell) {
        System.out.println("Spell owner ID: " + spell.getSpellOwnerID());
        health -= spell.getDamage();
        if(this instanceof WizardPlayer){
            try {
                if(health <= 0){
                    NetworkController.getInstance().playerKilledBy(spell.getSpellOwnerID());
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
