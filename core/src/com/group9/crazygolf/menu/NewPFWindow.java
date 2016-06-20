package com.group9.crazygolf.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.group9.crazygolf.ai.PathTest;
import com.group9.crazygolf.crazygolf;
import com.group9.crazygolf.game.GameScreen;
import com.group9.crazygolf.game.NewGameData;
import com.group9.crazygolf.menu.MenuScreen;

/**
 * Created by Aspire on 6/17/2016.
 */
public class NewPFWindow extends Window {
    final MenuScreen screen;
    public FileHandle file;
    NewGameData data;
    Skin skin;
    private boolean courseSelected = false;

    public NewPFWindow(Skin skin, final crazygolf game, MenuScreen screen) {
        super("A* Path Finder", skin);
        this.setModal(true);
        this.skin = skin;
        this.setSize(400, 85);
        this.screen = screen;

        Table tryTable = new Table(skin);
        add(tryTable).center();

        data = new NewGameData();

        int buttonWidth = 120, buttonHeight = 30;


        // Define buttons
        TextButton cancelButton = new TextButton("Cancel", skin);
        TextButton startButton = new TextButton("Start", skin);
        TextButton courseButton = new TextButton("Select course", skin);

        // Add buttons
        tryTable.add(courseButton).left().width(buttonWidth).height(buttonHeight);
        //tryTable.row();
        tryTable.add(cancelButton).left().width(buttonWidth).height(buttonHeight);
        tryTable.add(startButton).left().width(buttonWidth).height(buttonHeight);

        // Add listeners
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (courseSelected) {
                    game.setScreen(new PathTest(file));
                    remove();
                }
            }
        });
        courseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        screen.chooseFilePF();
    }

    public void setCourseSelected() {
        courseSelected = true;
    }

    public void setFile(FileHandle file) {
        this.file = file;
    }

}
