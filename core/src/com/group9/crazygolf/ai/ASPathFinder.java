package com.group9.crazygolf.ai;

import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Aspire on 6/15/2016.
 */
public class ASPathFinder {
    int width, height, counter;
    boolean[][] notBlocked;
    IndexedAStarPathFinder pathFinder;
    private IndexedGraph graph;
    Heuristic<Node> heuristic;
    HashMap<CustomPoint, Node> allNodes;
    ArrayList<CustomPoint>CC;

    public ASPathFinder(int X, int Y, boolean[][] grid){
        width = X;
        height = Y;
        notBlocked = grid;
        init();
    }
    public ArrayList<CustomPoint> getCC(){
        return CC;
    }

    //For non-node points
    public List<Node> findPath(int indexStart, int indexEnd) {
        return findPath(allNodes.get(CC.get(indexStart)), allNodes.get(CC.get(indexEnd)));
    }

    public List<Node> findPath(Node startNode, Node endNode) {
        GraphPath resultPath = new DefaultGraphPath();
        PathFinderRequest request = new PathFinderRequest(startNode, endNode, heuristic, resultPath);
        request.statusChanged = true;
        boolean success = pathFinder.search(request, 1000 * 1000 * 1000);
        System.out.println("SUCCESSFUL:      "+success);
        List<Node> result = new ArrayList<Node>();
        Iterator iter = resultPath.iterator();
        while (iter.hasNext()) {
            Node node = (Node) iter.next();
            result.add(node);
        }
        return result;
    }

    public void init() {
        initAllNodes();
        initGraph();
        initHeuristic();
        pathFinder = new IndexedAStarPathFinder(graph);
    }

