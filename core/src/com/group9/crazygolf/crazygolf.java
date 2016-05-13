package com.group9.crazygolf;

import com.badlogic.gdx.Game;
import com.group9.crazygolf.Menu.MenuScreen;

public class crazygolf extends Game {
	//After loading the game, show the Menu screen
	@Override
	public void create () {
		this.setScreen(new MenuScreen(this));
	}
}
