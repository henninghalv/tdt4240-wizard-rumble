package com.progark.group2.wizardrumble.states;

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

        this.stage.addActor(table);
    }


    @Override
    public void update(float dt) {

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
}
