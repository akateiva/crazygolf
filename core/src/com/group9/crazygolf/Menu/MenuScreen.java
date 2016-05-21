package com.group9.crazygolf.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.group9.crazygolf.coursedesginer.CourseDesignerScreen;
import com.group9.crazygolf.crazygolf;
import com.group9.crazygolf.game.GameScreen;


public class MenuScreen implements Screen, InputProcessor {
    final crazygolf game;
    SpriteBatch batch;
    Texture img;
    Stage stage;
    boolean gamePaused = false;
    private TextButton Player;
    private TextButton Play;
    private TextButton Exit;
    private TextButton CD;
    private TextButton Back;

    public MenuScreen(crazygolf game) {
        this.game = game;
        batch = new SpriteBatch();
        img = new Texture("Golf(Blur_and_Darken).jpg");
        stage = new Stage();

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        Play = new TextButton("New game", skin);
        Play.setPosition(550, 350);
        Play.setSize(200, 50);
        stage.addActor(Play);
        Play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newGame();
            }
        });

        CD = new TextButton("Course Designer", skin);
        CD.setPosition(550, 275);
        CD.setSize(200, 50);
        stage.addActor(CD);
        CD.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                newCourseDesigner();
            }
        });


        Exit = new TextButton("Exit", skin);
        Exit.setPosition(550, 200);
        Exit.setSize(200, 50);
        stage.addActor(Exit);
        Exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (gamePaused) {
                    resumeGame();
                } else {
                    Gdx.app.exit();
                }
            }

        });
    }

    /**
     * Informs the menu whether it is a pause menu or a start menu
     *
     * @param gamePaused
     */
    public void setGamePaused(boolean gamePaused) {
        this.gamePaused = gamePaused;

        if (gamePaused) {
            Exit.setText("Resume");
        } else {
            Exit.setText("Exit");
        }
    }

    public void resumeGame() {
        game.hidePauseMenu();
    }

    public void newGame() {
        game.setScreen(new GameScreen(game));
    }

    public void newCourseDesigner() {
        game.setScreen(new CourseDesignerScreen(game));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        stage.act(delta);
        batch.draw(img, 0, 0);
        batch.end();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
