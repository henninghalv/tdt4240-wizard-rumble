package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class State {

    private OrthographicCamera camera;

    public abstract void update();
    public abstract void render();
    public abstract void dispose();
    // No designated button for this method, use the back button on the device
    public abstract void onBackButtonPress();
}
