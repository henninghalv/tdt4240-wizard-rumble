package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.progark.group2.wizardrumble.entities.Spell;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;
import com.progark.group2.wizardrumble.controllers.SpellSelector1;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.entities.spells.FireBall;

import java.util.ArrayList;
import java.util.List;


import static com.badlogic.gdx.Input.Keys;
import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameState extends State {

    private Wizard wizard;
    private Texture wizardSprite;
    private TextureRegion region;

    private SpriteBatch sb;
    private MovementInput1 leftJoyStick;
    private AimInput1 rightJoyStick;
    private SpellSelector1 spellButtons;
    private TextButton button1, button2, button3;
    private Stage stage;

    // Used for testing spells
    private List<Spell> spells;

    private OrthographicCamera camera;
    private Viewport gamePort;
    private MapHandler mapHandler;


    public InGameState(GameStateManager gameStateManager) {
        super(gameStateManager);

        camera = new OrthographicCamera();
        gamePort = new FitViewport(WIDTH, HEIGHT, camera);

        wizard = new Wizard(new Vector2(WIDTH/2f, HEIGHT/2f));
        wizardSprite = new Texture("wizard_liten.jpg");
        region = new TextureRegion(wizardSprite);

        sb = new SpriteBatch();
        stage = new Stage();
        leftJoyStick = new MovementInput1(15, 15);
        rightJoyStick = new AimInput1(WIDTH-15- AimInput1.diameter, 15);

        spellButtons = new SpellSelector1();
        button1 = spellButtons.createSpellButton("Fire", WIDTH- AimInput1.diameter-50, 150);
        button2 = spellButtons.createSpellButton("Ice", WIDTH- AimInput1.diameter-80, 100);
        button3 = spellButtons.createSpellButton("Blast", WIDTH- AimInput1.diameter-60, 50);

        Gdx.input.setInputProcessor(stage);
        stage = new Stage(gamePort, sb);
        stage.addActor(leftJoyStick);
        stage.addActor(rightJoyStick);
        stage.addActor(button1);
        stage.addActor(button2);
        stage.addActor(button3);

        Gdx.input.setInputProcessor(stage);

        // Used for testing spells.
        spells = new ArrayList<Spell>();

        mapHandler = new MapHandler();

        camera.position.set(wizard.getPosition().x + wizardSprite.getWidth()/2f, wizard.getPosition().y + wizardSprite.getHeight()/2f, 0);
    }


    private void updateWizardRotation(){
        wizard.updateRotation(
                rightJoyStick.isTouched() ? // Terniary
                        new Vector2(rightJoyStick.getKnobPercentX(),rightJoyStick.getKnobPercentY()) :
                        new Vector2(leftJoyStick.getKnobPercentX(),leftJoyStick.getKnobPercentY())
        );
    }

    @Override
    public void handleInput() {

    }

    private void updateWizardPosition(){
        //GetKnobPercentX and -Y returns cos and sin values of the touchpad in question
        Vector2 leftJoyPosition = new Vector2(leftJoyStick.getKnobPercentX(),leftJoyStick.getKnobPercentY());
        wizard.updatePosition(leftJoyPosition);
    }

    private void castSpell() {
        // Computes x and y from right joystick input making the speed the same regardless of
        // where on the joystick you touch.
        float x1 = rightJoyStick.getKnobPercentX();
        float y1 = rightJoyStick.getKnobPercentY();
        float hypotenuse = (float) Math.sqrt(x1 * x1 + y1 * y1);
        float ratio = (float) Math.sqrt(2) / hypotenuse;
        float x = x1 * ratio;
        float y = y1 * ratio;

        FireBall fb = new FireBall( // spawnPoint, rotation, velocity
                new Vector2(
                        // TODO: offsetting spell position by screen width and height is not desirable, but it works for now.
                        // Need to further look into how spell position or rendering is decided.
                        wizard.getPosition().x - (WIDTH + wizardSprite.getWidth())/2f ,
                        wizard.getPosition().y - (HEIGHT + wizardSprite.getHeight())/2f
                        ),
                wizard.getRotation(), // rotation
                new Vector2(x, y)  // velocity
        );
        spells.add(fb); // THIS IS ONLY FOR FIREBALL AT THE MOMENT
    }

    private void updateCamera(float x, float y){
        camera.position.x = x;
        camera.position.y = y;
        camera.update();
    }

    @Override
    public void update(float dt) {
        // Let the player rotate primarily using the right stick, but use left stick if right stick input is absent.
        mapHandler.setView(camera);
        if (leftJoyStick.isTouched()){
            updateWizardPosition();
            updateWizardRotation();
            //Update camera to follow player. If we move player sprite to player, we have to fix this method.
            updateCamera(wizard.getPosition().x + wizardSprite.getWidth() / 2f, wizard.getPosition().y + wizardSprite.getHeight() / 2f);
            System.out.println(wizard.getPosition());
        }
        if (rightJoyStick.isTouched()){
            wizard.updateRotation(new Vector2(rightJoyStick.getKnobPercentX(),rightJoyStick.getKnobPercentY()));
        }

        // Probably temporary code. Written to test functionality.
        if (Gdx.input.justTouched()){
            // I think that spells should be cast when the player releases the right
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
        //Combines camera's coordinate system with world coordinate system.
        sb.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapHandler.render();
        sb.begin();
        sb.draw(region, wizard.getPosition().x, wizard.getPosition().y,
                wizardSprite.getWidth()/2f,
                wizardSprite.getHeight()/2f,
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
