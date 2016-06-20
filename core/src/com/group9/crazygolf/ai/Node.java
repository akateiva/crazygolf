package com.group9.crazygolf.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

/**
 * Created by Aspire on 6/14/2016.
 */
public class Node {
    int x, y;
    public int index;
    float tileX, tileY;
    Array<CustomConnection> cnc;

    public Node(int X, int Y, int i){
        x = X;
        y = Y;
        index = i;
    }

    public void setWorldCoor(float wX, float wY){
        //Maybe middle of a node idk
        tileX = wX;
        tileY = wY;
    }

    public Array<CustomConnection> getConnections(){
        return cnc;
    }
    public void setConnections(Array<CustomConnection> connect){
        cnc = new Array<CustomConnection>();
        cnc = connect;
    }
    public Node getNode(){
        return this;
    }

    public int getIndex(){
        return index;
    }

}
