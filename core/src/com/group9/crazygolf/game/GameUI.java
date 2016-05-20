package com.group9.crazygolf.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by akateiva on 20/05/16.
 */
public class GameUI {
    SpriteBatch batch;
    BitmapFont font50;
    ShapeRenderer shapeRenderer = new ShapeRenderer();

    LinkedList<FlashMessage> flashMessages;
    boolean powerBarVisible = false;
    float powerBarLevel = 1f;

    public GameUI() {
        batch = new SpriteBatch();
        FreeTypeFontGenerator freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Comic Sans MS.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        font50 = freeTypeFontGenerator.generateFont(parameter);
        font50.setColor(Color.YELLOW);
        freeTypeFontGenerator.dispose();

        flashMessages = new LinkedList<FlashMessage>();
    }


    public void update(float deltaTime) {
        batch.begin();

        float yOffset = 0;
        Iterator<FlashMessage> itr = flashMessages.iterator();
        while (itr.hasNext()) {
            FlashMessage flashMessage = itr.next();
            flashMessage.timeToLive -= deltaTime;
            if (flashMessage.timeToLive < 0) {
                itr.remove();
                continue;
            }

            font50.draw(batch, flashMessage.text, Gdx.graphics.getWidth() / 2 - flashMessage.glyphLayout.width / 2, Gdx.graphics.getHeight() - yOffset);
            yOffset += flashMessage.glyphLayout.height + 8f;
        }
        batch.end();

        if (powerBarVisible) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            //Black background
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - 128, 0, 256, 64);
            //Color gradient background
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - 128 + 4, 0 + 4, (256 - 4 * 2), 64 - 4 * 2, Color.GREEN, Color.RED, Color.RED, Color.GREEN);
            //Color gradient black mask
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 + 128 - 4, 0 + 4, -(256 - 4 * 2) * (1 - powerBarLevel), 64 - 4 * 2);
            shapeRenderer.end();
        }
    }

    public void dispose() {
        batch.dispose();
        font50.dispose();
    }

    public void addFlashMessage(String text, float timeToLive) {
        flashMessages.add(new FlashMessage(text, timeToLive, font50));
    }

    public boolean isPowerBarVisible() {
        return powerBarVisible;
    }

    public void setPowerBarVisible(boolean powerBarVisible) {
        this.powerBarVisible = powerBarVisible;
    }

    public float getPowerBarLevel() {
        return powerBarLevel;
    }

    public void setPowerBarLevel(float powerBarLevel) {
        this.powerBarLevel = powerBarLevel;
    }

    interface Hooks {

    }

    class FlashMessage {
        String text;
        float timeToLive;
        GlyphLayout glyphLayout;

        FlashMessage(String text, float timeToLive, BitmapFont font) {
            this.text = text;
            this.timeToLive = timeToLive;
            glyphLayout = new GlyphLayout(font, text);
        }
    }
}
