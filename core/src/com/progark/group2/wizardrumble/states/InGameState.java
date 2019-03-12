package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.progark.group2.wizardrumble.entities.Wizard;

import static com.badlogic.gdx.Input.Keys;

public class InGameState extends State {

    private Wizard wizard;
    private Texture wizardSprite;
    private TextureRegion region;

    public InGameState(){
  
    public InGameState(GameStateManager gameStateManager){
        super(gameStateManager);
        wizard = new Wizard(new Vector2(200,200));
        wizardSprite = new Texture("wizard.jpg");
        region = new TextureRegion(wizardSprite);
    }

    @Override
    public void update(float dt) {
        //updatePlayerRotation(direction);
        /*
        if (leftJoy.isTouched()){
            float angle = direction.angle();
            Vector2 movement = new Vector2((float) Math.sin(angle), (float) Math.cos(angle)); // Kan hende man må flippe sin og cos.
            position.x += movement.x;
            position.y += movement.y;
        }
        */


        if(Gdx.input.isKeyPressed(Keys.UP)){
            wizard.move(new Vector2(0,2));
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT)){
            wizard.move(new Vector2(2,0));
        }
        if(Gdx.input.isKeyPressed(Keys.DOWN)){
            wizard.move(new Vector2(0,-2));
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)){
            wizard.move(new Vector2(-2,0));
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(region, wizard.getPosition().x,wizard.getPosition().y,
                wizardSprite.getWidth()/(float)2,
                wizardSprite.getHeight()/(float)2,
                wizardSprite.getWidth(), wizardSprite.getHeight(),
                1,1,0);
        sb.end();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }
}
