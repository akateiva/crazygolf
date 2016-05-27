package com.group9.crazygolf.course;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * IF YOU WANT TO EXPORT A COURSE
 * ITS REAL EASY TO DO
 * JUST FOLLOW THESE STEPS
 * ASS MONEY DRUGS ASS MONEY DRUGS
 * <p>
 * Use exposed setter methods to set:
 * startPosition
 * endPosition
 * terrainMesh
 */
public class Course {
    private Vector3 startPosition;
    private Vector3 startNormal;
    private Vector3 endPosition;
    private Vector3 endNormal;

    private float[] terrainVertexArray;
    private short[] terrainIndexArray;
    private ArrayList bI;

    private String name;

    /**
     * Create a new course for exporting
     *
     * @param name the name of the course
     */
    public Course(String name) {
        this.name = name;
    }

    /**
     * Exports this course as a json file ( /courses/course name)
     */
    public void export() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        System.out.println(gson.toJson(this));
        Gdx.files.local("courses/" + name).writeString(gson.toJson(this), false);
    }

    public Vector3 getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Vector3 endPosition) {
        this.endPosition = endPosition;
    }

    public Vector3 getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Vector3 startPosition) {
        this.startPosition = startPosition;
    }

    public Vector3 getEndNormal() {
        return endNormal;
    }

    public void setEndNormal(Vector3 endNormal) {
        this.endNormal = endNormal;
    }

    public Vector3 getStartNormal() {
        return startNormal;
    }

    public void setStartNormal(Vector3 startNormal) {
        this.startNormal = startNormal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Mesh getTerrainMesh() {
        VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position(), VertexAttribute.TexCoords(0), VertexAttribute.Normal());
        Mesh mesh = new Mesh(true, terrainVertexArray.length, terrainIndexArray.length, attributes);
        mesh.setVertices(terrainVertexArray);
        mesh.setIndices(terrainIndexArray);
        return mesh;
    }

    public void setTerrainMesh(Mesh terrainMesh) {
        terrainIndexArray = new short[terrainMesh.getNumIndices()];
        terrainMesh.getIndices(terrainIndexArray);
        terrainVertexArray = new float[terrainMesh.getNumVertices() * (terrainMesh.getVertexSize() / 4)];
        System.out.printf("Vertice num %s vertex size %s float size %s", terrainMesh.getNumVertices(), terrainMesh.getVertexSize(), 4);
        terrainMesh.getVertices(terrainVertexArray);
    }

    public void setWalls(ArrayList<BoundInfo> bdInfo){
        bI = bdInfo;
    }
}
