package com.group9.crazygolf.ui;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * crazygolf
 * 2016
 *
 * Aleksas Kateiva
 * Eric Chang
 * Adeline Mekic
 * Florian Kok
 * Roger Sijben
 */

public class FileSaver extends Dialog {

    private Skin skin;
    private FileHandle directory;
    private FileHandle file;
    private Label fileNameLabel;

    public TextField fileNameInput;
    public String result;
    public String fileName;

    public FileHandle getFile() {
        return file;
    }

    @Override
    public Dialog show(Stage stage) {
        return super.show(stage);
    }

    public void setDirectory(FileHandle directory) {
        if (this.directory != directory) {
            this.directory = directory;
            this.file = null;
            buildList();
        }
    }

    public void setFile(FileHandle file) {
        if (this.file != file) {
            if (this.file != null) {
                Label label = (Label) this.findActor(this.file.name());
                label.setColor(Color.WHITE);
            }
            Label label = (Label) this.findActor(file.name());
            label.setColor(Color.RED);
            this.file = file;
            fileName = file.toString();
            fileNameInput.setText(fileName);
        }
    }

    public String getFileName() {
        return fileNameInput.getText();
    }

    public FileSaver(String title, Skin skin) {
        super(title, skin);
        this.getCell(getButtonTable()).expandX().fill();
        this.getButtonTable().defaults().expandX().fill();

        this.button("Cancel", "Cancel");
        this.button("OK", "OK");

        this.setModal(true);
        this.skin = skin;
    }

    public FileSaver(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
        this.setModal(true);
        this.skin = skin;
    }

    private void buildList() {

        FileHandle[] files = directory.list();
        Arrays.sort(files, new Comparator<FileHandle>() {
            @Override
            public int compare(FileHandle o1, FileHandle o2) {
                if (o1.isDirectory() && !o2.isDirectory()) {
                    return -1;
                }
                if (o2.isDirectory() && !o1.isDirectory()) {
                    return +1;
                }
                return o1.name().compareToIgnoreCase(o2.name());
            }
        });
        ScrollPane pane = new ScrollPane(null, skin);
        Table table = new Table().top().left();
        table.defaults().left();
        ClickListener fileClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("File Button Clicked");
                Label target = (Label) event.getTarget();
                if (target.getName().equals("..")) {
                    setDirectory(directory.parent());
                } else {
                    FileHandle handle = directory.child(target.getName());
                    if (handle.isDirectory()) {
                        setDirectory(handle);
                    } else {
                        setFile(handle);
                    }
                }
            }
        };

        fileNameInput = new TextField("", skin);
        fileNameLabel = new Label("File Name:", skin);
        fileNameInput.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                result = textField.getText();
            }
        });
        table.row();
        table.add(fileNameLabel).left().padBottom(10);
        table.add(fileNameInput).left().padBottom(10).width(300);
        table.row();

        table.row();
        Label label = new Label("..", skin);
        label.setName("..");
        label.addListener(fileClickListener);
        table.add(label).expandX().fillX();
        for (FileHandle file : files) {
            table.row();
            label = new Label(file.name(), skin);
            label.setName(file.name());
            label.addListener(fileClickListener);
            table.add(label).expandX().fillX();
        }
        pane.setWidget(table);
        this.getContentTable().reset();
        this.getContentTable().add(pane).maxHeight(200).expand().fill();
    }

}
