package com.progark.group2.wizardrumble.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


/** The main menu that is shown when the game is launched. The listeners must be added individually
 * to the buttons that are created by the parent class MenuState.
 */
public class MainMenuState extends MenuState {

    private Stage stage;
    private Table table;
    private Label.LabelStyle titleStyle;

    private final String title = "Wizard Rumble";


    //TODO Remove sout and uncomment method calls in the button listeners
    public MainMenuState(GameStateManager gameStateManager){
        super(gameStateManager);
        initialize();


        // startButton
        Stack startButton = this.menuButton("Start");
        startButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                startGame();
            }
        });
        this.table.add(startButton).pad(10f);
        this.table.row();

        // Settings
        Stack settingsButton = menuButton("Settings");
        settingsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Settings");
                openSettings();
            }
        });
        this.table.add(settingsButton).pad(10f);
        this.table.row();

        // exitButton
        Stack exitButton = menuButton("Exit");
        exitButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onBackButtonPress();
            }
        });
        this.table.add(exitButton).pad(10f);
        this.table.row();

        stage.addActor(table);
    }

    private void startGame(){
        this.gameStateManager.set(InGameState.getInstance());
    }

    private void openSettings(){
        this.gameStateManager.push(new MainMenuSettings(this.gameStateManager));
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override

    public void render(SpriteBatch spriteBatch) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void onBackButtonPress() {
        Gdx.app.exit();
    }

    /**
     * Initialize stage, input, table and font for title.
     */
    private void initialize() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Layout styling
        this.table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);
        table.center();

        // Title
        BitmapFont font = new BitmapFont();
        font.setColor(Color.BLACK);
        this.titleStyle = new Label.LabelStyle(font, font.getColor());

        Label label = new Label(this.title, titleStyle);
        label.setAlignment(Align.center);
        label.setSize((float)Gdx.graphics.getWidth()/4, (float)Gdx.graphics.getHeight()/4);
        table.add(label).size((float)Gdx.graphics.getWidth()/4, (float)Gdx.graphics.getHeight()/4);
        table.row();
    }
}
