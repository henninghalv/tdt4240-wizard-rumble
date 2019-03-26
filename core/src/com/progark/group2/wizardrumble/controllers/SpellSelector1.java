package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class SpellSelector1 {

    private TextButton spellButton;

    public Skin getSkin() {
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("black", new Texture(pixmap));
        skin.add("green", new Texture(pixmap));

        skin.add("default", new BitmapFont());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("black", new

                Color(0, 0, 0, 0.5f));
        textButtonStyle.down = skin.newDrawable("green", new

                Color(0, 0.5f, 0.5f, 0.75f));
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);
        return skin;

    }

    public TextButton createSpellButton(final String str, int x, int y) {

        spellButton = new TextButton(str, getSkin());
        spellButton.setX(x);
        spellButton.setY(y);
        spellButton.setWidth(60);
        spellButton.setHeight(40);
        spellButton.setVisible(true);

        spellButton.addListener(new InputListener() {

            //This does not work. Try Checkboxes.
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                spellButton.setChecked(true);
                //System.out.println("UP");
            }


            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println(str + " pressed down");
                return true;
            }
        });
        return spellButton;
    }
}