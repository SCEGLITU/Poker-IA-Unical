package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.mygdx.game.control.Game;
import com.mygdx.game.graphicsGDX.PrinterGDX;

public class MyGdxGame extends ApplicationAdapter {

	// **********   GLOBAL VARIABLES   *************
	public static int CARD_WIDTH = 75;
	public static int CARD_HEIGHT = 95;
	public static int WORLD_WIDTH=1100;
	public static int WORLD_HEIGHT=650;
	// ********** END GLOBAL VARIABLES *************

	private SpriteBatch batch;
	private Game game;
	private PrinterGDX printerGDX;

	@Override
	public void create () {
		batch = new SpriteBatch();
		game = new Game(batch);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor((float)0.0, (float)0.5, (float)0.0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		game.draw();

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
