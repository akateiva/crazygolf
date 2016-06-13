package com.group9.crazygolf.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
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
import com.group9.crazygolf.ui.FileChooser;


public class MenuScreen implements Screen {
    private final crazygolf game;
    public NewGameWindow newGameWindow;
    private SpriteBatch batch;
    private Texture img;
    private Stage stage;
    private Skin skin;
    private boolean gamePaused = false;
    private TextButton buttonExit;

    public MenuScreen(crazygolf game) {
        this.game = game;
        batch = new SpriteBatch();
        img = new Texture("Golf(Blur_and_Darken).jpg");
        stage = new Stage();

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton buttonNewGame = new TextButton("New game", skin);
        buttonNewGame.setPosition(550, 350);
        buttonNewGame.setSize(200, 50);
        stage.addActor(buttonNewGame);
        buttonNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newGameWindow();

            }
        });

        TextButton buttonCourseDesigner = new TextButton("Course Designer", skin);
        buttonCourseDesigner.setPosition(550, 275);
        buttonCourseDesigner.setSize(200, 50);
        stage.addActor(buttonCourseDesigner);
        buttonCourseDesigner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                newCourseDesigner();
            }
        });

        buttonExit = new TextButton("Exit", skin);
        buttonExit.setPosition(550, 200);
        buttonExit.setSize(200, 50);
        stage.addActor(buttonExit);
        buttonExit.addListener(new ChangeListener() {
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

    public void chooseFile() {
        FileChooser files = new FileChooser("Select Course File", skin) {
            @Override
            protected void result(Object object) {
                if (object.equals("OK")) {
                    FileHandle file = getFile();
                    newGameWindow.setFile(file);
                    newGameWindow.setCourseSelected();
                }
            }
        };
        files.setDirectory(Gdx.files.local("courses/"));
        files.show(stage);
    }

    //todo: fix this
    public void newGameWindow() {
        newGameWindow = new NewGameWindow(skin, game, this);
        stage.addActor(newGameWindow);
        newGameWindow.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 1);
    }
    /**
     * Informs the menu whether it is a pause menu or a start menu
     *
     * @param gamePaused
     */
    public void setGamePaused(boolean gamePaused) {
        this.gamePaused = gamePaused;

        if (gamePaused) {
            buttonExit.setText("Resume");
        } else {
            buttonExit.setText("Exit");
        }
    }

    private void resumeGame() {
        game.hidePauseMenu();
    }

    private void newCourseDesigner() {
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
        stage.act(delta);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //TODO: Implement proper resizing
        stage.getViewport().update(width, height, true);
    }


    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
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
}
