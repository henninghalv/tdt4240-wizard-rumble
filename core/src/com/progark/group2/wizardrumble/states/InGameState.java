package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;
import com.progark.group2.wizardrumble.entities.Entity;
import com.progark.group2.wizardrumble.entities.Wizard;

import static com.badlogic.gdx.Input.Keys;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameState extends State {

    private Wizard wizard;
    private Texture wizardSprite;
    private TextureRegion region;

    private SpriteBatch sb;
    private MovementInput1 leftJoyStick;
    private AimInput1 rightJoyStick;
    private Stage stage;


    public InGameState(GameStateManager gameStateManager) {
        super(gameStateManager);
        wizard = new Wizard(new Vector2(200, 200));
        wizardSprite = new Texture("wizard.jpg");
        region = new TextureRegion(wizardSprite);

        sb = new SpriteBatch();
        stage = new Stage();
        leftJoyStick = new MovementInput1(15, 15);
        rightJoyStick = new AimInput1(WIDTH-15- AimInput1.diameter, 15);

        Gdx.input.setInputProcessor(stage);
        stage = new Stage(new ScreenViewport(), sb);
        stage.addActor(leftJoyStick);
        stage.addActor(rightJoyStick);
        Gdx.input.setInputProcessor(stage);
    }

    private void updateWizardRotation(){
        wizard.updateRotation(
                rightJoyStick.isTouched() ? // Terniary
                        new Vector2(rightJoyStick.getKnobPercentX(),rightJoyStick.getKnobPercentY()) :
                        new Vector2(leftJoyStick.getKnobPercentX(),leftJoyStick.getKnobPercentY())
        );
    }

    private void updateWizardPosition(){
        //GetKnobPercentX and -Y returns cos and sin values of the touchpad in question
        Vector2 leftJoyPosition = new Vector2(leftJoyStick.getKnobPercentX(),leftJoyStick.getKnobPercentY());
        wizard.updatePosition(leftJoyPosition);
    }

    @Override
    public void update(float dt) {
        // Let the player rotate primarily using the right stick, but use left stick if right stick input is absent.
        if (leftJoyStick.isTouched()){
            updateWizardPosition();
            updateWizardRotation();
        }
        if (rightJoyStick.isTouched()){
            wizard.updateRotation(new Vector2(rightJoyStick.getKnobPercentX(),rightJoyStick.getKnobPercentY()));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(region, wizard.getPosition().x,wizard.getPosition().y,
                wizardSprite.getWidth()/(float)2,
                wizardSprite.getHeight()/(float)2,
                wizardSprite.getWidth(), wizardSprite.getHeight(),
                1,1, wizard.getRotation());
        sb.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }
}
