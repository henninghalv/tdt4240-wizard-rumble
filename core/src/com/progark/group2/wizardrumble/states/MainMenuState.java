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

    //private Image buttonImage;
    private Stage stage;
    private Table table;
    private Label.LabelStyle labelStyle;
    private Texture imageTexture;

    public MainMenuState(GameStateManager gameStateManager){
        super(gameStateManager);

        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Button styling
        this.imageTexture = new Texture("UI/blue_button00.png");
        BitmapFont font = new BitmapFont();
        font.setColor(Color.BLACK);
        this.labelStyle = new Label.LabelStyle(font, font.getColor());

        this.table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        table.center();

        // Buttons
        Stack startButton = addButton("Start");
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

        Stack exitButton = addButton("Exit");
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

        stage.addActor(table);
    }

    public void startGame(){
        this.gameStateManager.set(new InGameState(this.gameStateManager));
    }

    public void openSettings(){
        this.gameStateManager.push(new MainMenuSettings(this.gameStateManager));
    }

    public void openPlayerSettings(){
        this.gameStateManager.push(new PlayerStatsState(this.gameStateManager));
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
        // TODO lol how do u qq?
        System.out.println("Exit");
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
}
