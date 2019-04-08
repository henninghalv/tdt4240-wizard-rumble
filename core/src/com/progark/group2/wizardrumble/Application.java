package com.progark.group2.wizardrumble;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.states.GameStateManager;
import com.progark.group2.wizardrumble.states.MainMenuState;

import java.io.IOException;


/**
 * Keeps the main render loop Initialize the game, network controller and the gameManager, then
 * starts the first state.
 */
public class Application extends Game {

	// Window parameters
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	public static final float SCALE = 0.125f;
	public static final String TITLE = "WizardRumble";


	private SpriteBatch spriteBatch;
	private GameStateManager gameStateManager;


    @Override
	public void create () {
		Gdx.graphics.setTitle(TITLE);
		Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
        spriteBatch = new SpriteBatch();

		// Used to test server connection.
		// Not necessarily the right way to do it.
		try {
            NetworkController networkController = NetworkController.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}

        this.gameStateManager = GameStateManager.getInstance();
		try {
			gameStateManager.push(MainMenuState.getInstance());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render () {
		gameStateManager.update(Gdx.graphics.getDeltaTime());
		gameStateManager.render(spriteBatch);
	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
	}
}
