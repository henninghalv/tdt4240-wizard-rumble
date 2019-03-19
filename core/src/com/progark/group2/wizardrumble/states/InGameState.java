package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.progark.group2.wizardrumble.entities.Spell;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;
import com.progark.group2.wizardrumble.entities.Entity;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.entities.spells.FireBall;

import java.util.ArrayList;
import java.util.List;

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

    // Used for testing spells
    private List<Spell> spells;


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

        // Used for testing spells.
        spells = new ArrayList<Spell>();

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

    private void castSpell(){
        // Computes x and y from right joystick input making the speed the same regardless of
        // where on the joystick you touch.
        float x1 = rightJoyStick.getKnobPercentX();
        float y1 = rightJoyStick.getKnobPercentY();
        float hypotenuse = (float) Math.sqrt(x1*x1 + y1*y1);
        float ratio = (float) Math.sqrt(2)/hypotenuse;
        float x = x1*ratio;
        float y = y1*ratio;

        FireBall fb = new FireBall( // spawnPoint, rotation, velocity
                new Vector2(
                        wizard.getPosition().x - wizardSprite.getWidth()/(float)2,
                        wizard.getPosition().y - wizardSprite.getHeight()/(float)2
                ),
                wizard.getRotation(), // rotation
                new Vector2(x, y)  // velocity
        );
        spells.add(fb); // THIS IS ONLY FOR FIREBALL AT THE MOMENT
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

        // Probably temporary code. Written to test functionality.
        if (Gdx.input.justTouched()){
            if (rightJoyStick.isTouched()){
                castSpell();
            }
        }
        // Iterate spells to update
        for (Spell spell : spells){
            spell.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(region, wizard.getPosition().x,wizard.getPosition().y,
                wizardSprite.getWidth()/(float)2,
                //wizardSprite.getWidth()/(float)2,
                wizardSprite.getHeight()/(float)2,
                wizardSprite.getWidth(), wizardSprite.getHeight(),
                1,1, wizard.getRotation());
        // Iterate spells to render
        for (Spell spell : spells){
            spell.render(sb);
        }
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
