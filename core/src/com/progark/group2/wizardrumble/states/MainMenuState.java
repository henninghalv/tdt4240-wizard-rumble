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


/** The main menu that is shown when the game is launched
 */
public class MainMenuState extends State {

    private Stage stage;
    private Table table;
    private Label.LabelStyle labelStyle;
    private Texture imageTexture;

    private final String title = "Wizard Rumble";

    //TODO Remove sout and uncomment method calls in the button listeners
    public MainMenuState(GameStateManager gameStateManager){
        super(gameStateManager);

        initializeVariables();

        // Buttons
        Stack startButton = addButton("Start");
        startButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Start game");
                // startGame();
            }
        });

        Stack settingsButton = addButton("Settings");
        settingsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Settings");
                // openSettings();
            }
        });

        Stack exitButton = addButton("Exit");
        exitButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Exit");
                onBackButtonPress();
            }
        });

        stage.addActor(table);
    }

    private void startGame(){
        this.gameStateManager.set(new InGameState(this.gameStateManager));
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
     * Mek button
     *
     * @param buttonText
     * @return
     */
    private Stack addButton(String buttonText){
        Label label = new Label(buttonText, labelStyle);
        label.setAlignment(Align.center);

        Image buttonImage = new Image(this.imageTexture);

        Stack button = new Stack();
        button.add(buttonImage);
        button.add(label);

        this.table.add(button).pad(10f);
        this.table.row();

        return button;
    }

    /**
     * Initialize stuff
     */
    private void initializeVariables(){
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Button styling
        this.imageTexture = new Texture("UI/blue_button00.png");
        BitmapFont font = new BitmapFont();
        font.setColor(Color.BLACK);
        this.labelStyle = new Label.LabelStyle(font, font.getColor());

        // Layout styling
        this.table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        table.center();

        Label label = new Label(this.title, labelStyle);
        label.setAlignment(Align.center);
        label.setSize((float)Gdx.graphics.getWidth()/4, (float)Gdx.graphics.getHeight()/4);
        table.add(label).size((float)Gdx.graphics.getWidth()/4, (float)Gdx.graphics.getHeight()/4);
        table.row();
    }
}
