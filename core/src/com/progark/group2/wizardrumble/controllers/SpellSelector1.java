package com.progark.group2.wizardrumble.controllers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.progark.group2.wizardrumble.Application.WIDTH;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellSelector1 extends SpellSelector {

    private ButtonGroup spellSelector;

    public SpellSelector1(List<String> listOfSpells, Stage stage) {
        super(listOfSpells, stage);
        buttonList = new ArrayList<ImageButton>();
        createSpellButtons();
    }

    private ImageButton spellButton;

    private ArrayList<ImageButton> buttonList;

    private void createSpellButtons() {
        if (listOfSpells.size() == 0) {
            throw new IllegalArgumentException("ListOfSpells cannot be 0");
        }

        //Predefined positions for each button, max 4
        List<Integer> xCoords= new ArrayList<Integer>(Arrays.asList(-100, -80, -60, -40));
        List<Integer> yCoords= new ArrayList<Integer>(Arrays.asList(50, 120, 190, 260));

        spellSelector = new ButtonGroup();

        // Instantiate all spells in the list and add them to one buttonGroup
        for (int i = 0; i < super.listOfSpells.size(); i++) {
            Texture buttonTexture = assignButtonTexture(listOfSpells.get(i));
            Texture buttonCooldownTexture = assignButtonCooldownTexture(listOfSpells.get(i));
            Texture buttonDownTexture = assignButtonDownTexture(listOfSpells.get(i));
            Texture buttonCheckedTexture = assignButtonCheckedTexture(listOfSpells.get(i));

            Drawable drawableButtonDefault = new TextureRegionDrawable(new TextureRegion(buttonTexture));
            Drawable drawableButtonCooldown = new TextureRegionDrawable(new TextureRegion(buttonCooldownTexture));
            Drawable drawableButtonDown = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));
            Drawable drawableButtonChecked = new TextureRegionDrawable(new TextureRegion(buttonCheckedTexture));


            //spellButton = new ImageButton(drawableButtonDefault, drawableButtonDown, drawableButtonChecked);

            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();

            style.imageChecked = drawableButtonChecked;
            style.imageUp = drawableButtonDefault;
            style.imageDown = drawableButtonDown;
            style.imageDisabled = drawableButtonCooldown;

            spellButton = new ImageButton(style);


            // Places the buttons relative to the right joystick and the predefined list
            spellButton.setX(WIDTH - TouchPadAimInput1.diameter + xCoords.get(i));

            // Places the buttons relative according to the predefined list
            spellButton.setY(yCoords.get(i));

            spellButton.setWidth((float) 200 / listOfSpells.size());
            spellButton.setHeight((float) 200 / listOfSpells.size());
            spellButton.setVisible(true);
            spellButton.setName(listOfSpells.get(i));

            addListenerToSpellButton(spellButton, listOfSpells.get(i));

            spellSelector.add(spellButton);
            buttonList.add(spellButton);

            stage.addActor(spellButton);

        }

        // Ensures only one spell can be selected at any time(radio buttons)
        spellSelector.setMaxCheckCount(1);
        spellSelector.setMinCheckCount(1);
    }

    private Texture assignButtonTexture(String spellname){
        if(spellname.equals( "FireBall")){
            return new Texture("fire_button.png");
        }
        else if(spellname.equals("Ice")){
            return new Texture("ice_button.png");
        }
        else{
            // THIS SHOULD NEVER HAPPEN!
            return null;
        }
    }

    private Texture assignButtonDownTexture(String spellname){
        if(spellname.equals( "FireBall")){
            return new Texture("fire_button_down.png");
        }
        else if(spellname.equals("Ice")){
            return new Texture("ice_button_down.png");
        }
        else{
            // THIS SHOULD NEVER HAPPEN!
            return null;
        }
    }

    private Texture assignButtonCheckedTexture(String spellname){
        if(spellname.equals( "FireBall")){
            return new Texture("fire_button_checked.png");
        }
        else if(spellname.equals("Ice")){
            return new Texture("ice_button_checked.png");
        }
        else{
            // THIS SHOULD NEVER HAPPEN!
            return null;
        }
    }

    private Texture assignButtonCooldownTexture(String spellname){
        if(spellname.equals( "FireBall")){
            return new Texture("fire_button_cooldown.png");
        }
        else if(spellname.equals("Ice")){
            return new Texture("ice_button_cooldown.png");
        }
        else{
            // THIS SHOULD NEVER HAPPEN!
            return null;
        }
    }

    public void disableButton(String spellname){
        buttonList.get(listOfSpells.indexOf(spellname)).setDisabled(true);

    }

    public void enableButton(String spellname){
        buttonList.get(listOfSpells.indexOf(spellname)).setDisabled(false);
    }


    private void addListenerToSpellButton(ImageButton spellButton, final String spellName){
        spellButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
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