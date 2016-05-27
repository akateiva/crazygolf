package com.group9.crazygolf.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.group9.crazygolf.crazygolf;
import com.group9.crazygolf.game.GameScreen;
import com.group9.crazygolf.game.NewGameData;

/**
 * Created by akateiva on 21/05/16.
 */
class NewGameWindow extends Window {
    NewGameData data;
    Table playerTable;
    Skin skin;
    final MenuScreen screen;
    final Label textLabel;
    private boolean playersAdded = false;
    private boolean courseSelected = false;

    NewGameWindow(Skin skin, final crazygolf game, MenuScreen screen) {
        super("New Game", skin);
        this.setModal(true);
        this.skin = skin;
        this.setSize(400, 400);
        this.screen = screen;

        textLabel = new Label("Step 1: Add player(s)", skin);

        row();
        add(textLabel).left().padBottom(20);
        row().height(100);

        playerTable = new Table(skin);
        playerTable.top();

        add(playerTable);

        data = new NewGameData();

        row().height(30);

        int buttonWidth = 120;
        int padding = 60;

        // Define buttons
        TextButton addPlayerButton = new TextButton("Add player", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);
        TextButton startButton = new TextButton("Start", skin);
        TextButton courseButton = new TextButton("Select course", skin);

        // Add buttons
        add(addPlayerButton).left().width(buttonWidth).padRight(padding);
        add(courseButton).left().width(buttonWidth);
        row().height(30);
        add(cancelButton).left().width(buttonWidth).padTop(padding);
        add(startButton).left().width(buttonWidth).padTop(padding);

        // Add listeners
        addPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addNewPlayer();
            }
        });
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (playersAdded && courseSelected) {
                    game.setScreen(new GameScreen(game, data, Gdx.files.local("courses/assfuckery")));
                    remove();
                }
                if (playersAdded == false && courseSelected == false || playersAdded == false && courseSelected == true) {
                    textLabel.setText("Error: No player(s) added");
                }
                if (playersAdded == true && courseSelected == false) {
                    textLabel.setText("Error: No course selected");
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

    public void setCourseSelected() {
        courseSelected = true;
    }

    private void showFileChooser() {
        screen.chooseFile();
    }

    private void updatePlayerTable() {
        playerTable.clear();
        for (final NewGameData.Player ply : data.getPlayerList()) {
            final TextField nameField = new TextField(ply.name, skin);
            nameField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ply.name = nameField.getText();
                }
            });
            playerTable.add(nameField).pad(5).expandX().width(100);


            TextButton removePlayer = new TextButton("X", skin);
            removePlayer.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    data.getPlayerList().remove(ply);
                    updatePlayerTable();
                }
            });
            playerTable.add(removePlayer).pad(5);
            playerTable.row();
        }
        if (data.getPlayerList().size() == 0) {
            textLabel.setText("Step 1: Add player(s)");
            playersAdded = false;
        } else {
            textLabel.setText("Step 2: Select course");
            playersAdded = true;
        }
    }

    private void addNewPlayer() {
        data.add();
        updatePlayerTable();
    }

}
