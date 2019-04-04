package com.progark.group2.wizardrumble.states.InGameState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.entities.WizardEnemy;
import com.progark.group2.wizardrumble.entities.WizardPlayer;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import com.progark.group2.wizardrumble.entities.spells.FireBall;
import com.progark.group2.wizardrumble.entities.spells.Ice;
import com.progark.group2.wizardrumble.entities.spells.Spell;
import com.progark.group2.wizardrumble.listeners.WorldContactListener;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.network.resources.Player;
import com.progark.group2.wizardrumble.states.GameStateManager;
import com.progark.group2.wizardrumble.states.InGameMenuState;
import com.progark.group2.wizardrumble.states.State;
import com.progark.group2.wizardrumble.tools.B2WorldCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameState extends State {

    private NetworkController network;

    private WizardPlayer wizardPlayer;
    private HashMap<Integer, WizardEnemy> wizardEnemies = new HashMap<Integer, WizardEnemy>();
    private TextureRegion wizardPlayerRegion;
    private HashMap<Integer, TextureRegion> wizardEnemyRegions = new HashMap<Integer, TextureRegion>();

    // Currently selected spell
    private String activeSpell;

    private long cooldown;
    private long lastattack;

    // Spells that have been cast
    private List<Spell> spells;

    private OrthographicCamera camera;
    private Viewport gamePort;
    private MapHandler mapHandler;
    private SpriteBatch spriteBatch;

    // Box2d variables
    public final static World world = new World(new Vector2(0, 0), true);
    private Box2DDebugRenderer b2dr;
    private Array<Body> bodiesToDestroy;


    private static InGameState instance = null;
    private InGameHud inGameHud;

    // Last touch boolean for when rightJoyStick was touched last frame.
    // For when you release the right joystick, the spell should fire, instead of on justTouched().
    private boolean lastTouch;
    private float lastAimX;
    private float lastAimY;


    private InGameState(GameStateManager gameStateManager) throws IOException {
        super(gameStateManager);

        // Create camera
        camera = new OrthographicCamera();
        gamePort = new FitViewport(WIDTH, HEIGHT, camera);


        // Start with no active spell
        activeSpell = "";

        // Start with no casted spells
        spells = new ArrayList<Spell>();

        // Start without any buttons having been touched
        lastTouch = false;


        // Get the Network Controller
        network = NetworkController.getInstance();
        spriteBatch = new SpriteBatch();
        inGameHud = new InGameHud(spriteBatch);
        Gdx.input.setInputProcessor(inGameHud.getStage());


        // Create the stage
        stage = new Stage(gamePort, spriteBatch);

        // Setup Box2d and Map
        b2dr = new Box2DDebugRenderer();
        mapHandler = new MapHandler();

        // Creating a WizardPlayer object
        wizardPlayer = new WizardPlayer(
                network.getPlayer().getPosition()  // TODO: Give some other spawn point
                );


        // Add a new wizardPlayerRegion around Wizard
        wizardPlayerRegion = new TextureRegion(wizardPlayer.getSprite());

        // Creating all enemy players
        for (Player player : network.getPlayers().values()){
            WizardEnemy enemy = new WizardEnemy(
                    Wizard.DEFAULT_HEALTH,
                    player.getPosition() // TODO: Give some other spawn point
            );
            wizardEnemies.put(player.getConnectionId(), enemy);
            TextureRegion enemyRegion = new TextureRegion(enemy.getSprite());
            wizardEnemyRegions.put(player.getConnectionId(), enemyRegion);
        }


        // Creates map with all elements
        new B2WorldCreator(mapHandler.getMap());

        // Adds a world contact listener
        world.setContactListener(new WorldContactListener());

        lastTouch = false;
        cooldown = 2000;
        lastattack = -2000; // To allow spellcasting at the start of the game.

        bodiesToDestroy = new Array<Body>();

        // Set camera to initial wizardPlayer position
        camera.position.set(wizardPlayer.getPosition().x + wizardPlayer.getSprite().getWidth()/2f, wizardPlayer.getPosition().y + wizardPlayer.getSprite().getHeight()/2f, 0);

     }

    public static InGameState getInstance() throws IOException {
        if (instance == null){
            instance = new InGameState(GameStateManager.getInstance());
        }
        return instance;
    }


    private void updateWizardRotation(){
        wizardPlayer.updateRotation(
                inGameHud.getRightJoyStick().isTouched() ?
                        new Vector2(inGameHud.getRightJoyStick().getKnobPercentX(), inGameHud.getRightJoyStick().getKnobPercentY()) :
                        new Vector2(inGameHud.getLeftJoyStick().getKnobPercentX(), inGameHud.getLeftJoyStick().getKnobPercentY())
        );
    }


    private void updateWizardPosition() {
        //GetKnobPercentX and -Y returns cos and sin values of the touchpad in question
        Vector2 leftJoyPosition = new Vector2(inGameHud.getLeftJoyStick().getKnobPercentX(), inGameHud.getLeftJoyStick().getKnobPercentY());
        wizardPlayer.updatePosition(leftJoyPosition);
    }

    private void castSpell(String spell) {
        // Computes x and y from right joystick input making the speed the same regardless of
        // where on the joystick you touch.
        float x1 = lastAimX;
        float y1 = lastAimY;
        float hypotenuse = (float) Math.sqrt(x1 * x1 + y1 * y1);
        float ratio = (float) Math.sqrt(2) / hypotenuse;
        float x = x1 * ratio;
        float y = y1 * ratio;

        // Logic for casting when FireBall has been selected
        if (spell.equals("FireBall")) {
            FireBall fb = new FireBall( // spawnPoint, rotation, velocity
                            // spawnPoint, rotation, velocity
                            getSpellInitialPosition(wizardPlayer.getPosition(), wizardPlayer.getSize(), FireBall.texture.getHeight(), FireBall.texture.getWidth(),x,y),
                            wizardPlayer.getRotation(), // rotation
                            new Vector2(x, y)  // velocity
            );
            spells.add(fb); // Add to list of casted spells
        }

        // Logic for casting when Ice has been selected
        if (spell.equals("Ice")) {
            Ice ic = new Ice( // spawnPoint, rotation, velocity
                    getSpellInitialPosition(wizardPlayer.getPosition(), wizardPlayer.getSize(), Ice.texture.getHeight(), Ice.texture.getWidth(),x,y),
                    wizardPlayer.getRotation(), // rotation
                    new Vector2(x, y)  // velocity
            );
            spells.add(ic); // Add to list of casted spells
        }
    }

    private void updateCamera(float x, float y) {
        camera.position.x = x;
        camera.position.y = y;
        camera.update();
    }

    public InGameHud getInGameHud(){
        return inGameHud;
    }

    @Override
    public void update(float dt) {

        // Debugging method to let us know if at any point a spell still exists after it's body is destroyed.
        for (Spell spell : spells){
            if(spell.getB2body() == (null)){
                System.out.println("Spell isn't properly deleted");
            }
        }

        // Deletes bodies after collision
        delete();

        world.step(1 / 60f, 6, 2);

        mapHandler.setView(camera);

        // Let the player rotate primarily using the right stick, but use left stick if right stick input is absent.
        updateWizardPosition();

        if (inGameHud.getLeftJoyStick().isTouched()){
            updateWizardRotation();
            //Update camera to follow player. If we move player sprite to player, we have to fix this method.
            updateCamera(wizardPlayer.getPosition().x + wizardPlayer.getSprite().getWidth() / 2f, wizardPlayer.getPosition().y + wizardPlayer.getSprite().getHeight() / 2f);
            // Sends the updated position to the server
            network.updatePlayerPosition(wizardPlayer.getPosition(), wizardPlayer.getRotation());
        }

        if (inGameHud.getRightJoyStick().isTouched()){
            wizardPlayer.updateRotation(new Vector2(inGameHud.getRightJoyStick().getKnobPercentX(), inGameHud.getRightJoyStick().getKnobPercentY()));
            network.updatePlayerPosition(wizardPlayer.getPosition(), wizardPlayer.getRotation());
        }

        // Probably temporary code. Written to test functionality.
        if (lastTouch && !inGameHud.getRightJoyStick().isTouched()){
        // if (inGameHud.getRightJoyStick().isTouched()){
            // I think that spells should be cast when the player releases the right joystick, so that you can
            // see the rotation of the player character and not rely on hopefully having touched the joystick correctly

            long time = System.currentTimeMillis();

            if (time > lastattack + cooldown){
                for (String spell : inGameHud.getSpellNames()) {
                    if (spell.equals(inGameHud.getSpellSelector().getSpellSelected())) {
                        activeSpell = spell;
                        break;
                    }
                }
                castSpell(activeSpell);
                lastattack = time;
                //System.out.println(lastattack);
            }

        }

        lastTouch = inGameHud.getRightJoyStick().isTouched();
        lastAimX = inGameHud.getRightJoyStick().getKnobPercentX();
        lastAimY = inGameHud.getRightJoyStick().getKnobPercentY();

        // Iterate spells to update
        for (Spell spell : spells) {
            spell.update();
        }

        // Keep the Wizard object in sync with Player object
        for (Player player : network.getPlayers().values()) {
            wizardEnemies.get(player.getConnectionId()).setPosition(player.getPosition());
            wizardEnemies.get(player.getConnectionId()).setRotation(player.getRotation());
            wizardEnemies.get(player.getConnectionId()).updateBodyPosition(player.getPosition());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.onBackButtonPress();
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        //Combines camera's coordinate system with world coordinate system.
        spriteBatch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapHandler.render();
        spriteBatch.begin();

        spriteBatch.draw(wizardPlayerRegion, wizardPlayer.getPosition().x, wizardPlayer.getPosition().y,
                wizardPlayer.getSprite().getWidth() / 2f,
                wizardPlayer.getSprite().getHeight() / 2f,
                wizardPlayer.getSprite().getWidth(), wizardPlayer.getSprite().getHeight(),
                1, 1, wizardPlayer.getRotation());

        // Iterate spells to render
        for (Spell spell : spells) {
            spell.render(spriteBatch);
        }

        for (Integer wizardId : wizardEnemies.keySet()) {
            WizardEnemy enemy = wizardEnemies.get(wizardId);
            TextureRegion enemyRegion = wizardEnemyRegions.get(wizardId);
            spriteBatch.draw(
                    enemyRegion, enemy.getPosition().x, enemy.getPosition().y,
                    enemy.getSprite().getWidth() / 2f,
                    enemy.getSprite().getHeight() / 2f,
                    enemy.getSprite().getWidth(), enemy.getSprite().getHeight(),
                    1, 1, enemy.getRotation()
            );
        }

        spriteBatch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        // box2d debug renderer
        // Renders visible boxes around all collidable objects.
        b2dr.render(world, camera.combined);
        // Draws the game stage
        stage.draw();
        //Secondly draw the Hud
        spriteBatch.setProjectionMatrix(inGameHud.getStage().getCamera().combined);
        inGameHud.getStage().draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleInput() {

    }

    public void addToBodyList(Body body){
        if(!bodiesToDestroy.contains(body,true)){
            bodiesToDestroy.add(body);
        }
        //bodiesToDestroy.add(body);
        //System.out.println(bodiesToDestroy);
    }

    public void removeSpell(Spell spell){
        spells.remove(spell);
    }

    private void delete(){
        for (Body body : bodiesToDestroy) {
            world.destroyBody(body);
        }
        bodiesToDestroy.clear();
    }

    @Override
    public void onBackButtonPress() {
        this.gameStateManager.push(new InGameMenuState(gameStateManager));
    }

    public Vector3 getCamPosition(){
        return camera.position;
    }

    public WizardPlayer getWizardPlayer(){ return wizardPlayer; }

    @Override
    public void activate(){
        super.activate();
        Gdx.input.setInputProcessor(inGameHud.getStage());
    }

    private Vector2 getSpellInitialPosition(Vector2 wizpos, Vector2 wizSize, int spellHeight, int spellWidth, float angleX, float angleY){
        // To not run into the spell.
        int offset = 40; // TODO Tweak this to align with spell size.
        return new Vector2(wizpos.x + angleX*offset + spellWidth/2f*angleX - wizSize.x/2f,
                wizpos.y + angleY*offset + spellHeight/2f*angleY - wizSize.y/2f);
    }
}
