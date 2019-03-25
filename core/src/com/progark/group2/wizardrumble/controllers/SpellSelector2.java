package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class SpellSelector2 {

    private TextButton spellButton;

    public Skin getSkin() {
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", new

                Color(0, 0, 0, 1));
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);
        return skin;

    }


    public TextButton createSpellButton() {

        spellButton = new TextButton("RESET", getSkin());
        spellButton.setX(5);
        spellButton.setY(Gdx.graphics.getHeight() - 25);
        spellButton.setWidth(60);
        spellButton.setVisible(true);
        return spellButton;
    }
}
