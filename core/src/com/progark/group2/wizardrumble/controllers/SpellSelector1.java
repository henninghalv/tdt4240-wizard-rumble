package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SpellSelector1 {

    private CheckBox spellButton;

    public Skin getSkin() {
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("black", new Texture(pixmap));
        skin.add("green", new Texture(pixmap));

        skin.add("default", new BitmapFont());

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.up = skin.newDrawable("black", new

                Color(0, 0, 0, 0.5f));
        checkBoxStyle.down = skin.newDrawable("green", new

                Color(0, 0.5f, 0.5f, 0.75f));
        checkBoxStyle.checked = skin.newDrawable("green", new

                Color(0, 0.5f, 0.5f, 0.75f));
        checkBoxStyle.font = skin.getFont("default");
        skin.add("default", checkBoxStyle);
        return skin;

    }

    public CheckBox createSpellButton(final String str, int x, int y) {

        spellButton = new CheckBox(str, getSkin());
        spellButton.setX(x);
        spellButton.setY(y);
        spellButton.setWidth(60);
        spellButton.setHeight(40);
        spellButton.setVisible(true);


        spellButton.addListener(new InputListener() {




            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //System.out.println(str + " selected, " + "checked="+spellButton.isChecked());
                //System.out.println("getAllChecked: "+spellButton.getButtonGroup().getAllChecked()+ "\n");
                //System.out.println("getChecked: "+spellButton.getButtonGroup().getChecked()+ "\n");
                return true;
            }
        });
        return spellButton;
    }
}