package com.progark.group2.wizardrumble.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.progark.group2.wizardrumble.states.InGameState;


public abstract class Wizard extends Entity {
    private Body b2body; // the body that contains the wizard in the box2d world
    protected int playerID;
    protected int health;
    protected int maxHealth;
    private int speed = 100;
    private Texture wizardSprite;
    public final static int DEFAULT_HEALTH = 100;
    private Touchpad leftJoy; // importer touchpad-objektet som Bjørn lager
    private Touchpad rightJoy; // importer touchpad-objektet som Bjørn lager
    private final float ANGLE_OFFSET = 270;


    public Wizard(int maxHealth, Vector2 spawnPoint) {
        wizardSprite = new Texture("wizard_front.png");
        this.position = spawnPoint;
        this.maxHealth = maxHealth;
        defineWizard();
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void attack(){

    }

    private void defineWizard(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x + (1/2f * wizardSprite.getWidth()), position.y + (1/2f * wizardSprite.getHeight()));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = InGameState.world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(wizardSprite.getWidth() / 2f, wizardSprite.getHeight() / 2f);

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }


    public void updatePosition(Vector2 direction) {
        // Stops the wizard from moving when you're not using the left joystick.
        if (direction == new Vector2(0, 0)) {
            b2body.setLinearVelocity(0, 0);
        }
        b2body.setLinearVelocity(speed * direction.x, speed * direction.y);
        position.x = b2body.getPosition().x - (wizardSprite.getWidth() / 2f);
        position.y = b2body.getPosition().y - (wizardSprite.getHeight() / 2f);

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


    public Body getB2body(){
        return b2body;
    }

    public Texture getSprite(){
        return wizardSprite;
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
