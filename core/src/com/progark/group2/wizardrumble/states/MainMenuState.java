package com.progark.group2.wizardrumble.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuState extends State {

    public MainMenuState(GameStateManager gameStateManager){
        super(gameStateManager);

    }

    public void startGame(){

    }

    public void openSettings(){

    }

    public void openPlayerSettings(){

    }

    @Override
    public void update(float dt) {

    }

    @Override

    public void render(SpriteBatch spriteBatch) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }
}
