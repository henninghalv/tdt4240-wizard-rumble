package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;
import com.progark.group2.wizardrumble.states.resources.UIButton;
import com.progark.group2.wizardrumble.tools.SoundManager;

import java.io.IOException;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameMenuState extends State {

    private Table table;
    private Sprite pauseOverlay;
    private Vector3 cameraPosition;


    public InGameMenuState(GameStateManager gameStateManager){
        super(gameStateManager);

        table = new Table();
        table.setFillParent(true);
        table.center();

        // resumeButton
        Stack resumeButton = new UIButton(new Texture("UI/blue_button00.png"), "Resume").getButton();
        resumeButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onBackButtonPress();
            }
        });
        this.table.add(resumeButton).pad(10f);
        this.table.row();

        // exitButton
        Stack exitButton = new UIButton(new Texture("UI/blue_button00.png"), "Exit").getButton();
        exitButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    exitToMainMenu();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.table.add(exitButton).pad(10f);
        this.table.row();

        this.stage.addActor(table);

        this.pauseOverlay = new Sprite(new Texture("black.png"));
        this.pauseOverlay.setSize(WIDTH, HEIGHT);
        this.pauseOverlay.setAlpha(0.5f);

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        this.pauseOverlay.draw(spriteBatch);
        spriteBatch.end();

        Gdx.input.setInputProcessor(stage);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void onBackButtonPress() {
        this.gameStateManager.pop();
    }

    private void openSettings(){
        System.out.println("Settings");
        //this.gameStateManager.push(new InGameSettings(this.gameStateManager));
    }

    private void exitToMainMenu() throws IOException {
        NetworkController.getInstance().playerKilledBy(0);
        NetworkController.getInstance().playerLeftGame();
        this.gameStateManager.pop();
        GameStateManager.getInstance().set(MainMenuState.getInstance());
        SoundManager.getInstance().switchMusic("menu");
    }
}
