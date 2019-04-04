package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.entities.WizardPlayer;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;
import com.progark.group2.wizardrumble.entities.spells.Spell;
import com.progark.group2.wizardrumble.entities.spells.FireBall;
import com.progark.group2.wizardrumble.listeners.WorldContactListener;
import com.progark.group2.wizardrumble.tools.B2WorldCreator;

import java.util.ArrayList;
import java.util.List;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameState extends State {

    private Wizard wizard;
    private TextureRegion region;

    private MovementInput1 leftJoyStick;
    private AimInput1 rightJoyStick;
    private Stage stage;

    // Used for testing spells
    private List<Spell> spells;
    private String selectedSpell;
    private long cooldown;
    private long lastattack;

    private OrthographicCamera camera;
    private MapHandler mapHandler;

    //Box2d variables
    public final static World world = new World(new Vector2(0,0), true);
    private Box2DDebugRenderer b2dr;
    private Array<Body> bodiesToDestroy;

    private static InGameState instance = null;

    // Last touch boolean for when rightJoyStick was touched last frame.
    // For when you release the right joystick, the spell should fire, instead of on justTouched().
    private boolean lastTouch;
    private float lastAimX;
    private float lastAimY;


    private InGameState(GameStateManager gameStateManager) {
        super(gameStateManager);

        camera = new OrthographicCamera();
        Viewport gamePort = new FitViewport(WIDTH, HEIGHT, camera);

        //Box2d
        b2dr = new Box2DDebugRenderer();

        mapHandler = new MapHandler();

        //Startposition must be changed. It is only like this while the user input moves.
        //wizard = new WizardPlayer(new Vector2(WIDTH / 2f, HEIGHT / 2f + (32 * 4)));
        wizard = new WizardPlayer(new Vector2(1450,1500));
        region = new TextureRegion(wizard.getSprite());

        SpriteBatch sb = new SpriteBatch();
        stage = new Stage();
        leftJoyStick = new MovementInput1(15, 15);
        rightJoyStick = new AimInput1(WIDTH-15- AimInput1.diameter, 15);

        Gdx.input.setInputProcessor(stage);
        stage = new Stage(gamePort, sb);
        stage.addActor(leftJoyStick);
        stage.addActor(rightJoyStick);
        Gdx.input.setInputProcessor(stage);


        new B2WorldCreator(mapHandler.getMap());



        // Set camera to initial wizard position
        camera.position.set(wizard.getPosition().x + wizard.getSprite().getWidth()/2f, wizard.getPosition().y + wizard.getSprite().getHeight()/2f, 0);

        // Used for testing spells.
        spells = new ArrayList<Spell>();
        lastTouch = false;
        cooldown = 2000;
        lastattack = -2000; // To allow spellcasting at the start of the game.

        // Adds a world contact listener
        world.setContactListener(new WorldContactListener());

        bodiesToDestroy = new Array<Body>();

    }

    public static InGameState getInstance(){
        if (instance == null){
            instance = new InGameState(GameStateManager.getInstance());
        }
        return instance;
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

    private void castSpell(String selectedSpell) {
        // Computes x and y from right joystick input making the speed the same regardless of
        // where on the joystick you touch.
        float angleX1 = lastAimX;
        float angleY1 = lastAimY;
        float hypotenuse = (float) Math.sqrt(angleX1 * angleX1 + angleY1 * angleY1);
        float ratio = (float) Math.sqrt(2) / hypotenuse;
        float angleX = angleX1 * ratio;
        float angleY = angleY1 * ratio;


        FireBall fb = new FireBall( // spawnPoint, rotation, velocity
                getSpellInitialPosition(wizard.getPosition(), wizard.getSize(), FireBall.texture.getHeight(), FireBall.texture.getWidth(),angleX,angleY),
                wizard.getRotation(), // rotation
                new Vector2(angleX, angleY)  // velocity
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
        for (Spell spell : spells){
            if(spell.getB2body().equals(null)){
                System.out.println("Spell isn't properly deleted");
            }
        }
        delete();
        world.step(1/60f, 6, 2);

        mapHandler.setView(camera);

        // Let the player rotate primarily using the right stick, but use left stick if right stick input is absent.
        updateWizardPosition();
        if (leftJoyStick.isTouched()){
            updateWizardRotation();
            //Update camera to follow player. If we move player sprite to player, we have to fix this method.


            updateCamera(wizard.getPosition().x + wizard.getSprite().getWidth() / 2f, wizard.getPosition().y + wizard.getSprite().getHeight() / 2f);

        }
        if (rightJoyStick.isTouched()){
            wizard.updateRotation(new Vector2(rightJoyStick.getKnobPercentX(),rightJoyStick.getKnobPercentY()));
        }

        // Probably temporary code. Written to test functionality.
        if (lastTouch && !rightJoyStick.isTouched()){
        // if (rightJoyStick.isTouched()){
            // I think that spells should be cast when the player releases the right joystick, so that you can
            // see the rotation of the player character and not rely on hopefully having touched the joystick correctly
            long time = System.currentTimeMillis();
            //System.out.println(time);
            if (time > lastattack + cooldown){
                castSpell("fireball");
                lastattack = time;
                //System.out.println(lastattack);
            }
        }
        lastTouch = rightJoyStick.isTouched();
        lastAimX = rightJoyStick.getKnobPercentX();
        lastAimY = rightJoyStick.getKnobPercentY();

        // Jank solution that takes in Wizards position and offsets by half screen size etc.
        // TODO Bind joysticks position to actual screen (suspect something with viewPort and/or stage)

        leftJoyStick.updatePosition(wizard.getPosition().x - WIDTH/2f + 15, wizard.getPosition().y  - HEIGHT/2f + 15);
        rightJoyStick.updatePosition(wizard.getPosition().x + WIDTH/2f + 35 - AimInput1.diameter, wizard.getPosition().y  - HEIGHT/2f + 15);

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

        // box2d debug renderer
        // Renders visible boxes around all collidable objects.
        b2dr.render(world, camera.combined);

        sb.begin();

        sb.draw(region, wizard.getPosition().x,wizard.getPosition().y,
                wizard.getSprite().getWidth()/2f,
                wizard.getSprite().getHeight()/2f,
                wizard.getSprite().getWidth(), wizard.getSprite().getHeight(),
                1,1, wizard.getRotation());

        // Iterate spells to render
        for (Spell spell : spells){
            spell.render(sb);
        }

        sb.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    public void addToBodyList(Body body){
        if(!bodiesToDestroy.contains(body,true)){
            bodiesToDestroy.add(body);
        }
        //bodiesToDestroy.add(body);
        System.out.println(bodiesToDestroy);
    }

    public void removeSpell(Spell spell){
        spells.remove(spell);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }

    private void delete(){
        for (Body body : bodiesToDestroy) {
            world.destroyBody(body);
        }
        bodiesToDestroy.clear();
    }

    private Vector2 getSpellInitialPosition(Vector2 wizpos, Vector2 wizSize, int spellHeight, int spellWidth, float angleX, float angleY){
        // To not run into the spell.
        int offset = 40; // TODO Tweak this to align with spell size.
        return new Vector2(wizpos.x + angleX*offset + spellWidth/2f*angleX - wizSize.x/2f,
                           wizpos.y + angleY*offset + spellHeight/2f*angleY - wizSize.y/2f);
    }

    private float getRotation(int indexOfArea){
        float startRotation = 22.5f;
        float rotationInterval = 45f;

        return startRotation + indexOfArea * rotationInterval + wizard.getOffset();

    }

}
