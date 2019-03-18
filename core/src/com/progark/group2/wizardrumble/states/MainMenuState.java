package com.progark.group2.wizardrumble.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/** The main menu that is shown when the game is launched
 */
public class MainMenuState extends State {

    private Texture button;
    private Stage stage;
    private Table table;

    public MainMenuState(GameStateManager gameStateManager){
        super(gameStateManager);

        button = new Texture("UI/blue_button00.png");

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
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(button, 0, 0);
        spriteBatch.end();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onBackButtonPress() {
        this.quitGame();
    }

    private void quitGame(){
        // TODO lol how do u qq?
    }
}
