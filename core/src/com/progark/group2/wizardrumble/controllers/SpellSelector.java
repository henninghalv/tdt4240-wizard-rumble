package com.progark.group2.wizardrumble.controllers;


import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.List;

public abstract class SpellSelector {
    protected List<String> listOfSpells;
    protected String spellSelected;
    protected Stage stage;


    public SpellSelector(List<String> listOfSpells, Stage stage){
        this.listOfSpells=listOfSpells;
        this.stage=stage;
    }


    public String getSpellSelected() {
        return spellSelected;
    }

    public void setSpellSelected(String spellSelected) {
        this.spellSelected = spellSelected;
    }


}