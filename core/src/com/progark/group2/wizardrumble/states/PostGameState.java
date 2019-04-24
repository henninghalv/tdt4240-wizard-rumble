package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

public class PostGameState extends State {
    private static PostGameState instance = null;
    private Map stats;

    public PostGameState(GameStateManager gameStateManager){
        super(gameStateManager);
    }

    public static PostGameState getInstance(){
        if(instance == null){
            return new PostGameState(GameStateManager.getInstance());
        }
        return instance;
    }

    public void updateDB(){

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }
}