    private void initAllNodes() {
        allNodes = new HashMap<CustomPoint, Node>();
        int index = 0;
        CC = new ArrayList<CustomPoint>();

        //make nodes
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Node node = new Node(i, j, index++);
                CustomPoint CP = new CustomPoint(i,j);
                CC.add(CP);
                allNodes.put(CP, node);
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //TOP LEFT
                if (i == 0 && j == height - 1) {
                    int tIndex = j+i*32;
                    int index1 = j+(i+1)*32;
                    int index2 = (j-1)+(i)*32;
                    int index3 = (j-1)+(i+1)*32;
                    Node source = allNodes.get(CC.get(tIndex));
                    Array<CustomConnection> connections = new Array<CustomConnection>();
                    addConnection(connections, CC.get(tIndex), CC.get(index1));
                    addConnection(connections, CC.get(tIndex), CC.get(index2));
                    addConnection(connections, CC.get(tIndex), CC.get(index3));
                    source.setConnections(connections);
                    continue;
                }
                //TOP RIGHT
                if (i == width - 1 && j == height - 1) {
                    int tIndex = j+i*32;
                    int index1 = j+(i-1)*32;
                    int index2 = (j-1)+(i)*32;
                    int index3 = (j-1)+(i-1)*32;
                    Node source = allNodes.get(CC.get(tIndex));
                    Array<CustomConnection> connections = new Array<CustomConnection>();
                    addConnection(connections, CC.get(tIndex), CC.get(index1));
                    addConnection(connections, CC.get(tIndex), CC.get(index2));
                    addConnection(connections, CC.get(tIndex), CC.get(index3));
                    source.setConnections(connections);
                    continue;
                }
                //BOTTOM LEFT
                if (i == 0 && j == 0) {
                    int tIndex = j+i*32;
                    int index1 = j+(i+1)*32;
                    int index2 = (j+1)+(i)*32;
                    int index3 = (j+1)+(i+1)*32;
                    Node source = allNodes.get(CC.get(tIndex));
                    Array<CustomConnection> connections = new Array<CustomConnection>();
                    addConnection(connections, CC.get(tIndex), CC.get(index1));
                    addConnection(connections, CC.get(tIndex), CC.get(index2));
                    addConnection(connections, CC.get(tIndex), CC.get(index3));
                    source.setConnections(connections);
                    continue;
                }
                //BOTTOM RIGHT
                if (i == width - 1 && j == 0) {
                    int tIndex = j+i*32;
                    int index1 = j+(i-1)*32;
                    int index2 = (j+1)+(i)*32;
                    int index3 = (j+1)+(i-1)*32;
                    Node source = allNodes.get(CC.get(tIndex));
                    Array<CustomConnection> connections = new Array<CustomConnection>();
                    addConnection(connections, CC.get(tIndex), CC.get(index1));
                    addConnection(connections, CC.get(tIndex), CC.get(index2));
                    addConnection(connections, CC.get(tIndex), CC.get(index3));
                    source.setConnections(connections);
                    continue;
                }
                //TOP
                if(j == height -1 ){
                    int tIndex = j+i*32;
                    int index1 = j+(i-1)*32;
                    int index2 = (j-1)+(i-1)*32;
                    int index3 = (j)+(i+1)*32;
                    int index4 = (j-1)+(i+1)*32;
                    int index5 = (j-1)+(i)*32;
                    Node source = allNodes.get(CC.get(tIndex));
                    Array<CustomConnection> connections = new Array<CustomConnection>();
                    addConnection(connections, CC.get(tIndex), CC.get(index1));
                    addConnection(connections, CC.get(tIndex), CC.get(index2));
                    addConnection(connections, CC.get(tIndex), CC.get(index3));
                    addConnection(connections, CC.get(tIndex), CC.get(index4));
                    addConnection(connections, CC.get(tIndex), CC.get(index5));
                    source.setConnections(connections);
                    continue;
                }
                //BOTTOM
                if(j == 0){
                    int tIndex = j+i*32;
                    int index1 = j+(i-1)*32;
                    int index2 = (j+1)+(i-1)*32;
                    int index3 = (j)+(i+1)*32;
                    int index4 = (j+1)+(i+1)*32;
                    int index5 = (j+1)+(i)*32;
                    Node source = allNodes.get(CC.get(tIndex));
                    Array<CustomConnection> connections = new Array<CustomConnection>();
                    addConnection(connections, CC.get(tIndex), CC.get(index1));
                    addConnection(connections, CC.get(tIndex), CC.get(index2));
                    addConnection(connections, CC.get(tIndex), CC.get(index3));
                    addConnection(connections, CC.get(tIndex), CC.get(index4));
                    addConnection(connections, CC.get(tIndex), CC.get(index5));
                    source.setConnections(connections);
                    continue;
                }
                //LEFT
                if(i == 0){
                    int tIndex = j+i*32;
                    int index1 = (j-1)+(i)*32;
                    int index2 = (j-1)+(i+1)*32;
                    int index3 = (j+1)+(i)*32;
                    int index4 = (j+1)+(i+1)*32;
                    int index5 = (j)+(i+1)*32;
                    Node source = allNodes.get(CC.get(tIndex));
                    Array<CustomConnection> connections = new Array<CustomConnection>();
                    addConnection(connections, CC.get(tIndex), CC.get(index1));
                    addConnection(connections, CC.get(tIndex), CC.get(index2));
                    addConnection(connections, CC.get(tIndex), CC.get(index3));
                    addConnection(connections, CC.get(tIndex), CC.get(index4));
                    addConnection(connections, CC.get(tIndex), CC.get(index5));
                    source.setConnections(connections);
                    continue;
                }
                //RIGHT
                if(i ==  width - 1){
                    int tIndex = j+i*32;
                    int index1 = (j-1)+(i)*32;
                    int index2 = (j-1)+(i-1)*32;
                    int index3 = (j+1)+(i)*32;
                    int index4 = (j+1)+(i-1)*32;
                    int index5 = (j)+(i-1)*32;
                    Node source = allNodes.get(CC.get(tIndex));
                    Array<CustomConnection> connections = new Array<CustomConnection>();
                    addConnection(connections, CC.get(tIndex), CC.get(index1));
                    addConnection(connections, CC.get(tIndex), CC.get(index2));
                    addConnection(connections, CC.get(tIndex), CC.get(index3));
                    addConnection(connections, CC.get(tIndex), CC.get(index4));
                    addConnection(connections, CC.get(tIndex), CC.get(index5));
                    source.setConnections(connections);
                    continue;
                }
                //MIDDLE
                int tIndex = j+i*32;
                int index1 = (j-1)+(i-1)*32;
                int index2 = (j)+(i-1)*32;
                int index3 = (j+1)+(i-1)*32;
                int index4 = (j+1)+(i)*32;
                int index5 = (j-1)+(i)*32;
                int index6 = (j+1)+(i+1)*32;
                int index7 = (j-1)+(i+1)*32;
                int index8 = (j)+(i+1)*32;
                Node source = allNodes.get(CC.get(tIndex));
                Array<CustomConnection> connections = new Array<CustomConnection>();
                addConnection(connections, CC.get(tIndex), CC.get(index1));
                addConnection(connections, CC.get(tIndex), CC.get(index2));
                addConnection(connections, CC.get(tIndex), CC.get(index3));
                addConnection(connections, CC.get(tIndex), CC.get(index4));
                addConnection(connections, CC.get(tIndex), CC.get(index5));
                addConnection(connections, CC.get(tIndex), CC.get(index6));
                addConnection(connections, CC.get(tIndex), CC.get(index7));
                addConnection(connections, CC.get(tIndex), CC.get(index8));
                source.setConnections(connections);
                continue;
            }
        }
    }

    public void initGraph() {
        graph = new IndexedGraph() {


            @Override
            public int getIndex(Object node) {
                    Node n = (Node) node;
                    return n.index;
            }

            @Override
            public int getNodeCount() {
                return width*height;
            }

            @Override
            public Array getConnections(Object n) {
                if (n.getClass().isAssignableFrom(Node.class)) {
                    return ((Node) n).getConnections();
                }
                return null;
            }
        };
    }

    public void initHeuristic() {
        heuristic = new Heuristic<Node>() {
            @Override
            public float estimate(Node startNode, Node endNode) {
                return Math.max(Math.abs(startNode.tileX - endNode.tileX), Math.abs(startNode.tileY - endNode.tileY));
            }
        };
    }

    public void addConnection(Array<CustomConnection> connections, CustomPoint from, CustomPoint to) {
        float cost = 2;
        if ((from.x == to.x && from.y != to.y) || (from.x != to.x && from.y == to.y)) {
            cost = 1;
        }
        Node fromNode = allNodes.get(from);
        Node toNode = allNodes.get(to);
        if(toNode == null){
            counter++;
            System.out.println(counter);
        }
        if (notBlocked[to.x][to.y]) {
            connections.add(new CustomConnection(fromNode, toNode, cost));
            //System.out.println(from.x + ", " + from.y + "     " + to.x + ", " + to.y);
        }
    }
}
