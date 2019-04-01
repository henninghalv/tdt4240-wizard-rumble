package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.progark.group2.wizardrumble.entities.Spell;
import static com.progark.group2.wizardrumble.Application.WIDTH;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellSelector1 extends SpellSelector {

    public SpellSelector1(List<String> listOfSpells, Stage stage) {
        super(listOfSpells, stage);
        createSpellButtons();
    }

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

    public void createSpellButtons() {
        if (listOfSpells.size() == 0) {
            throw new IllegalArgumentException("yo peeps, listOfSpells cannot be 0");
        }

        List<Integer> xCoords= new ArrayList<Integer>(Arrays.asList(-50, -80, -60, -20));
        List<Integer> yCoords= new ArrayList<Integer>(Arrays.asList(50, 100, 150, 200));

        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < super.listOfSpells.size(); i++) {
            spellButton = new CheckBox(listOfSpells.get(i), getSkin());
            spellButton.setX(WIDTH - AimInput1.diameter + xCoords.get(i));
            spellButton.setY(yCoords.get(i));

            spellButton.setWidth((float) 150 / listOfSpells.size());
            spellButton.setHeight((float) 100 / listOfSpells.size());
            spellButton.setVisible(true);


            addListenerToSpellButton(spellButton,listOfSpells.get(i));

            buttonGroup.add(spellButton);

            stage.addActor(spellButton);


        }

        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);

        //buttonGroup.setUncheckLast(true);
//        (buttonGroup.getButtons().get(0)).setChecked(true);





    }


    private void addListenerToSpellButton(CheckBox spellButton, final String spellName){
        spellButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //System.out.println(str + " selected, " + "checked="+spellButton.isChecked());
                //System.out.println("getAllChecked: "+spellButton.getButtonGroup().getAllChecked()+ "\n");
                //System.out.println("getChecked: "+spellButton.getButtonGroup().getChecked()+ "\n");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setSelectedSpell(spellName);
            }
        });
    }

    protected String getSelectedSpell() {
        return super.getSpellSelected();
    }

    protected void setSelectedSpell(String spellName) {
        super.setSpellSelected(spellName);
    }

}