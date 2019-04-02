package com.progark.group2.wizardrumble.states.resources;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;

public class UIButton {

    private Stack button;

    public UIButton(Texture texture, String buttonText) {
        // Button text styling
        BitmapFont font = new BitmapFont();
        font.setColor(Color.BLACK);
        Label label = new Label(buttonText, new LabelStyle(font, font.getColor()));
        label.setAlignment(Align.center);

        Image buttonImage = new Image(texture);

        Stack button = new Stack();
        button.add(buttonImage);
        button.add(label);
        this.button = button;
    }

    public Stack getButton() {
        return button;
    }
}
