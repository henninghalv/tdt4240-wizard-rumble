package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class State{

    protected OrthographicCamera camera;
    protected GameStateManager gameStateManager;

    protected Stage stage;


    public State(GameStateManager gameStateManager){
        this.gameStateManager = gameStateManager;
        this.camera = new OrthographicCamera();
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch spriteBatch);
    public abstract void dispose();
    public abstract void handleInput();

    // No designated button for this method, use the back button on the device
    public abstract void onBackButtonPress();

    public void activate(){
        Gdx.input.setInputProcessor(stage);
    }
}
