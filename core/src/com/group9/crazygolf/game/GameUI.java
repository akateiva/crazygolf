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
 * Handles drawing game related user interface stuff
 */
public class GameUI {
    private SpriteBatch batch;
    private BitmapFont font32;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private LinkedList<FlashMessage> flashMessages;
    private boolean powerBarVisible = false;
    private float powerBarLevel = 1f;

    public GameUI() {
        batch = new SpriteBatch();
        FreeTypeFontGenerator freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Comic Sans MS.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        font32 = freeTypeFontGenerator.generateFont(parameter);
        font32.setColor(Color.YELLOW);
        freeTypeFontGenerator.dispose();

        flashMessages = new LinkedList<FlashMessage>();
    }


    public void update(float deltaTime) {
        batch.begin();

        float yOffset = 16f;
        Iterator<FlashMessage> itr = flashMessages.iterator();
        while (itr.hasNext()) {
            FlashMessage flashMessage = itr.next();
            flashMessage.timeToLive -= deltaTime;
            if (flashMessage.timeToLive < 0) {
                itr.remove();
                continue;
            }

            font32.draw(batch, flashMessage.text, Gdx.graphics.getWidth() / 2 - flashMessage.glyphLayout.width / 2, Gdx.graphics.getHeight() - yOffset);
            yOffset += flashMessage.glyphLayout.height + 8f;
        }
        batch.end();

        if (powerBarVisible) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            //Black background
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - 128, 0, 256, 64);
            //Color gradient background
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - 128 + 4, 4, (256 - 4 * 2), 64 - 4 * 2, Color.GREEN, Color.RED, Color.RED, Color.GREEN);
            //Color gradient black mask
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 + 128 - 4, 4, -(256 - 4 * 2) * (1 - powerBarLevel), 64 - 4 * 2);
            shapeRenderer.end();
        }
    }

    public void dispose() {
        batch.dispose();
        font32.dispose();
    }

    /**
     * Display a flash message on screen for a set amount of time.
     *
     * @param text       The text to be displayed.
     * @param timeToLive How long should the text be displayed for.
     */
    public void addFlashMessage(String text, float timeToLive) {
        flashMessages.add(new FlashMessage(text, timeToLive, font32));
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

    /**
     * FlashMessage is used for storing information about flash messages.
     * Flash messages are the things that appear in the top part of your screen and disappear after a set amount of time.
     */
    private class FlashMessage {
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
