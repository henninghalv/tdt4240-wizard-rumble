package com.progark.group2.wizardrumble.states.ingamestate;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;
import com.progark.group2.wizardrumble.controllers.SpellSelector1;
import com.progark.group2.wizardrumble.controllers.SpellSelector;


import java.util.ArrayList;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameHud {

    private Stage stage;
    private FitViewport stageViewport;
    private HealthBar healthBar;

    private MovementInput1 leftJoyStick;
    private AimInput1 rightJoyStick;

    private SpellSelector spellSelector;
    private ArrayList<String> spellNames;

    public InGameHud(SpriteBatch spriteBatch) {
        stageViewport = new FitViewport(WIDTH, HEIGHT, new OrthographicCamera());
        stage = new Stage(stageViewport, spriteBatch); //create stage with the stageViewport and the SpriteBatch given in Constructor

        leftJoyStick = new MovementInput1(15, 15);
        rightJoyStick = new AimInput1(WIDTH-15- AimInput1.diameter, 15);

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

    public MovementInput1 getLeftJoyStick() {
        return leftJoyStick;
    }

    public AimInput1 getRightJoyStick() {
        return rightJoyStick;
    }

    public SpellSelector getSpellSelector() {
        return spellSelector;
    }

    public ArrayList<String> getSpellNames() {
        return spellNames;
    }
}
