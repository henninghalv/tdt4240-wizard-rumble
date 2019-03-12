package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class State {

    private OrthographicCamera camera;

    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();

    // No designated button for this method, use the back button on the device
    public abstract void onBackButtonPress();
}
