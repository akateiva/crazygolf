package com.group9.crazygolf.ai;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.google.gson.Gson;
import com.group9.crazygolf.TrackingCameraController;
import com.group9.crazygolf.course.Course;
import com.sun.javafx.geom.Line2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aspire on 6/16/2016.
 */
public class PathTest implements Screen, InputProcessor {
    Camera cam;
    TrackingCameraController trackingCameraController;
    InputMultiplexer inputMux;
    Environment environment;
    ModelBuilder modelBuilder;
    ModelBatch modelBatch;
    ModelInstance grid, box;
    Engine engine;
    double gap;
    float gapSize;
    int width, height, sIndex, eIndex;
    Course course;
    List<Node> path;
    Mesh mesh;
    boolean[][] nodeGrid;
    ArrayList<Vector3> walls;
    ArrayList<Float> worldY = new ArrayList<Float>();
    ArrayList<Integer> iWorldY = new ArrayList<Integer>();
    Vector3 intersection3;
    Model boxx, bbox, wbox, ybox;
    float[] vertices;
    short[] indices;
    ArrayList<ModelInstance> mi = new ArrayList<ModelInstance>();
    CustomPoint startPos, endPos;
    ASPathFinder pF;

    Texture texture;
    ShaderProgram shader;
    String vertexShader = "attribute vec4 a_position;    \n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_worldView;\n" +
            "varying vec4 v_color;" +
            "varying vec2 v_texCoords;" +
            "void main()                  \n" +
            "{                            \n" +
            "   v_color = vec4(1, 1, 1, 1); \n" +
            "   v_texCoords = a_texCoord0; \n" +
            "   gl_Position =  u_worldView * a_position;  \n"      +
            "}                            \n" ;
    String fragmentShader = "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main()                                  \n" +
            "{                                            \n" +
            "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
            "}";

