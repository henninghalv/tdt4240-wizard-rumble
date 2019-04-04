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

        skin.add("black", new Texture(pixmap)); // Used for unselected button
        skin.add("green", new Texture(pixmap)); // Used for selected button
        skin.add("default", new BitmapFont()); // Used for font

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();

        checkBoxStyle.up = skin.newDrawable("black",
                new Color(0, 0, 0, 0.5f)); // Used for unselected button
        checkBoxStyle.checked = skin.newDrawable("green",
                new Color(0, 0.5f, 0.5f, 0.75f)); // Used for selected button
        checkBoxStyle.font = skin.getFont("default");

        skin.add("default", checkBoxStyle);
        return skin;

    }



    public void createSpellButtons() {
        if (listOfSpells.size() == 0) {
            throw new IllegalArgumentException("ListOfSpells cannot be 0");
        }

        //Predefined positions that match the right joystick
        List<Integer> xCoords= new ArrayList<Integer>(Arrays.asList(-70, -100, -80, -40));
        List<Integer> yCoords= new ArrayList<Integer>(Arrays.asList(50, 100, 150, 200));

        ButtonGroup buttonGroup = new ButtonGroup();

        // Instantiate all spells in the list and add them to one buttonGroup
        for (int i = 0; i < super.listOfSpells.size(); i++) {
            spellButton = new CheckBox(listOfSpells.get(i), getSkin());

            // Places the buttons relative to the right joystick and the predefined list
            spellButton.setX(WIDTH - AimInput1.diameter + xCoords.get(i));

            // Places the buttons relative according to the predefined list
            spellButton.setY(yCoords.get(i));

            spellButton.setWidth((float) 150 / listOfSpells.size());
            spellButton.setHeight((float) 100 / listOfSpells.size());
            spellButton.setVisible(true);

            addListenerToSpellButton(spellButton,listOfSpells.get(i));

            buttonGroup.add(spellButton);

            stage.addActor(spellButton);
        }

        // Ensures only one spell can be selected at any time(radio buttons)
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);
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