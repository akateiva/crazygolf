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

    int width, height;
    boolean nodeGrid[][];

    public NodeState(int Width, int Height){
        width = Width;
        height = Height;
        nodeGrid = new boolean[width][height];
    }

    public boolean[][] getNodeGrid(){
        return nodeGrid;
    }

}
