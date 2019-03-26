package com.progark.group2.wizardrumble.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import java.io.IOException;

public class UsernamePrompt implements Input.TextInputListener {

    public UsernamePrompt() {
    }

    @Override
    public void input(String text) {
        Preferences preferences = Gdx.app.getPreferences("user");
        preferences.putString("username", text);
        preferences.flush();
        try {
            NetworkController.getInstance().requestPlayerCreation(text);
            NetworkController.getInstance().requestGameCreation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void canceled() {
        Preferences preferences = Gdx.app.getPreferences("user");
        preferences.putString("username", "Guest");
        preferences.flush();

        // TODO: How to handle "Guest" profile?
    }
}
