package com.group9.crazygolf.Menu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class PlayerScreen implements Screen, InputProcessor {
    Game game;
    SpriteBatch batch;
    Texture img;
    Stage stage;
    private TextButton Return;
    private TextField txtName;
    private TextButton ResetPlayers;
    private TextButton Done;
    private int pCount;
    String[] pType = new String[]{"Human", "Bot"};


    PlayerScreen(Game game, int numPlayers){
        this.game = game;
        batch = new SpriteBatch();
        img = new Texture("Golf(Blur_and_Darken).jpg");
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        pCount = numPlayers;

        for (int i=0; i<pCount;i++)
        {
            txtName = new TextField("", skin);txtName.setPosition(550, 500-i*62);txtName.setSize(200,50);stage.addActor(txtName);

            SelectBox<String> PlayerType = new SelectBox<String>(skin); PlayerType.setItems(pType);PlayerType.setPosition(770, 500-i*62);
            PlayerType.setSize(80, 50);stage.addActor(PlayerType);

            Label PlayerNumber = new Label("Player "+(i+1), skin);
            PlayerNumber.setPosition(465, 500-i*62);PlayerNumber.setSize(50, 50); stage.addActor(PlayerNumber);
        }

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Return = new TextButton("Menu", skin);Return.setPosition(465,500-pCount*62);Return.setSize(120, 50);stage.addActor(Return);
        Return.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                Menu();
            }
        });

        ResetPlayers = new TextButton("Reset Players", skin);ResetPlayers.setPosition(735,500-pCount*62);ResetPlayers.setSize(120, 50);
        stage.addActor(ResetPlayers);
        ResetPlayers.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {pCountScreen();
            }
        });

        Done = new TextButton("Done", skin); Done.setPosition(600, 500-pCount*62);Done.setSize(120, 50);stage.addActor(Done);
    }

    public void pCountScreen()
    {
        game.setScreen(new PlayerCountScreen(game, 0));
    }

    public void Menu()
    {
        game.setScreen(new MenuScreen(game, this));
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
