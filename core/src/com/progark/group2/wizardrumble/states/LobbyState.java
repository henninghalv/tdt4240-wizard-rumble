package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LobbyState extends MenuState {

    private Stage stage;
    private Table table;
    private Label.LabelStyle titleStyle;

    private static LobbyState instance = null;

    private final String title = "Lobby - waiting for players...";

    private LobbyState(GameStateManager gameStateManager) {
        super(gameStateManager);
        initialize();


        // Panel1
        Stack panel1 = menuButton("");
        this.table.add(panel1).pad(0F);
        this.table.row();


        // Panel2
        Stack panel2 = menuButton("");
        this.table.add(panel2).pad(0F);
        this.table.row();


        // Panel3
        Stack panel3 = menuButton("");
        this.table.add(panel3).pad(0F);
        this.table.row();


        // Panel4
        Stack panel4 = menuButton("");
        this.table.add(panel4).pad(0F);
        this.table.row();


        // Panel5
        Stack panel5 = menuButton("");
        this.table.add(panel5).pad(0F);
        this.table.row();

        // Panel5
        Stack panel6 = menuButton("");
        this.table.add(panel6).pad(0F);
        this.table.row();

        // back button
        Stack startButton = this.menuButton("Back");
        startButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                backToMainMenu();
            }
        });
        this.table.add(startButton).pad(100f);
        this.table.row();

        stage.addActor(table);
    }

    public static LobbyState getInstance() {
        if (instance == null) {
            instance = new LobbyState(GameStateManager.getInstance());
        }
        return instance;
    }

    private void backToMainMenu(){
        this.gameStateManager.set(MainMenuState.getInstance());
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
