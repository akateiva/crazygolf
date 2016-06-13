package com.group9.crazygolf.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.group9.crazygolf.crazygolf;
import com.group9.crazygolf.game.GameScreen;
import com.group9.crazygolf.game.NewGameData;

/**
 * Created by akateiva / Roger on 12/06/16.
 */
class NewGameWindow extends Window {
    final MenuScreen screen;
    final Label textLabel;
    public FileHandle file;
    NewGameData data;
    Table playerTable, botTable;
    Skin skin;
    private boolean playersAdded = false, botsAdded = false;
    private boolean courseSelected = false;

    NewGameWindow(Skin skin, final crazygolf game, MenuScreen screen) {
        super("New Game", skin);
        this.setModal(true);
        this.skin = skin;
        this.setSize(500, 400);
        this.screen = screen;

        textLabel = new Label("Step 1: Add player(s)", skin);

        playerTable = new Table(skin);
        botTable = new Table(skin);
        Table tryTable = new Table(skin);
        Table table3 = new Table(skin);

        add(table3).left().row();
        add(playerTable).left().row();
        add(botTable).left().row();
        add(tryTable).left().row();

        table3.padBottom(30);
        playerTable.padBottom(0);
        botTable.padBottom(30);

        data = new NewGameData();

        int buttonWidth = 120, buttonHeight = 30;
        int padding = 30;

        // Define buttons
        TextButton addPlayerButton = new TextButton("Add player", skin);
        TextButton addAIButton = new TextButton("Add AI", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);
        TextButton startButton = new TextButton("Start", skin);
        TextButton courseButton = new TextButton("Select course", skin);

        // Add buttons
        table3.add(textLabel);
        tryTable.add(addPlayerButton).left().width(buttonWidth).height(buttonHeight).padRight(padding);
        tryTable.add(addAIButton).left().width(buttonWidth).height(buttonHeight);
        tryTable.add(courseButton).left().padLeft(padding).width(buttonWidth).height(buttonHeight);
        tryTable.row();
        tryTable.add(cancelButton).left().width(buttonWidth).height(buttonHeight).padTop(10);
        tryTable.add(startButton).left().width(buttonWidth).height(buttonHeight).padTop(10);

        // Add listeners
        addPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addNewPlayer();
            }
        });
        addAIButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addNewBot();
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
                    game.setScreen(new GameScreen(game, data, file));
                    remove();
                }
                textLabel.setText(returnError());
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

    public void setFile(FileHandle file) {
        this.file = file;
    }

    private void showFileChooser() {
        screen.chooseFile();
    }

    private void updatePlayerTable() {
        playerTable.clear();
        playerTable.row();
        int buttonWidth = 120, buttonHeight = 30;
        for (final NewGameData.Player ply : data.getPlayerList()) {
            final TextField nameField = new TextField(ply.name, skin);
            nameField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ply.name = nameField.getText();
                }
            });
            playerTable.add(nameField).width(buttonWidth).height(buttonHeight).padBottom(10);

            TextButton removePlayer = new TextButton("X", skin);

            removePlayer.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    data.getPlayerList().remove(ply);
                    updatePlayerTable();
                }
            });
            playerTable.add(removePlayer).width(25).height(buttonHeight).left().padLeft(30).padBottom(10);
            playerTable.row();
        }
        validateOptions();
    }

    private void updateBotTable() {
        botTable.clear();
        botTable.row();
        int buttonWidth = 120;
        int buttonHeight = 30;

        for (final NewGameData.Bot ply : data.getBotList()) {
            final TextField nameField = new TextField(ply.name, skin);
            final SelectBox selectBox = new SelectBox(skin);
            selectBox.addListener(new ChangeListener() {
                public void changed (ChangeEvent event, Actor actor) {
                    System.out.println(selectBox.getSelected());
                }
            });
            selectBox.setItems("Simple", "Average", "Clever");
            nameField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ply.name = nameField.getText();
                }
            });

            botTable.add(nameField).width(buttonWidth).height(buttonHeight).padBottom(10);
            botTable.add(selectBox).width(buttonWidth).height(buttonHeight).padBottom(10).padLeft(30);

            TextButton removeAI = new TextButton("X", skin);
            removeAI.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    data.getBotList().remove(ply);
                    updateBotTable();
                }
            });
            botTable.add(removeAI).width(25).height(buttonHeight).left().padLeft(30).padBottom(10);
            botTable.row();
        }
        validateOptions();
    }

    private void addNewPlayer() {
        data.addPlayer();
        updatePlayerTable();
    }

    private void addNewBot() {
        data.addBot();
        updateBotTable();
    }

    private void validateOptions() {
        if (data.getPlayerList().size() == 0) {
            textLabel.setText("Step 1: Add player(s)");
            playersAdded = false;
        } else {
            textLabel.setText("Step 2: Add AI Bot(s)");
            playersAdded = true;
        }
        if (data.getBotList().size() == 0) {
            textLabel.setText("Step 2: Add AI Bot(s)");
            botsAdded = false;
        } else {
            textLabel.setText("Step 3: Select course");
            botsAdded = true;
        }
    }

    private String returnError() {
        String error = "";
        if (courseSelected == false) {
            error = "Error: No course selected";
        }
        if (botsAdded == false) {
            error = "Error: No bot(s) added";
        }
        if (playersAdded == false) {
            error = "Error: No player(s) added";
        }
        return error;
    }

}
