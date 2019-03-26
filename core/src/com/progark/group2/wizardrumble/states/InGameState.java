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
import com.progark.group2.wizardrumble.Application;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.handlers.MapHandler;
import com.progark.group2.wizardrumble.controllers.AimInput1;
import com.progark.group2.wizardrumble.controllers.MovementInput1;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameState extends State {

    private Wizard wizard;
    private TextureRegion region;

    private MovementInput1 leftJoyStick;
    private AimInput1 rightJoyStick;
    private Stage stage;

    private OrthographicCamera camera;
    private MapHandler mapHandler;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;



    public InGameState(GameStateManager gameStateManager) {
        super(gameStateManager);

        camera = new OrthographicCamera();
        Viewport gamePort = new FitViewport(Application.WIDTH, Application.HEIGHT, camera);

        //Box2d
        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();

        mapHandler = new MapHandler();

        //Startposition must be changed. It is only like this while the user input moves.
        wizard = new Wizard(new Vector2(Application.WIDTH / 2f, Application.HEIGHT / 2f + (32 * 4)), world);
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
                wizard.getSprite().getWidth()/(float)2,
                wizard.getSprite().getHeight()/(float)2,
                wizard.getSprite().getWidth(), wizard.getSprite().getHeight(),
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
