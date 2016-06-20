package com.group9.crazygolf.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.group9.crazygolf.course.Course;

import java.util.ArrayList;

/**
 * Created by Aspire on 6/15/2016.
 */
public class NodeState {
    Vector2 p1, p2, p3, p4,intersection;
    Vector3 intersection3;
    Mesh mesh;
    int width, height;
    Camera cam;
    short[] indices;
    float[] vertices;
    boolean nodeGrid[][];
    float gapSize;
    ArrayList<Vector3> walls;
    Course course;
    CustomPoint startPos, endPos;

    public NodeState(Course Course, int Width, int Height, double Gap, ArrayList<Vector3> Walls){
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        course = Course;
        mesh = Course.getTerrainMesh();
        width = Width;
        height = Height;
        gapSize = (float) Gap;
        walls = Walls;
        vertices = new float[mesh.getMaxVertices()];
        mesh.getVertices(vertices);
        nodeGrid = new boolean[width][height];
    }

    public boolean[][] getNodeGrid(){
        return nodeGrid;
    }

}
