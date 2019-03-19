package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.progark.group2.wizardrumble.Application;
import com.progark.group2.wizardrumble.controllers.JoyStick;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameState extends State {

    private Wizard wizard;
    private Texture wizardSprite;
    private TextureRegion region;

    private SpriteBatch sb;
    private JoyStick leftJoyStick;
    private JoyStick rightJoyStick;
    private Stage stage;

    private OrthographicCamera camera;
    private Viewport gamePort;
    private MapHandler mapHandler;


    public InGameState(GameStateManager gameStateManager) {
        super(gameStateManager);

        camera = new OrthographicCamera();
        gamePort = new FitViewport(Application.WIDTH, Application.HEIGHT, camera);

        wizard = new Wizard(new Vector2(Application.WIDTH/2, Application.HEIGHT/2));
        wizardSprite = new Texture("wizard_liten.jpg");
        region = new TextureRegion(wizardSprite);

        sb = new SpriteBatch();
        stage = new Stage();
        leftJoyStick = new JoyStick(15, 15);
        rightJoyStick = new JoyStick(WIDTH-15-JoyStick.diameter, 15);
        Gdx.input.setInputProcessor(stage);
        stage = new Stage(gamePort, sb);
        stage.addActor(leftJoyStick);
        stage.addActor(rightJoyStick);
        Gdx.input.setInputProcessor(stage);

        mapHandler = new MapHandler();

        camera.position.set(wizard.getPosition().x + wizardSprite.getWidth()/2, wizard.getPosition().y + wizardSprite.getHeight()/2, 0);
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
            updateCamera(wizard.getPosition().x + wizardSprite.getWidth() / 2, wizard.getPosition().y + wizardSprite.getHeight() / 2);
        }
        if (rightJoyStick.isTouched()){
            wizard.updateRotation(new Vector2(rightJoyStick.getKnobPercentX(),rightJoyStick.getKnobPercentY()));
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
