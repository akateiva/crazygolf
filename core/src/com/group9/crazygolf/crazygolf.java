package com.group9.crazygolf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.group9.crazygolf.menu.MenuScreen;

public class crazygolf extends Game {
    private MenuScreen menuScreen;
    private Screen previousScreen = null;

	@Override
	public void create () {
        menuScreen = new MenuScreen(this);
        setScreen(menuScreen);
    }

    public Screen getMenuScreen() {
        return menuScreen;
    }

    public void showPauseMenu() {
        menuScreen.setGamePaused(true);
        previousScreen = getScreen();
        setScreen(menuScreen);
    }

    public void hidePauseMenu() {
        setScreen(previousScreen);
        System.out.println(previousScreen);
        menuScreen.setGamePaused(false);
    }
}
