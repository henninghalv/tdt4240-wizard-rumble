package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.progark.group2.wizardrumble.controllers.AimInput;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.entities.WizardPlayer;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;
import com.progark.group2.wizardrumble.entities.Spell;
import com.progark.group2.wizardrumble.entities.spells.FireBall;

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

    private OrthographicCamera camera;
    private MapHandler mapHandler;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;

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
        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();

        mapHandler = new MapHandler();

        //Startposition must be changed. It is only like this while the user input moves.
        wizard = new WizardPlayer(Wizard.DEFAULT_HEALTH, new Vector2(WIDTH / 2f, HEIGHT / 2f + (32 * 4)), world);
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


        // Set camera to initial wizard position
        camera.position.set(wizard.getPosition().x + wizard.getSprite().getWidth()/2f, wizard.getPosition().y + wizard.getSprite().getHeight()/2f, 0);

        // Used for testing spells.
        spells = new ArrayList<Spell>();
        lastTouch = false;

    }

    public static InGameState getInstance(){
        if (instance == null){
            instance = new InGameState(GameStateManager.getInstance());
        }
        System.out.print("created instance");
        return instance;
    }

    public World getWorld(){
        return world;
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
                        wizard.getPosition().x - (WIDTH + wizard.getSprite().getWidth())/2f ,
                        wizard.getPosition().y - (HEIGHT + wizard.getSprite().getHeight())/2f
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
        world.step(1/60f, 6, 2);

        mapHandler.setView(camera);

        // Let the player rotate primarily using the right stick, but use left stick if right stick input is absent.
        updateWizardPosition();
        if (leftJoyStick.isTouched()){
            updateWizardRotation();
            //Update camera to follow player. If we move player sprite to player, we have to fix this method.


            updateCamera(wizard.getPosition().x + wizard.getSprite().getWidth() / 2f, wizard.getPosition().y + wizard.getSprite().getHeight() / 2f);
            System.out.println(wizard.getPosition());

        }
        if (rightJoyStick.isTouched()){
            wizard.updateRotation(new Vector2(rightJoyStick.getKnobPercentX(),rightJoyStick.getKnobPercentY()));
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

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {

    }

}
