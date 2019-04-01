package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.entities.WizardEnemy;
import com.progark.group2.wizardrumble.entities.WizardPlayer;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;
import com.progark.group2.wizardrumble.entities.Spell;
import com.progark.group2.wizardrumble.entities.spells.FireBall;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.network.resources.Player;

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

    private MovementInput1 leftJoyStick;
    private AimInput1 rightJoyStick;

    // Used for testing spells
    private List<Spell> spells;
    private String selectedSpell;

    //private OrthographicCamera camera;
    private Viewport gamePort;
    private MapHandler mapHandler;

    //Box2d variables
    public final static World world = new World(new Vector2(0,0), true);
    private Box2DDebugRenderer b2dr;

    private static InGameState instance = null;

    // Last touch boolean for when rightJoyStick was touched last frame.
    // For when you release the right joystick, the spell should fire, instead of on justTouched().
    private boolean lastTouch;
    private float lastAimX;
    private float lastAimY;


    private InGameState(GameStateManager gameStateManager) throws IOException {
        super(gameStateManager);
        // Get the Network Controller
        network = NetworkController.getInstance();
        // Create a camera
        this.camera = new OrthographicCamera();
        gamePort = new FitViewport(WIDTH, HEIGHT, camera);
        //Setup Box2d and Map
        b2dr = new Box2DDebugRenderer();
        mapHandler = new MapHandler();

        // Creating a WizardPlayer object
        wizardPlayer = new WizardPlayer(
                Wizard.DEFAULT_HEALTH,
                network.getPlayer().getPosition(),  // TODO: Give some other spawn point
                new Texture("wizard_front.png")
        );

        // Add a new wizardPlayerRegion around Wizard
        wizardPlayerRegion = new TextureRegion(wizardPlayer.getSprite());

        // Creating all enemy players

        for (Player player : network.getPlayers().values()){
            WizardEnemy enemy = new WizardEnemy(
                    Wizard.DEFAULT_HEALTH,
                    player.getPosition(), // TODO: Give some other spawn point
                    new Texture("wizard_front.png")
            );
            wizardEnemies.put(player.getConnectionId(), enemy);
            TextureRegion enemyRegion = new TextureRegion(enemy.getSprite());
            wizardEnemyRegions.put(player.getConnectionId(), enemyRegion);
        }

        // Create Sprite Batch
        SpriteBatch sb = new SpriteBatch();

        leftJoyStick = new MovementInput1(15, 15);
        rightJoyStick = new AimInput1(WIDTH-15- AimInput1.diameter, 15);

        this.stage = new Stage(gamePort, sb);
        this.stage.addActor(leftJoyStick);
        this.stage.addActor(rightJoyStick);
        Gdx.input.setInputProcessor(this.stage);

        // Makes objects into bodies in the box2d world.
        // This makes them collidable.
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        // The number 3 is the index of the object layer in the TiledMap.
        for (MapObject object : mapHandler.getMap().getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2);

            body = world.createBody(bdef);
            shape.setAsBox(rectangle.getWidth() / 2, rectangle.getHeight() / 2);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
        // Set camera to initial wizardPlayer position
        camera.position.set(wizardPlayer.getPosition().x + wizardPlayer.getSprite().getWidth()/2f, wizardPlayer.getPosition().y + wizardPlayer.getSprite().getHeight()/2f, 0);
        // Used for testing spells.
        spells = new ArrayList<Spell>();
        lastTouch = false;
    }

    public static InGameState getInstance() throws IOException {
        if (instance == null){
            instance = new InGameState(GameStateManager.getInstance());
        }
        return instance;
    }

    public World getWorld(){
        return world;
    }

    private void updateWizardRotation(){
        wizardPlayer.updateRotation(
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
        Vector2 leftJoyPosition = new Vector2(leftJoyStick.getKnobPercentX(), leftJoyStick.getKnobPercentY());
        wizardPlayer.updatePosition(leftJoyPosition);
    }

    private void castSpell(String selectedSpell) {
        // Computes x and y from right joystick input making the speed the same regardless of
        // where on the joystick you touch.
        float x1 = lastAimX;
        float y1 = lastAimY;
        float hypotenuse = (float) Math.sqrt(x1 * x1 + y1 * y1);
        float ratio = (float) Math.sqrt(2) / hypotenuse;
        float x = x1 * ratio;
        float y = y1 * ratio;

        FireBall fb = new FireBall( // spawnPoint, rotation, velocity
                new Vector2(
                        // TODO: offsetting spell position by screen width and height is not desirable, but it works for now.
                        // Need to further look into how spell position or rendering is decided.
                        wizardPlayer.getPosition().x - (WIDTH + wizardPlayer.getSprite().getWidth())/2f ,
                        wizardPlayer.getPosition().y - (HEIGHT + wizardPlayer.getSprite().getHeight())/2f
                        ),
                wizardPlayer.getRotation(), // rotation
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
        world.step(1/60f, 6, 2);

        mapHandler.setView(camera);

        // Let the player rotate primarily using the right stick, but use left stick if right stick input is absent.
        updateWizardPosition();
        if (leftJoyStick.isTouched()){
            updateWizardRotation();
            //Update camera to follow player. If we move player sprite to player, we have to fix this method.
            updateCamera(wizardPlayer.getPosition().x + wizardPlayer.getSprite().getWidth() / 2f, wizardPlayer.getPosition().y + wizardPlayer.getSprite().getHeight() / 2f);
            // Sends the updated position to the server
            network.updatePlayerPosition(wizardPlayer.getPosition(), wizardPlayer.getRotation());
        }
        if (rightJoyStick.isTouched()){
            wizardPlayer.updateRotation(new Vector2(rightJoyStick.getKnobPercentX(),rightJoyStick.getKnobPercentY()));
            network.updatePlayerPosition(wizardPlayer.getPosition(), wizardPlayer.getRotation());
        }

        // Probably temporary code. Written to test functionality.
        if (lastTouch && !rightJoyStick.isTouched()){
        // if (rightJoyStick.isTouched()){
            // I think that spells should be cast when the player releases the right joystick, so that you can
            // see the rotation of the player character and not rely on hopefully having touched the joystick correctly
            castSpell("fireball");
        }
        lastTouch = rightJoyStick.isTouched();
        lastAimX = rightJoyStick.getKnobPercentX();
        lastAimY = rightJoyStick.getKnobPercentY();

        // Jank solution that takes in Wizards position and offsets by half screen size etc.
        // TODO Bind joysticks position to actual screen (suspect something with viewPort and/or stage)

        // The offsets might be off as well. Adding 30 to the rightJoySticks X seems wrong. #MagicNumbersBTW

        leftJoyStick.updatePosition(wizardPlayer.getPosition().x - WIDTH/2f + 15, wizardPlayer.getPosition().y  - HEIGHT/2f + 15);
        rightJoyStick.updatePosition(wizardPlayer.getPosition().x + WIDTH/2f + 35 - AimInput1.diameter, wizardPlayer.getPosition().y  - HEIGHT/2f + 15);

        // Iterate spells to update
        for (Spell spell : spells){
            spell.update();
        }

        // Keep the Wizard object in sync with Player object
        for (Player player : network.getPlayers().values()){
            wizardEnemies.get(player.getConnectionId()).setPosition(player.getPosition());
            wizardEnemies.get(player.getConnectionId()).setRotation(player.getRotation());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            this.onBackButtonPress();
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        //Combines camera's coordinate system with world coordinate system.
        spriteBatch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapHandler.render();
        spriteBatch.begin();

        spriteBatch.draw(wizardPlayerRegion, wizardPlayer.getPosition().x, wizardPlayer.getPosition().y,
                wizardPlayer.getSprite().getWidth()/2f,
                wizardPlayer.getSprite().getHeight()/2f,
                wizardPlayer.getSprite().getWidth(), wizardPlayer.getSprite().getHeight(),
                1,1, wizardPlayer.getRotation());

        // Iterate spells to render
        for (Spell spell : spells){
            spell.render(spriteBatch);
        }

        for(Integer wizardId : wizardEnemies.keySet()) {
            WizardEnemy enemy = wizardEnemies.get(wizardId);
            TextureRegion enemyRegion = wizardEnemyRegions.get(wizardId);
            spriteBatch.draw(
                    enemyRegion, enemy.getPosition().x, enemy.getPosition().y,
                    enemy.getSprite().getWidth()/2f,
                    enemy.getSprite().getHeight()/2f,
                    enemy.getSprite().getWidth(), enemy.getSprite().getHeight(),
                    1, 1, enemy.getRotation()
            );
        }

        spriteBatch.end();

        // box2d debug renderer
        // Renders visible boxes around all collidable objects.
        b2dr.render(world, camera.combined);
        stage.draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {
        this.gameStateManager.push(new InGameMenuState(gameStateManager));
    }

}
