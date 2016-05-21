/**
 package com.group9.crazygolf.menu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sun.org.apache.bcel.internal.generic.InstructionComparator;

public class PlayerCountScreen implements Screen, InputProcessor {
    Game game;
    SpriteBatch batch;
    Texture img;
    Stage stage;
    private TextButton Proceed;
    private TextButton PlayerCancel;
    private TextField PlayerNumber;
    private Label Instructions;
    private int pCount;

    PlayerCountScreen(Game game, int numPlayers){
        this.game = game;
        batch = new SpriteBatch();
        img = new Texture("Golf(Blur_and_Darken).jpg");
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        pCount = numPlayers;

        PlayerCancel = new TextButton("Cancel", skin); PlayerCancel.setPosition(550,275);PlayerCancel.setSize(200, 50);stage.addActor(PlayerCancel);
        PlayerCancel.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
menu();
            }
        });

        PlayerNumber = new TextField("", skin); PlayerNumber.setPosition(550, 350);PlayerNumber.setSize(200,50);stage.addActor(PlayerNumber);
        Proceed = new TextButton("Proceed", skin); Proceed.setPosition(550,425);Proceed.setSize(200, 50);stage.addActor(Proceed);
        Proceed.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                pCount = tryParseInt(PlayerNumber.getText());
                if (pCount>0 && pCount<7)
                {
                    SetPlayers();
                } else{
                    PlayerScreen();
                }
            }
        });
        Instructions= new Label("Insert a Number Between 1 and 6 ", skin);
        Instructions.setPosition(525, 475);Instructions.setSize(350, 50); stage.addActor(Instructions);
    }

    public int tryParseInt(String value)
    {
        try{
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe){
            return 0;
        }
    }

 public void menu()
    {
        game.setScreen(new MenuScreen(game));
    }

    public void PlayerScreen()
    {
        game.setScreen(new PlayerCountScreen(game, 0));
    }

    public void SetPlayers()
    {
        game.setScreen(new PlayerScreen(game, pCount));
    }
    @Override
    public void show() {
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
    public boolean keyUp(int keycode) {return false;}
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
 */