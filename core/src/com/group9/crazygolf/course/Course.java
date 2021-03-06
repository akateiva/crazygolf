package com.group9.crazygolf.course;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Course {
    private Vector3 startPosition;
    private Vector3 startNormal;
    private Vector3 endPosition;
    private Vector3 endNormal;
    private ArrayList<Vector3> norms;

    private float[] terrainVertexArray;
    private short[] terrainIndexArray;
    private BoundInfo[] bI;

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
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
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
        //bI = (BoundInfo[]) bdInfo.toArray();
        bI = bdInfo.toArray(new BoundInfo[bdInfo.size()]);
        System.out.println(bI.length);
    }

    public void setNorms(ArrayList<Vector3> Norms){
        norms = Norms;
    }

    public ArrayList<Vector3> getNorms(){
        return norms;
    }


    public BoundInfo[] getbI() {
        return bI;
    }
}
