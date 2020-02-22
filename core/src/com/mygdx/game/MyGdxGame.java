package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter {

	// **********   GLOBAL VARIABLES   *************
	public static int CARD_WIDTH = 50;
	public static int CARD_HEIGHT = 70;
	public static int WORLD_WIDTH=1100;
	public static int WORLD_HEIGHT=650;
	// ********** END GLOBAL VARIABLES *************

	SpriteBatch batch;
	Deck cards;

	@Override
	public void create () {
		batch = new SpriteBatch();
		cards = new Deck();
		cards.setCardPosition(1,Suite.CLUBS,WORLD_WIDTH/2,WORLD_HEIGHT/2);
		}

	@Override
	public void render () {
		Gdx.gl.glClearColor((float)0.0, (float)0.5, (float)0.0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		cards.getCard(1,Suite.CLUBS).draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose ()
	{
		cards.dispose();
		batch.dispose();
	}
}
