package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameHud {

    private Stage stage;
    private FitViewport stageViewport;

    private MovementInput1 leftJoyStick;
    private AimInput1 rightJoyStick;

    public InGameHud(SpriteBatch spriteBatch) {
        stageViewport = new FitViewport(WIDTH, HEIGHT, new OrthographicCamera());
        stage = new Stage(stageViewport, spriteBatch); //create stage with the stageViewport and the SpriteBatch given in Constructor

        leftJoyStick = new MovementInput1(15, 15);
        rightJoyStick = new AimInput1(WIDTH-15- AimInput1.diameter, 15);

        stage.addActor(leftJoyStick);
        stage.addActor(rightJoyStick);
    }

    public Stage getStage() { return stage; }

    public void dispose(){
        stage.dispose();
    }

    public MovementInput1 getLeftJoyStick() {
        return leftJoyStick;
    }

    public AimInput1 getRightJoyStick() {
        return rightJoyStick;
    }
}
