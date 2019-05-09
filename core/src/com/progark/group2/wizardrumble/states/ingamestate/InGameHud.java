package com.progark.group2.wizardrumble.states.ingamestate;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.progark.group2.wizardrumble.controllers.TouchPadAimInput1;
import com.progark.group2.wizardrumble.controllers.TouchPadMovementInput1;
import com.progark.group2.wizardrumble.controllers.SpellSelector1;


import java.util.ArrayList;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameHud {

    private Stage stage;
    private FitViewport stageViewport;
    private HealthBar healthBar;

    private TouchPadMovementInput1 leftJoyStick;
    private TouchPadAimInput1 rightJoyStick;

    private SpellSelector1 spellSelector;
    private ArrayList<String> spellNames;

    public InGameHud(SpriteBatch spriteBatch) {
        stageViewport = new FitViewport(WIDTH, HEIGHT, new OrthographicCamera());
        stage = new Stage(stageViewport, spriteBatch); //create stage with the stageViewport and the SpriteBatch given in Constructor

        leftJoyStick = TouchPadMovementInput1.getInstance();
        rightJoyStick = TouchPadAimInput1.getInstance();

        stage.addActor(leftJoyStick);
        stage.addActor(rightJoyStick);

        spellNames = new ArrayList<String>();
        spellNames.add("FireBall");
        spellNames.add("Ice");

        spellSelector = new SpellSelector1(spellNames, stage);
        spellSelector.setSpellSelected("FireBall");  // Setting default spell to Fireball

        healthBar = new HealthBar();
        stage.addActor(healthBar);

    }

    public Stage getStage() { return stage; }

    public void dispose(){
        stage.dispose();
    }

    public HealthBar getHealthBar(){
        return healthBar;
    }

    public TouchPadMovementInput1 getLeftJoyStick() {
        return leftJoyStick;
    }

    public TouchPadAimInput1 getRightJoyStick() {
        return rightJoyStick;
    }

    public SpellSelector1 getSpellSelector() {
        return spellSelector;
    }

    public ArrayList<String> getSpellNames() {
        return spellNames;
    }
}
