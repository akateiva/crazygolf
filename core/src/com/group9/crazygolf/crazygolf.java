package com.group9.crazygolf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class crazygolf extends Game {

	//After loading the game, show the menu screen
	@Override
	public void create () {
		this.setScreen(new MenuScreen(this));
	}




}