    public void create(){

        cam = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 0f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        trackingCameraController = new TrackingCameraController(cam);


        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        engine = new Engine();


        inputMux = new InputMultiplexer();
        inputMux.addProcessor(this);
        inputMux.addProcessor(trackingCameraController);

        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();

        //Make a 32x32 grid composed of 0.5x0.5 squares
        Model gridd = modelBuilder.createLineGrid(32, 32, 0.5f, 0.5f, new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        boxx = modelBuilder.createBox(0.4f, 0.01f, 0.4f,new Material(ColorAttribute.createDiffuse(Color.LIME)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        wbox = modelBuilder.createBox(0.1f, 0.01f, 0.1f,new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        bbox = modelBuilder.createBox(0.1f, 0.01f, 0.1f,new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ybox = modelBuilder.createBox(0.1f, 0.01f, 0.1f,new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        grid= new ModelInstance(gridd, 0,0,0);
        box = new ModelInstance(boxx, 0,0,0);

        width = 32;
        height = 32;
        gapSize = 0.5f;
        shader = new ShaderProgram(vertexShader, fragmentShader);
        FileHandle img = Gdx.files.internal("grass.jpg");
        texture = new Texture(img, Pixmap.Format.RGB565, false);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        texture.setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
    }

    public PathTest(FileHandle Course){
        //Load the course from a file
        Gson gson = new Gson();
        course = gson.fromJson(Course.readString(), Course.class);
        mesh = course.getTerrainMesh();
        indices = new short[mesh.getMaxIndices()];
        mesh.getIndices(indices);
        vertices = new float[mesh.getMaxVertices()];
        mesh.getVertices(vertices);
        //Optional I guess
        //flattenMesh();
        create();
        calcPath();
    }

    public void calcPath(){
        walls = getWallData(course);
        NodeState nodeState = new NodeState(width, height);
        nodeGrid = nodeState.getNodeGrid();

        //Set true values
        updateNodeGrid();

        pF = new ASPathFinder(width, height, nodeGrid, gapSize);
        pF.setWorldY(vertices, indices);
        pF.init();

        //Have to use indexes cant make a new custom point
        ArrayList<CustomPoint> CP = pF.getCC();
        for(int i=0;i<CP.size();i++){
            if(CP.get(i).x == startPos.x && CP.get(i).y == startPos.y){
                sIndex = i;
            }
            if(CP.get(i).x == endPos.x && CP.get(i).y == endPos.y){
                eIndex = i;
            }
        }
        path = pF.findPath(sIndex, eIndex);
        System.out.println(path.size()+"  PATH SIZE");
        //displayNodes();
        //Override worldX and worldY of start/end Nodes to hold accurate positions
        pF.setStartEndWorldCoor(course.getStartPosition(), course.getEndPosition());
        //only print if there if a path
        if(path.size()>0) {
            printRes();
        }

    }
    public void printRes(){
        float startX = path.get(0).worldX;
        float startY = path.get(0).worldY;
        float startZ = path.get(0).worldZ;

        ModelInstance pathStart = new ModelInstance(wbox, startX,startY+0.01f,startZ);
        mi.add(pathStart);

        for(int i=1;i<path.size()-1;i++){
            float x = path.get(i).worldX;
            float y = path.get(i).worldY;
            float z = path.get(i).worldZ;

            ModelInstance pathMiddle = new ModelInstance(ybox, x,y+0.01f,z);
            mi.add(pathMiddle);
        }

        float endX = path.get(path.size()-1).worldX;
        float endY = path.get(path.size()-1).worldY;
        float endZ = path.get(path.size()-1).worldZ;
        ModelInstance pathEnd = new ModelInstance(bbox, endX,endY+0.01f,endZ);
        mi.add(pathEnd);


    }
    public void updateNodeGrid(){
        for(int j=0;j<width;j++){
            for(int k=0;k<height;k++){
                checkInMesh(j, k);
                checkIntersectWall(j,k);
                OverrideStartEnd(j,k);
            }
        }
    }

    public void OverrideStartEnd(int nodeX, int nodeY){

        //Override the value of a start/end cell to true
        //as it has to be accessible

        Vector3 startN = course.getStartPosition();
        Vector3 endN = course.getEndPosition();
        float vecX = toWorldCoorX(nodeX);
        float vecY = toWorldCoorY(nodeY);

        //bottom left of node
        Vector2 p1 = new Vector2(vecX, vecY);
        //top left of node
        Vector2 p2 = new Vector2(vecX, vecY - gapSize);
        //top right of node
        Vector2 p3 = new Vector2(vecX + gapSize, vecY - gapSize);
        //bottom right of node
        Vector2 p4 = new Vector2(vecX + gapSize, vecY);

        if(startPos == null && startN.x>p1.x && startN.x<p4.x && startN.z< p1.y && startN.z>p2.y){
            nodeGrid[nodeX][nodeY] = true;
            startPos = new CustomPoint(nodeX, nodeY);
            //System.out.println("start pos"+nodeX+", "+nodeY);
        }
        if(endPos == null && endN.x>p1.x && endN.x<p4.x && endN.z<p1.y && endN.z>p2.y){
            nodeGrid[nodeX][nodeY] = true;
            endPos = new CustomPoint(nodeX, nodeY);
            //System.out.println("end pos"+nodeX+", "+nodeY);
        }

    }

    public void checkInMesh(int nodeX, int nodeY){
        //takes x,y of node and checks if it intersects the mesh
        float Xray = toWorldCoorX(nodeX);
        float Yray = toWorldCoorY(nodeY);
        Ray ray = new Ray(new Vector3(Xray, 1, Yray), new Vector3(0,-1,0));
        Vector3 intersec = new Vector3();
        Intersector.intersectRayPlane(ray, new Plane(new Vector3(0f, 1f, 0f), 0f), intersec);
        intersection3 = new Vector3();

        for (int i = 0; i < indices.length / 3; i++) {
            Vector3 t1 = new Vector3(vertices[i * 3 * 8], vertices[i * 3 * 8 + 1], vertices[i * 3 * 8 + 2]);
            Vector3 t2 = new Vector3(vertices[(i * 3 + 1) * 8], vertices[(i * 3 + 1) * 8 + 1], vertices[(i * 3 + 1) * 8 + 2]);
            Vector3 t3 = new Vector3(vertices[(i * 3 + 2) * 8], vertices[(i * 3 + 2) * 8 + 1], vertices[(i * 3 + 2) * 8 + 2]);
            if (Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection3)) {
                //System.out.println(intersection3.toString());
                nodeGrid[nodeX][nodeY] = true;
                break;
            }else{
                nodeGrid[nodeX][nodeY] = false;
            }
        }
    }


    public void checkIntersectWall(int nodeX, int nodeY){

        //only bother checking if the node is on the mesh
        //if its not then it can be intersecting any walls anyways
        if(nodeGrid[nodeX][nodeY] == true) {
            for (int i = 0; i < walls.size() / 2; i++) {
                float x1 = walls.get(i * 2).x;
                float z1 = walls.get(i * 2).z;
                float x2 = walls.get(i * 2 + 1).x;
                float z2 = walls.get(i * 2 + 1).z;

                float vecX = toWorldCoorX(nodeX);
                float vecY = toWorldCoorY(nodeY);

                //bottom left of node
                Vector2 p1 = new Vector2(vecX, vecY);
                //top left of node
                Vector2 p2 = new Vector2(vecX, vecY - gapSize);
                //top right of node
                Vector2 p3 = new Vector2(vecX + gapSize, vecY - gapSize);
                //bottom right of node
                Vector2 p4 = new Vector2(vecX + gapSize, vecY);

                Line2D wall = new Line2D(x1, z1, x2, z2);
                //bottom left to top left
                Line2D l1 = new Line2D(vecX, vecY, vecX, vecY - gapSize);
                //top left to to right
                Line2D l2 = new Line2D(vecX, vecY - gapSize, vecX + gapSize, vecY - gapSize);
                //top right to bottom right
                Line2D l3 = new Line2D(vecX + gapSize, vecY - gapSize, vecX + gapSize, vecY);
                //bottom right to bottom left
                Line2D l4 = new Line2D(vecX + gapSize, vecY, vecX, vecY);
                //top left to bottom right
                Line2D l5 = new Line2D(vecX, vecY - gapSize, vecX + gapSize, vecY);
                //bottom left to top right
                Line2D l6 = new Line2D(vecX, vecY, vecX + gapSize, vecY - gapSize);

                if(wall.intersectsLine(l1)){
                    nodeGrid[nodeX][nodeY] = false;
                }
                if(wall.intersectsLine(l2)){
                    nodeGrid[nodeX][nodeY] = false;
                }
                if(wall.intersectsLine(l3)){
                    nodeGrid[nodeX][nodeY] = false;
                }
                if(wall.intersectsLine(l4)){
                    nodeGrid[nodeX][nodeY] = false;
                }
                if(wall.intersectsLine(l5)){
                    nodeGrid[nodeX][nodeY] = false;
                }
                if(wall.intersectsLine(l6)){
                    nodeGrid[nodeX][nodeY] = false;
                }
            }
        }
    }

    public void displayNodes(){
        for(int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                if(nodeGrid[i][j]==true){
                    float X = toWorldCoorX(i);
                    float Y  = toWorldCoorY(j);
                    float offSet = gapSize/2;
                    //adj position so its in the middel of a node not bottom left pos
                    ModelInstance a = new ModelInstance(boxx,X+offSet,0,Y-offSet);
                    //ModelInstance a = new ModelInstance(boxx,Xray,0,Yray);
                    mi.add(a);
                }
            }
        }
    }

    public float toWorldCoorX(float fX){
        fX*=gapSize;
        float adjW = (width*gapSize)/2;
        return fX-adjW;
    }

    public float toWorldCoorY(float fY){
        fY*=gapSize;
        float adjH = (width*gapSize)/2;
        return fY-adjH;
    }

    public void flattenMesh(){
        //set y component of every vertex to 0
        int numVerts = mesh.getMaxVertices();
        float[] verts = new float[numVerts];
        mesh.getVertices(verts);
        for(int i=0;i<numVerts/8;i++){
            verts[i*8+1] =0;
        }
        mesh.setVertices(verts);
    }

    public ArrayList<Vector3> getWallData(Course c){
        ArrayList<Vector3> data = new ArrayList<Vector3>();
        for(int i=0;i<c.getbI().length;i++){
            data.add(c.getbI()[i].start);
            data.add(c.getbI()[i].end);
        }
        return data;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMux);
    }

    @Override
    public void render(float delta) {
        trackingCameraController.update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        texture.bind();
        shader.begin();
        shader.setUniformMatrix("u_worldView", cam.combined);
        shader.setUniformi("u_texture", 0);

        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();

        modelBatch.begin(cam);
        modelBatch.render(grid, environment);
        if(mi.size()>0){
            for(int i =0;i<mi.size();i++){
                modelBatch.render(mi.get(i), environment);
            }
        }
        //modelBatch.render(box, environment);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
