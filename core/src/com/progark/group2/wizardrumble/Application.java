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

import java.util.List;

public class Application extends ApplicationAdapter {
	private SpriteBatch batch;

	private GameStateManager gsm;
	private NetworkController nc;
	private int height;
	private int width;
	private List<Spell> spellList;


	/*
	public void update(){

	}

	*/
	
	@Override
	public void create () {
		gsm = new GameStateManager();
		batch = new SpriteBatch();
		gsm.push(new InGameState());
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
