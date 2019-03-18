package com.progark.group2.wizardrumble.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.progark.group2.wizardrumble.Application;

import java.awt.Font;

/** The main menu that is shown when the game is launched
 */
public class MainMenuState extends State {

    private Image buttonImage;
    private Texture buttonTexture;
    private Stage stage;
    private Table table;
    private Viewport viewport;
    private TextButton textButton;
    private TextButton.TextButtonStyle textButtonStyle;

    public MainMenuState(GameStateManager gameStateManager){
        super(gameStateManager);

        viewport = new FitViewport(Application.WIDTH,Application.HEIGHT, this.camera);

        buttonTexture = new Texture("UI/blue_button00.png");
        buttonImage = new Image(buttonTexture);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);


        BitmapFont font = new BitmapFont();
        font.setColor(Color.BLACK);
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButton = new TextButton("Start", textButtonStyle);

        Stack button = new Stack();
        button.add(buttonImage);
        button.add(textButton);

        button.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Hallo?");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Bye");
            }
        });

        table = new Table();
        table.add(button);
        table.setFillParent(true);

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
    }
}
