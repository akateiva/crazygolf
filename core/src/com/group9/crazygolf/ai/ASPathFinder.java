package com.group9.crazygolf.ai;

import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
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
    int startIndex, endIndex;
    float gap;
    ArrayList<Vector3> norms;

    public ASPathFinder(int X, int Y, boolean[][] grid, float gapSize, ArrayList<Vector3> Norms){
        width = X;
        height = Y;
        notBlocked = grid;
        gap = gapSize;
        norms = Norms;
        allNodes = new HashMap<CustomPoint, Node>();
        int index = 0;
        CC = new ArrayList<CustomPoint>();

        //make nodes
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Node node = new Node(i, j, index++, gap, width, height);
                CustomPoint CP = new CustomPoint(i,j);
                CC.add(CP);
                allNodes.put(CP, node);
            }
        }
    }
    public ArrayList<CustomPoint> getCC(){
        return CC;
    }

    //For non-node points
    public List<Node> findPath(int indexStart, int indexEnd) {
        startIndex = indexStart;
        endIndex = indexEnd;
        return findPath(allNodes.get(CC.get(indexStart)), allNodes.get(CC.get(indexEnd)));
    }

    public List<Node> findPath(Node startNode, Node endNode) {
        GraphPath resultPath = new DefaultGraphPath();
        PathFinderRequest request = new PathFinderRequest(startNode, endNode, heuristic, resultPath);
        request.statusChanged = true;
        boolean success = pathFinder.search(request, 1000 * 1000 * 1000);
        //System.out.println("SUCCESSFUL:      "+success);
        List<Node> result = new ArrayList<Node>();
        Iterator iter = resultPath.iterator();
        while (iter.hasNext()) {
            Node node = (Node) iter.next();
            result.add(node);
        }
        return result;
    }

    public void setStartEndWorldCoor(Vector3 startVec, Vector3 endVec, Vector3 strNorm, Vector3 endNorm){
        allNodes.get(CC.get(startIndex)).worldX = startVec.x;
        allNodes.get(CC.get(startIndex)).worldY = startVec.y;
        allNodes.get(CC.get(startIndex)).worldZ = startVec.z;
        allNodes.get(CC.get(startIndex)).normal = strNorm;

        allNodes.get(CC.get(endIndex)).worldX = endVec.x;
        allNodes.get(CC.get(endIndex)).worldY = endVec.y;
        allNodes.get(CC.get(endIndex)).worldZ = endVec.z;
        allNodes.get(CC.get(endIndex)).normal = endNorm;

    }

    public void setWorldY(float[] vertices, short[] indices){
        for(int a=0;a<width;a++){
            for(int b=0;b<height;b++){
                float Xray = toWorldCoorX(a);
                float Yray = toWorldCoorY(b);
                float offset = gap/2;
                Ray ray = new Ray(new Vector3(Xray+offset, 10, Yray-offset), new Vector3(0,-10,0));
                Vector3 intersection3 = new Vector3();
                for (int i = 0; i < indices.length / 3; i++) {
                    Vector3 t1 = new Vector3(vertices[i * 3 * 8], vertices[i * 3 * 8 + 1], vertices[i * 3 * 8 + 2]);
                    Vector3 t2 = new Vector3(vertices[(i * 3 + 1) * 8], vertices[(i * 3 + 1) * 8 + 1], vertices[(i * 3 + 1) * 8 + 2]);
                    Vector3 t3 = new Vector3(vertices[(i * 3 + 2) * 8], vertices[(i * 3 + 2) * 8 + 1], vertices[(i * 3 + 2) * 8 + 2]);
                    Vector3 thisNorm = norms.get(i).nor();
                    if (Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection3)) {
                        int index = a*32+b;
                        if(notBlocked[a][b] == true){
                            allNodes.get(CC.get(index)).worldY = intersection3.y;
                            allNodes.get(CC.get(index)).normal = thisNorm;
                        }
                        break;
                    }
                }
            }
        }
    }

    public float toWorldCoorX(float fX){
        fX*= gap;
        float adjW = (width*gap)/2;
        return fX-adjW;
    }

    public float toWorldCoorY(float fY){
        fY*=gap;
        float adjH = (width*gap)/2;
        return fY-adjH;
    }

    public void init() {
        initAllNodes();
        initGraph();
        initHeuristic();
        pathFinder = new IndexedAStarPathFinder(graph);
    }

    private void initAllNodes() {

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
                //return Math.max(Math.abs(startNode.x - endNode.x), Math.abs(startNode.z - endNode.z));
                return Math.max(Math.max(Math.abs(startNode.worldX - endNode.worldX),
                        Math.abs(startNode.worldZ - endNode.worldZ)), Math.abs(startNode.worldY- endNode.worldY));
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

        //This will make the path avoid  any slopes if possible
        if(fromNode.worldY!=toNode.worldY){
            cost+=10;
        }

        if (notBlocked[to.x][to.y]) {
            connections.add(new CustomConnection(fromNode, toNode, cost));
        }
    }
}
