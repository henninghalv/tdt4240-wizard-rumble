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
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.network.resources.Player;

import java.io.IOException;
import java.util.HashMap;

public class LobbyState extends MenuState {

    private NetworkController network = NetworkController.getInstance();

    private Stage stage;
    private Table table;
    private Label.LabelStyle titleStyle;

    private static LobbyState instance = null;

    private final String title = "Lobby - waiting for players...";

    private LobbyState(GameStateManager gameStateManager) throws IOException {
        super(gameStateManager);
        // Getting NetworkController
        initialize();
    }

    public static LobbyState getInstance() throws IOException {
        if (instance == null) {
            instance = new LobbyState(GameStateManager.getInstance());
        }
        return instance;
    }

    private void createPanels() {
        for (Integer playerId : network.getPlayers().keySet()) {
            table.add(menuButton(network.getPlayers().get(playerId).getName())).pad(0F);
            table.row();
        }
        table.add(menuButton(network.getPlayer().getName())).pad(0F);
        table.row();
    }

    private void createBackButton() {
        // back button
        Stack backButton = this.menuButton("Back");
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    backToMainMenu();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        table.add(backButton).pad(100f);
    }

    private void createStartButton() {
        // back button
        Stack startButton = this.menuButton("Start Game");
        startButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    startGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        table.add(startButton).pad(70f);

    }

    private void backToMainMenu() throws IOException {
        this.gameStateManager.set(MainMenuState.getInstance());
    }

    private void requestGameStart() {
        network.requestGameStart();
    }

    public void startGame() throws IOException {
        InGameState state = InGameState.getInstance();
        System.out.println("Instance: " + state);
        this.gameStateManager.set(state);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        if(!network.getPlayers().isEmpty()){
            table.clear();
            createPanels();
            createBackButton();
            createStartButton();  // TODO: Remove. No manual starting. Meant for testing.
            stage.addActor(table);
        }
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
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Layout styling
        table = new Table();
        table.setFillParent(true);
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

        table.debug();
        stage.addActor(table);
    }
}
