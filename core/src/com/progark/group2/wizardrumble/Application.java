package com.progark.group2.wizardrumble;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.progark.group2.wizardrumble.entities.Spell;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.states.GameStateManager;
import com.progark.group2.wizardrumble.states.InGameState;

import java.io.IOException;
import java.util.List;

/**
 * Keeps the main render loop Initialize the game, network controller and the gameManager, then
 * starts the first state.
 */
public class Application extends ApplicationAdapter {

	// Window parameters
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	public static final float SCALE = 0.5f;
	public static final String TITLE = "WizardRumble";


	private SpriteBatch spriteBatch;
	private GameStateManager gameStateManager;
	private NetworkController networkController;

	// TODO remove
	private Texture img;

	// TODO move to inGameSate
	private List<Spell> spellList;


	
	@Override
	public void create () {
		Gdx.graphics.setTitle(TITLE);
		Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
        spriteBatch = new SpriteBatch();

		// Used to test server connection.
		// Not necessarily the right way to do it.
        networkController = new NetworkController();

		try {
            networkController.init();
		} catch (IOException e) {
			e.printStackTrace();
		}

        this.gameStateManager = GameStateManager.getInstance();
        gameStateManager.push(new InGameState(gameStateManager));
	}

	@Override
	public void render () {
		//TODO remove
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		//spriteBatch.draw(img, 0, 0);
		spriteBatch.end();

		gameStateManager.update(Gdx.graphics.getDeltaTime());
		gameStateManager.render(spriteBatch);
	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
		//img.dispose();
		//gameStateManager.dispose();
	}
}
