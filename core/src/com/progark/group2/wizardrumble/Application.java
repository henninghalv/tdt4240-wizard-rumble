package com.progark.group2.wizardrumble;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.progark.group2.wizardrumble.entities.Spell;
import com.progark.group2.wizardrumble.network.NetworkController;
import com.progark.group2.wizardrumble.states.GameStateManager;

import java.util.List;

/**
 * Initialize the game, network controller and the gameManager, then starts the first state.
 */
public class Application extends ApplicationAdapter {
	// Window parameters
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	public static final float SCALE = 0.5f;
	public static final String TITLE = "WizardRumble";


	private SpriteBatch batch;
	private GameStateManager gameStateManager;
	private NetworkController nc;

	private Texture img;


	/*
	private int height;
	private int width;
	private List<Spell> spellList;
	*/
	
	@Override
	public void create () {
		Gdx.graphics.setTitle(TITLE);
		Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}



	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
