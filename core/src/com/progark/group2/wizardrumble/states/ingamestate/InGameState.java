package com.progark.group2.wizardrumble.states.ingamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.minlog.Log;
import com.progark.group2.wizardrumble.controllers.TouchPadAimInput1;
import com.progark.group2.wizardrumble.controllers.TouchPadMovementInput1;
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
import com.progark.group2.wizardrumble.tools.SoundManager;
import com.progark.group2.wizardrumble.tools.SoundType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.SCALE;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameState extends State {

    private NetworkController network;
    private SoundManager soundManager;

    private WizardPlayer wizardPlayer;
    private HashMap<Integer, WizardEnemy> wizardEnemies;
    private TextureRegion wizardPlayerRegion;
    private HashMap<Integer, TextureRegion> wizardEnemyRegions;

    // Currently selected spell
    private String activeSpell;
    private ArrayList<Long> lastattacks;

    // Spells that have been cast
    private ArrayList<Spell> spells;

    private OrthographicCamera camera;
    private Viewport gamePort;
    private MapHandler mapHandler;
    private SpriteBatch spriteBatch;

    // Box2d variables
    public static World world;
    private Box2DDebugRenderer b2dr;
    private Array<Body> bodiesToDestroy;


//    private static InGameState instance = null;
    private InGameHud inGameHud;

    // Last touch boolean for when rightJoyStick was touched last frame.
    // For when you release the right joystick, the spell should fire, instead of on justTouched().
    private boolean lastTouch;
    private Vector2 lastAim;


    public InGameState(GameStateManager gameStateManager) throws IOException {
        super(gameStateManager);

        wizardEnemies = new HashMap<Integer, WizardEnemy>();
        wizardEnemyRegions = new HashMap<Integer, TextureRegion>();
        spells = new ArrayList<Spell>();
        bodiesToDestroy = new Array<Body>();

        // Get the Network Controller
        network = NetworkController.getInstance();
        // Get the Sound Manager
        soundManager = SoundManager.getInstance();
        soundManager.switchMusic("battle");

        // Setting up the state
        setupCameraAndWorld();
        setupStageAndMap();
        setupSpells();
        setupPlayer();
        setupEnemies();
        updatePositions();



        // Set camera to initial wizardPlayer position
        camera.position.set((wizardPlayer.getPosition().x + wizardPlayer.getPlayerSprite().getWidth()/2f)*SCALE, (wizardPlayer.getPosition().y + wizardPlayer.getPlayerSprite().getHeight()/2f)*SCALE, 0);

    }

    // SETUP METHODS =====

    private void setupCameraAndWorld(){
        // Creating a new world
        world  = new World(new Vector2(0, 0), true);

        // Adds a world contact listener
        world.setContactListener(new WorldContactListener());

        // Create camera
        camera = new OrthographicCamera();

        // To allow for higher wizard and spell speeds, we scale everything down
        camera.zoom = 1.0f * SCALE;

        // Creating a viewport(the game view)
        gamePort = new FitViewport(WIDTH, HEIGHT, camera);
    }

    private void setupStageAndMap(){
        spriteBatch = new SpriteBatch();
        inGameHud = new InGameHud(spriteBatch);

        // Create the stage
        stage = new Stage(gamePort, spriteBatch);

        // Setup Box2d and Map
        b2dr = new Box2DDebugRenderer();
        mapHandler = new MapHandler(spriteBatch);

        // Creates map with all elements
        new B2WorldCreator(mapHandler.getMap());

    }

    private void setupSpells(){
        lastattacks = new ArrayList<Long>();
        // Start with fireball as active spell
        activeSpell = "FireBall";

        // This is used to cast spells when right joystick is released.
        // Starts as not being touched, obviously.
        lastTouch = false;

        lastattacks.add(-(getCooldown("FireBall")));
        lastattacks.add(-(getCooldown("Ice")));

    }

    private void setupPlayer(){
        // Creating a WizardPlayer object
        wizardPlayer = new WizardPlayer(network.getPlayer().getPosition(), network.getPlayer().getPlayerSlotId());
        // Add a new wizardPlayerRegion around Wizard
        wizardPlayerRegion = new TextureRegion(wizardPlayer.getPlayerSprite());
        addPlayerToMapLayers(wizardPlayer);
    }

    private void setupEnemies(){
        // Creating all enemy players
        for (Player player : network.getPlayers().values()){
            WizardEnemy enemy = new WizardEnemy(player.getPosition(), player.getPlayerSlotId());
            wizardEnemies.put(player.getPlayerSlotId(), enemy);
            TextureRegion enemyRegion = new TextureRegion(enemy.getPlayerSprite());
            wizardEnemyRegions.put(player.getPlayerSlotId(), enemyRegion);
            addPlayerToMapLayers(enemy);
        }
    }

    private void updatePositions(){
        updateWizardRotation();
        updateWizardPosition();
        network.updatePlayerPosition(wizardPlayer.getPosition(), wizardPlayer.getRotation());
    }

    private void addPlayerToMapLayers(Wizard wizard){
        MapObject object = new MapObject();
        object.getProperties().put("player", wizard);
        mapHandler.getMap().getLayers().get("players").getObjects().add(object);
    }

    // ==========

    // MOVEMENT LOGIC =====

    private void updateWizardPositionAndRotation(){
        // Let the player rotate primarily using the right stick, but use left stick if right stick input is absent.
        updateWizardPosition();

        if (inGameHud.getLeftJoyStick().isTouched()){
            updateWizardRotation();
            // Sends the updated position to the server
            network.updatePlayerPosition(wizardPlayer.getPosition(), wizardPlayer.getRotation());
        }

        if (inGameHud.getRightJoyStick().isTouched()){
            wizardPlayer.updateRotation(TouchPadAimInput1.getInstance().getAimDirection());
            // Sends the updated position to the server
            network.updatePlayerPosition(wizardPlayer.getPosition(), wizardPlayer.getRotation());
        }
    }

    private void updateWizardRotation(){
        wizardPlayer.updateRotation(
                inGameHud.getRightJoyStick().isTouched() ?
                        TouchPadAimInput1.getInstance().getAimDirection() :
                        TouchPadMovementInput1.getInstance().getMovementDirection()
        );
    }

    private void updateWizardPosition() {
        wizardPlayer.updatePosition(TouchPadMovementInput1.getInstance().getMovementDirection());
    }

    private void updateCamera() {
        camera.position.x = wizardPlayer.getPosition().x + wizardPlayer.getPlayerSprite().getWidth() / 2f;
        camera.position.y = wizardPlayer.getPosition().y + wizardPlayer.getPlayerSprite().getHeight() / 2f;
        camera.update();
    }

    // =========

    // SPELL LOGIC =====

    private void castSpell(String spell) {
        // Computes x and y from right joystick input making the speed the same regardless of
        // where on the joystick you touch.
        float x1 = lastAim.x;
        float y1 = lastAim.y;
        float hypotenuse = (float) Math.sqrt(x1 * x1 + y1 * y1);
        float ratio = (float) Math.sqrt(2) / hypotenuse;
        float x = x1 * ratio;
        float y = y1 * ratio;

        Vector2 spawnPoint = getSpellInitialPosition(wizardPlayer.getPosition(), wizardPlayer.getSize(), FireBall.texture.getHeight(), FireBall.texture.getWidth(),x,y);

        float rotation = wizardPlayer.getRotation(); // rotation

        Vector2 velocity = new Vector2(x, y);  // velocity

        // Logic for casting when FireBall has been selected
        if (spell.equals("FireBall")) {
            FireBall fb = new FireBall(network.getPlayerId(), spawnPoint, rotation, velocity);
            fb.cast(spells, network);
            inGameHud.getSpellSelector().disableButton("FireBall");
            fb.playSound(1.0f);
            SoundManager.getInstance().playSound(SoundType.FIRECAST, 1.0f);
        }

        // Logic for casting when Ice has been selected
        if (spell.equals("Ice")) {
            Vector2 pivot = new Vector2(wizardPlayer.getPosition().x + wizardPlayer.getSize().x/2f, wizardPlayer.getPosition().y + wizardPlayer.getSize().y/2f);
            Ice icicles = new Ice(network.getPlayerId(), spawnPoint, rotation, velocity, pivot);
            icicles.cast(spells, network);
            inGameHud.getSpellSelector().disableButton("Ice");
            icicles.playSound(1.0f);
            SoundManager.getInstance().playSound(SoundType.ICECAST, 1.0f);
        }

    }

    private Vector2 getSpellInitialPosition(Vector2 wizpos, Vector2 wizSize, int spellHeight, int spellWidth, float angleX, float angleY){
        // To not run into the spell.
        float offset = 25*SCALE; // TODO Tweak this to align with spell size.
        return new Vector2(wizpos.x + wizSize.x/2f + angleX*offset + spellWidth/2f*angleX*SCALE,
                wizpos.y + wizSize.y/2f + angleY*offset + spellHeight/2f*angleY*SCALE);
    }

    public void addSpell(Spell spell){
        spell.playSound(0.5f);
        spells.add(spell);
    }

    public void removeSpell(Spell spell){
        spells.remove(spell);
    }

    private void debugSpells(){
        // Debugging method to let us know if at any point a spell still exists after it's body is destroyed.
        for (Spell spell : spells){
            if(spell.getB2body() == (null)){
                Log.error("Spell isn't properly deleted");
            }
        }
    }

    private void checkSpellCooldown(){
        // Probably temporary code. Written to test functionality.
        if (lastTouch && !inGameHud.getRightJoyStick().isTouched()){
            // I think that spells should be cast when the player releases the right joystick, so that you can
            // see the rotation of the player character and not rely on hopefully having touched the joystick correctly

            long time = System.currentTimeMillis();

            for (String spell : inGameHud.getSpellNames()) {
                if (spell.equals(inGameHud.getSpellSelector().getSpellSelected())) {
                    activeSpell = spell;
                    break;
                }
            }

            if (time > lastattacks.get(inGameHud.getSpellNames().indexOf(activeSpell)) + getCooldown(activeSpell)){
                castSpell(activeSpell);
                lastattacks.set(inGameHud.getSpellNames().indexOf(activeSpell), time);
            }

        }
    }

    // Checks if cooldown time is over, and enables the buttons.
    private void timer(){
        for(int i = 0; i < inGameHud.getSpellNames().size(); i++){
            long time = lastattacks.get(i) + getCooldown(inGameHud.getSpellNames().get(i)) - System.currentTimeMillis();
            if(time < 0){
                inGameHud.getSpellSelector().enableButton(inGameHud.getSpellNames().get(i));
            }
        }
    }

    // =========

    // GAME LOGIC =====

    // UPDATING

    @Override
    public void update(float dt) {
        // Deletes bodies after collision
        delete();

        checkWizardPlayerHealth();

        debugSpells();

        world.step(1 / 60f, 6, 2);
        mapHandler.setView(camera);

        if(wizardPlayer.getB2body().isActive()){
            // Checks if player tries to cast spell and the spell is ready
            checkSpellCooldown();
            updateWizardPositionAndRotation();
            //Update camera to follow player.
            updateCamera();
        }

        lastTouch = inGameHud.getRightJoyStick().isTouched();
        lastAim = TouchPadAimInput1.getInstance().getAimDirection();

        // Iterate spells to update
        for (Spell spell : spells) {
            spell.update();
        }

        // Keep the Wizard object in sync with Player object for the other players
        for (Player player : network.getPlayers().values()) {
            if(player.isAlive()){
                wizardEnemies.get(player.getPlayerSlotId()).setPosition(player.getPosition());
                wizardEnemies.get(player.getPlayerSlotId()).setRotation(player.getRotation());
                wizardEnemies.get(player.getPlayerSlotId()).updateBodyPosition(player.getPosition());
            }
        }

        timer();

        Gdx.input.setCatchBackKey(true);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))) {
            this.onBackButtonPress();
        }
    }

    // RENDERING

    @Override
    public void render(SpriteBatch spriteBatch) {
        //Combines camera's coordinate system with world coordinate system.
        spriteBatch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the ground
        mapHandler.renderGround();

        spriteBatch.begin();

        // Draw the game objects above ground
        // Iterate spells to render
        for (Spell spell : spells) {
            spell.render(spriteBatch);
        }

        if(wizardPlayer.getB2body().isActive() && wizardPlayer.getHealth() <= 0){
            handlePlayerDead();
        }

        drawEnemies(spriteBatch);
        drawPlayer(spriteBatch);

        spriteBatch.end();

        // Draw the trees and rock which the player can go behind
        mapHandler.renderTreesAndRocks();
        stage.act(Gdx.graphics.getDeltaTime());
        // box2d debug renderer
        // Renders visible boxes around all collidable objects.
//        b2dr.render(world, camera.combined);
        // Draws the game stage
        stage.draw();

        //Secondly draw the Hud
        spriteBatch.setProjectionMatrix(inGameHud.getStage().getCamera().combined);
        inGameHud.getStage().draw();
    }

    private void drawEnemies(SpriteBatch spriteBatch){
        for (Integer wizardId : wizardEnemies.keySet()) {
            WizardEnemy enemy = wizardEnemies.get(wizardId);
            TextureRegion enemyRegion = wizardEnemyRegions.get(wizardId);

            for (Player player : network.getPlayers().values()){
                if(player.getPlayerSlotId() == wizardId){
                    if(player.isAlive()){
                        enemyRegion.setTexture(enemy.getPlayerSprite());
                    } else{
                        enemyRegion.setTexture(enemy.getPlayerDeadSprite());
                    }
                    break;
                }
            }

            spriteBatch.draw(
                    enemyRegion, enemy.getPosition().x, enemy.getPosition().y,
                    enemy.getPlayerSprite().getWidth() / 2f,
                    enemy.getPlayerSprite().getHeight() / 2f,
                    enemy.getPlayerSprite().getWidth(), enemy.getPlayerSprite().getHeight(),
                    SCALE, SCALE, 0
            );
        }
    }

    private void drawPlayer(SpriteBatch spriteBatch){

        if(network.getPlayer().isAlive()){
            wizardPlayerRegion.setRegion(wizardPlayer.getPlayerSprite());
        } else {
            wizardPlayerRegion.setRegion(wizardPlayer.getPlayerDeadSprite());
        }

        spriteBatch.draw(wizardPlayerRegion, wizardPlayer.getPosition().x, wizardPlayer.getPosition().y,
                wizardPlayer.getPlayerSprite().getWidth() / 2f,
                wizardPlayer.getPlayerSprite().getHeight() / 2f,
                wizardPlayer.getPlayerSprite().getWidth(), wizardPlayer.getPlayerSprite().getHeight(),
                SCALE, SCALE, 0
        );
    }

    // EVENTS

    public void handlePlayerDead(){
        camera.zoom = 2*SCALE;
        camera.position.set(
                mapHandler.getMapSize().x/2,
                mapHandler.getMapSize().x/2,
                0
        );
        inGameHud.dispose();
        wizardPlayer.getB2body().setActive(false);
    }

    public void handleEnemyDead(int playerSlotId){
        WizardEnemy enemy = wizardEnemies.get(playerSlotId);
        enemy.getB2body().setActive(false);
    }

    private void checkWizardPlayerHealth(){
        inGameHud.getHealthBar().updateHealth(wizardPlayer.getHealth());
    }

    // GARBAGE COLLECTION

    public void addToBodyList(Body body){
        if(!bodiesToDestroy.contains(body,true)){
            bodiesToDestroy.add(body);
        }
    }

    private void delete(){
        for (Body body : bodiesToDestroy) {
            world.destroyBody(body);
        }
        bodiesToDestroy.clear();
    }

    private long getCooldown(String spellname){
        if(spellname.equals("FireBall")){
            return FireBall.cooldown;
        }
        return Ice.cooldown;
    }

    // ==========


    @Override
    public void dispose() {

    }

    @Override
    public void handleInput() {

    }

    // ACTIONS =====

    @Override
    public void onBackButtonPress() {
        this.gameStateManager.push(new InGameMenuState(gameStateManager));
    }

    @Override
    public void activate(){
        super.activate();
        Gdx.input.setInputProcessor(inGameHud.getStage());
    }

    // ==========

}
