package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuSettings extends SettingsState {

    private static MainMenuSettings instance = null;

    private MainMenuSettings(){
        super(GameStateManager.getInstance());
    }

    protected static MainMenuSettings getInstance() {
        if (instance == null) {
            instance = new MainMenuSettings();
        }
        return instance;
    }


    @Override
    public void update(float dt) {

    }

    @Override
    public void applyChanges() {

    }

    @Override
    public void handleInput() {

    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }
}
