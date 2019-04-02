package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.io.IOException;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class InGameMenuState extends MenuState {

    private Table table;
    private Sprite pauseOverlay;
    private Vector3 cameraPosition;


    InGameMenuState(GameStateManager gameStateManager){
        super(gameStateManager);

        table = new Table();
        table.setFillParent(true);
        table.center();

        // resumeButton
        Stack resumeButton = this.menuButton("Resume");
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

        // settingsButton
        Stack settingsButton = this.menuButton("Settings");
        settingsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                openSettings();
            }
        });
        this.table.add(settingsButton).pad(10f);
        this.table.row();

        // exitButton
        Stack exitButton = this.menuButton("Exit");
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
    }


    @Override
    public void update(float dt) {
        //TODO esc doesn't work, but not crucial for phone
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            this.onBackButtonPress();
        }

        try {
            this.cameraPosition = InGameState.getInstance().getCamPosition();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        this.pauseOverlay.draw(spriteBatch);
        spriteBatch.end();

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
        this.gameStateManager.pop();
        GameStateManager.getInstance().set(MainMenuState.getInstance());
    }
}
