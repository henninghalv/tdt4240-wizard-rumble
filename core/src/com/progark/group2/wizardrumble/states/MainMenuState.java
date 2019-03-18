package com.progark.group2.wizardrumble.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.progark.group2.wizardrumble.Application;

/** The main menu that is shown when the game is launched
 */
public class MainMenuState extends State {

    private Image buttonImage;
    private Texture buttonTexture;
    private Stage stage;
    private Table table;
    private Viewport viewport;

    public MainMenuState(GameStateManager gameStateManager){
        super(gameStateManager);

        viewport = new FitViewport(Application.WIDTH,Application.HEIGHT, this.camera);

        buttonTexture = new Texture("UI/blue_button00.png");
        buttonImage = new Image(buttonTexture);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.add(buttonImage).size(buttonImage.getWidth(), buttonImage.getHeight());
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
