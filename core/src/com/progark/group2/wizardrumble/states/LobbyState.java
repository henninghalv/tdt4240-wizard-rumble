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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.network.resources.Player;
import com.progark.group2.wizardrumble.states.resources.UIButton;

import java.io.IOException;
import java.util.HashMap;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class LobbyState extends State {

    private NetworkController network;

    private Table table = new Table();
    private Label.LabelStyle titleStyle;
    private Texture backgroundImage;
    private HashMap<Integer, Player> players = new HashMap<Integer, Player>();

    private final String title = "Lobby - waiting for players...";

    protected LobbyState(GameStateManager gameStateManager) throws IOException {
        super(gameStateManager);
        initialize();
    }

    private void createPanels() {
        table.add(new UIButton(new Texture("UI/blue_button01.png"), network.getPlayer().getName()).getButton()).spaceBottom(10f).pad(0F);
        table.row();

        for (Integer playerId : players.keySet()) {
            table.add(new UIButton(new Texture("UI/blue_button01.png"), players.get(playerId).getName()).getButton()).pad(0F);
            table.row();
        }
    }

    private void createBackButton() {
        // back button
        Stack backButton = new UIButton(new Texture("UI/blue_button00.png"), "Back").getButton();
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

        table.add(backButton).pad(50f);
    }

    private void createStartButton() {
        // back button
        Stack startButton = new UIButton(new Texture("UI/blue_button00.png"), "Start game").getButton();
        startButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                requestGameStart();
            }
        });

        table.add(startButton).pad(70f);

    }

    private void backToMainMenu() throws IOException {
        this.gameStateManager.set(MainMenuState.getInstance());
        network.playerLeftGame();
    }

    private void requestGameStart() {
        network.requestGameStart();
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

        table.clear();
        renderLobbyTitle();
        if(network.getPlayer() != null){
            players.clear();
            players.putAll(network.getPlayers());
            createPanels();
        }
        createBackButton();
        if(!players.isEmpty()){
            createStartButton();  // TODO: Remove. No manual starting. Meant for testing.
        }
        stage.addActor(table);

    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(backgroundImage, 0, 0);
        spriteBatch.end();
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
        // Getting NetworkController
        try {
            network = NetworkController.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        backgroundImage = new Texture("background.png");
        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Layout styling
        table.setFillParent(true);
        table.center();

    }

    private void renderLobbyTitle(){

        // Title
        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        this.titleStyle = new Label.LabelStyle(font, font.getColor());

        Label label = new Label(this.title, titleStyle);
        label.setAlignment(Align.center);
        label.setSize((float)Gdx.graphics.getWidth()/4, (float)Gdx.graphics.getHeight()/12);
        table.add(label).size((float)Gdx.graphics.getWidth()/4, (float)Gdx.graphics.getHeight()/12);
        table.row();

//        table.debug();
        stage.addActor(table);
    }
}
