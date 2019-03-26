package com.progark.group2.wizardrumble.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class InGameMenuState extends MenuState {

    private Table table;


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
                exitToMainMenu();
            }
        });
        this.table.add(exitButton).pad(10f);
        this.table.row();

        this.stage.addActor(table);
    }


    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            this.onBackButtonPress();
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
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

    private void exitToMainMenu(){
        this.gameStateManager.set(new MainMenuState(this.gameStateManager));
    }
}
