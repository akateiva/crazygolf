package com.group9.crazygolf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by akateiva on 26/05/16.
 */
public class Course {
    public Vector3 startPosition;
    public Vector3 endPosition;
    public Mesh terrainMesh;
    String name;

    public Course(String name) {
        this.name = name;

        startPosition = new Vector3(69, 32, 10);
        endPosition = new Vector3(69, 32, 32);
    }

    public void export() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        System.out.println(gson.toJson(this));
        Gdx.files.local("courses/" + name).writeString(gson.toJson(this), false);
    }
}
