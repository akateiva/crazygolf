package com.group9.crazygolf.menu;

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

    NewGameWindow(Skin skin, final crazygolf game) {
        super("New Game", skin);
        setModal(true);

        this.skin = skin;

        playerTable = new Table(skin);
        playerTable.top();


        add(playerTable).expandX();

        data = new NewGameData();

        row();
        TextButton addPlayerButton = new TextButton("Add player", skin);
        addPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addNewPlayer();
            }
        });
        add(addPlayerButton).expandX().pad(10);

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        add(cancelButton).expandX().pad(10);

        TextButton startButton = new TextButton("Start", skin);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, data));
                remove();
            }
        });
        add(startButton).expandX().pad(10);

        pack();
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
        pack();
    }

    private void addNewPlayer() {
        data.add();
        updatePlayerTable();
    }

}
