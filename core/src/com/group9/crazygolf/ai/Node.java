package com.group9.crazygolf.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

/**
 * Created by Aspire on 6/14/2016.
 */
public class Node {
    int x, z, width, height;
    public int index;
    float worldX, worldZ, worldY;
    Array<CustomConnection> cnc;
    float gap;

    public Node(int X, int Z, int i, float Gap, int Width, int Height){
        x = X;
        z = Z;
        index = i;
        gap = Gap;
        width = Width;
        height = Height;
        //Store the middle position of the node in relation to the game world
        //If its an start/end pos, it stores the position of the start/end pos
        setWorldX();
        setWorldZ();
    }

    public Array<CustomConnection> getConnections(){
        return cnc;
    }
    public void setConnections(Array<CustomConnection> connect){
        cnc = new Array<CustomConnection>();
        cnc = connect;
    }
    public void setWorldX(){
        float cx = x*gap;
        float adjW = (width*gap)/2;
        float offset = gap/2;
        worldX = cx -adjW + offset;
    }

    public void setWorldZ(){
        float cz = z*gap;
        float adjH = (height*gap)/2;
        float offset = gap/2;
        worldZ = cz -adjH - offset;
    }
    public Node getNode(){
        return this;
    }

    public int getIndex(){
        return index;
    }

}
