package com.group9.crazygolf.Menu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.group9.crazygolf.GameScreen;


public class MenuScreen implements Screen, InputProcessor {
    Game game;
    SpriteBatch batch;
    Texture img;
    Stage stage;
    private TextButton Player;
    private TextButton Play;
    private TextButton Exit;
    private TextButton CD;
    private PlayerScreen mPS;
    private boolean setPS = false;

    public MenuScreen(Game game){
        this(game, null);
    }


    MenuScreen(Game game, PlayerScreen PS){
        if (PS!=null)
        {
            setPS = true;
        }
        this.game = game;
        batch = new SpriteBatch();
        img = new Texture("Golf(Blur_and_Darken).jpg");
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        mPS = PS;

        Player = new TextButton("Players", skin); Player.setPosition(550,425);Player.setSize(200, 50);stage.addActor(Player);
        Player.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                PlayerCountScreen();
            }
        });
        Play = new TextButton("Play", skin);Play.setPosition(550, 350);Play.setSize(200, 50);stage.addActor(Play);
        Play.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                newGame();
            }
        });

        CD = new TextButton("Course Designer", skin);CD.setPosition(550, 275);CD.setSize(200, 50);stage.addActor(CD);
        CD.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {

            }
        });

        Exit = new TextButton("Exit", skin);Exit.setPosition(550, 200);Exit.setSize(200, 50);stage.addActor(Exit);
        Exit.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                Gdx.app.exit();
            }
        });
    }

    public void newGame()
    {
        game.setScreen(new GameScreen(game));
    }


    public void PlayerCountScreen()
    {
        if (setPS) {
            game.setScreen(mPS);

        }else{
            game.setScreen(new PlayerCountScreen(game, 0));
        }
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
        if(keycode == Input.Keys.ENTER){
            game.setScreen(new GameScreen(game));
            return false;
        }
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
