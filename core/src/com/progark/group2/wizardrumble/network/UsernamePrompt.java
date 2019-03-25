package com.progark.group2.gameserver.resources;

import com.badlogic.gdx.Input;
import com.progark.group2.wizardrumble.network.NetworkController;

import java.io.IOException;

public class UsernamePrompt implements Input.TextInputListener {

    public UsernamePrompt() {
    }

    @Override
    public void input(String text) {
        try {
            NetworkController.getInstance().username = text;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void canceled() {

    }
}
