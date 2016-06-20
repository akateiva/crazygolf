package com.group9.crazygolf.ai;


import com.badlogic.gdx.ai.pfa.Connection;

/**
 * Created by Aspire on 6/14/2016.
 */
public class CustomConnection implements Connection {
    Node fromNode;
    Node toNode;
    float cost;

    public CustomConnection(Node fNode, Node tNode, float Cost){
        fromNode = fNode;
        toNode = tNode;
        cost = Cost;
    }

    @Override
    public float getCost(){
        return cost;
    }

    @Override
    public Node getFromNode(){
        return fromNode;
    }

    @Override
    public Node getToNode(){
        return toNode;
    }

}
