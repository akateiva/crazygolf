package com.group9.crazygolf.coursedesginer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ShortArray;
import com.group9.crazygolf.course.BoundInfo;
import com.group9.crazygolf.course.Course;
import com.group9.crazygolf.crazygolf;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by akateiva on 11/05/16.
 */
public class CourseDesignerScreen implements Screen, InputProcessor {
    crazygolf game;
    Mode mode;
    Engine engine;
    PerspectiveCamera cam;
    InputMultiplexer inputMux;
    Stage stage;
    int dragY, listSize, index, dragX, U, V, outerCount, counter, realINDsize;
    float[] vertList = new float[0];
    float[] newVertList = new float[0];
    float[] obsCoords = new float[6];
    ModelBatch modelBatch;
    ModelBuilder modelBuilder;
    ModelInstance instance, instance2, vPosInst, normArrow;
    Model model, grid;
    Model vertexPos, vertPos, sphere;
    Environment environment;
    Mesh mesh;
    ArrayList<ModelInstance> vertices, positions, boundary, walls, normArrows;
    ArrayList<Float> boundAngles, borderDist;
    ArrayList<Integer> Indexes, vertPointers;
    ArrayList<Vector3> borderPos, innerVec, triNorms;
    ArrayList<Integer>onlyOuter;
    ArrayList<BoundInfo> boundInfo;
    boolean ctrlPressed, moveMouse,bool,runOnce = true, startSet, endSet, showBounds;
    boolean outerMode, hideVerts, obstacle, uvSet, onceArrow;
    Robot robot;
    Vector3 intersection2, startPos, endPos, strNorm, endNorm;
    short[] indices;
    Texture texture;
    ShaderProgram shader;
    float lowest, highest;

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

    public CourseDesignerScreen(crazygolf game) {
        this.game = game;
        stage = new Stage();
        engine = new Engine();
        mode = Mode.DO_NOTHING;
        vertices = new ArrayList<ModelInstance>();
        positions = new ArrayList<ModelInstance>();
        boundary = new ArrayList<ModelInstance>();
        normArrows = new ArrayList<ModelInstance>();
        walls = new ArrayList<ModelInstance>();
        Indexes = new ArrayList<Integer>();
        triNorms = new ArrayList<Vector3>();
        boundAngles = new ArrayList<Float>();
        borderDist = new ArrayList<Float>();
        vertPointers = new ArrayList<Integer>();
        borderPos = new ArrayList<Vector3>();
        innerVec = new ArrayList<Vector3>();
        onlyOuter = new ArrayList<Integer>();
        boundInfo = new ArrayList<BoundInfo>();

        /* Set up the camera */
        cam = new PerspectiveCamera(35, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 0f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;

        ctrlPressed = false;
        createUI();
        shader = new ShaderProgram(vertexShader, fragmentShader);
        FileHandle img = Gdx.files.internal("grass.jpg");
        texture = new Texture(img, Pixmap.Format.RGB565, false);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        texture.setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        //Because we want to check for events on an UI as well as clicks in the world, we must create an input multiplexer
        //Inputs will processed in the UI first, and if there are no events ( i.e. mouseDown returns false, then that that event is passed down to CourseDesignScreen event processor)
        inputMux = new InputMultiplexer();
        inputMux.addProcessor(stage);
        inputMux.addProcessor(this);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();

        //Making a random mesh so it won't be null
        mesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
        mesh.setVertices(new float[]{0f, 0f, 0f,0f, 0f, 0f,0f, 0f, 0f,0f, 0f, 0f,});
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0,});
        Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        modelBuilder.begin();
        modelBuilder.part("Temp", mesh, GL20.GL_TRIANGLES, material);
        model = modelBuilder.end();
        instance = new ModelInstance(model, 0,0,0);

        //Adding random model to ArrayList so it won't be null
        vertPos = modelBuilder.createBox(0, 0, 0, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        vPosInst = new ModelInstance(vertPos,0,0,0);
        vertices.add(vPosInst);
        positions.add(vPosInst);positions.add(vPosInst);
        //Test sphere, no idea what to put for u, v
        sphere = modelBuilder.createSphere(0.15f,0.15f,0.15f,20,20, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        //Make a cube model
        vertexPos = modelBuilder.createBox(0.15f, 0.15f, 0.15f, new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        //Make a 32x32 grid composed of 0.5x0.5 squares
        grid = modelBuilder.createLineGrid(32, 32, 0.5f, 0.5f, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance2 = new ModelInstance(grid, 0,-1f,0);
    }

    public void liftVertex(float[]liftTarget, int screenY, boolean resHeight, int index){
        if (resHeight){
            vertList[index]=0;
            updateBorders();
        }else {
            for (int i = 0; i < Indexes.size(); i++) {
                //Drop vertex down if mouse moves down
                if (screenY > dragY && !ctrlPressed && vertList[Indexes.get(i)] > -1f) {
                    vertList[Indexes.get(i)] -= 0.05;

                }
                //Lift vertex up if mouse moves up
                if (screenY < dragY && !ctrlPressed && vertList[Indexes.get(i)] < 2f) {
                    vertList[Indexes.get(i)] += 0.05;
                }
                //Update cube position
                int cubeIndex = ((Indexes.get(i)-1) / 8);
                Vector3 temp = new Vector3(vertList[Indexes.get(i)-1], vertList[Indexes.get(i)], vertList[Indexes.get(i)+1]);
                ModelInstance newVertPos = new ModelInstance(sphere, temp);
                //need to clear first
                vertices.set(cubeIndex, newVertPos);
            }
        }
        Indexes.clear();
        mesh.setVertices(vertList);
        dragY = screenY;
    }

    public void resizeArray(float[] oldVertList) {
        // create a new array of size+3
        int newSize = oldVertList.length + 3;
        float[] newArray = new float[newSize];
        System.arraycopy(oldVertList, 0, newArray, 0, oldVertList.length);
        vertList = newArray;
    }

    public void simClick(int i){
        try{
            robot = new Robot();
        }
         catch(AWTException e){
             e.printStackTrace();
         }
        setInput(i);
        //Simulate mouse click in the middle of screen to get mesh to render
        robot.delay(5);
        robot.mousePress(MouseEvent.BUTTON1_MASK);
        robot.mouseRelease(MouseEvent.BUTTON1_MASK);
    }

    public void makeMesh1(){
        //Getting Indices from x,z coords of vertices
        DelaunayTriangulator dt = new DelaunayTriangulator();
        ShortArray meshIndices = dt.computeTriangles(newVertList, false);

        indices = new short[meshIndices.size];
        for (int i = 0; i < meshIndices.size; i++) {
            indices[i] = meshIndices.get(i);
        }
        int length = vertList.length;

        //Constructing mesh
        mesh = new Mesh(true, length, indices.length,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"),
                new VertexAttribute(VertexAttributes.Usage.Normal,3, ShaderProgram.NORMAL_ATTRIBUTE));

        addTextCoor(vertList);
        mesh.setVertices(vertList);
        mesh.setIndices(indices);
        createNormMesh();
        remakeVerts();
    }
    public void remakeVerts(){
        //Clear vertSphere list and recreate spheres for all positions after repeats are set
        hideVerts = true;
        vertices.clear();
        for(int i=0;i<vertList.length/8;i++) {
            Vector3 temp = new Vector3(vertList[i*8], vertList[i*8+1], vertList[i*8+2]);
            ModelInstance newVertPos = new ModelInstance(sphere, temp);
            //need to clear first
            vertices.add(newVertPos);
        }
        hideVerts = false;
    }

    public void updateMesh(){
        mesh = new Mesh(true, vertList.length, indices.length,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"),
                new VertexAttribute(VertexAttributes.Usage.Normal,3, ShaderProgram.NORMAL_ATTRIBUTE));
        mesh.setVertices(vertList);
        mesh.setIndices(indices);
        Material material = new Material(ColorAttribute.createAmbient(Color.GREEN));
        modelBuilder.begin();
        modelBuilder.part("Course", mesh, GL20.GL_TRIANGLES, material);
        model = modelBuilder.end();
        instance = new ModelInstance(model, 0, 0, 0);
        uvSet = true;
    }

    public void newCourseDesigner() {
        game.setScreen(new CourseDesignerScreen(game));
    }

    public void addTextCoor(float[] array){

        //Make new float[] with size for textCoords
        float[] newArray = new float[array.length/3*2+array.length];
        //copy stuff from old array to new array in the right places
        for(int i=0; i<array.length/3;i++){
            newArray[i*5] = array[i*3];
            newArray[i*5+1] = array[i*3+1];
            newArray[i*5+2] = array[i*3+2];
        }

        float[] newTemp = new  float[newArray.length/5*3+newArray.length];

        for(int i=0;i<newArray.length/5;i++){
            newTemp[i*8]=newArray[i*5];
            newTemp[i*8+1]=newArray[i*5+1];
            newTemp[i*8+2]=newArray[i*5+2];
            newTemp[i*8+3]=newArray[i*5+3];
            newTemp[i*8+4]=newArray[i*5+4];
        }

        for(int i=0;i<newTemp.length/8;i++){
            newTemp[i*8+5]=0;
            newTemp[i*8+6]=1;
            newTemp[i*8+7]=1;
        }
        vertList = newTemp;
        setUV(vertList);

    }

    public void setUV(float[] newArray){
        float u = ((float) Gdx.graphics.getWidth()) / texture.getWidth()/7;
        float v = ((float) Gdx.graphics.getHeight()) / texture.getHeight()/7;
        for(int i=0;i<vertList.length/8;i++){
            newArray[i*8+3] = u*newArray[i*8];
            newArray[i*8+4] = -v*newArray[i*8+2];
        }
    }

    public void setInput(int i){
        if(i == 0) {
            Gdx.input.setInputProcessor(this);
        }
        if(i == 1) {
            Gdx.input.setInputProcessor(inputMux);
        }
    }

    public boolean checkLiftVertex(float[] liftTarget){
        Indexes.clear();
        boolean boole = false;
        //Check if input matches location of a vertex within error bound of +-0.3f
        for (int i=0;i<vertList.length/8;i++){
            if (liftTarget[0]<vertList[i*8]+0.3&&liftTarget[0]>vertList[i*8]-0.3&&
                    liftTarget[2]<vertList[i*8+2]+0.3&&liftTarget[2]>vertList[i*8+2]-0.3){
                    Indexes.add(i*8+1);
                    boole =  true;
            }
        }
        return boole;
    }

    private void createUI() {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Window window = new Window("Tools", skin);

        TextButton addOutVertexButton = new TextButton("Add Outer Vertex", skin);
        addOutVertexButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                if(mode == Mode.DO_NOTHING) {
                    mode = Mode.POINT_EDITOR;
                    outerMode = true;
                }
            }
        });
        window.add(addOutVertexButton);
        TextButton addInVertexButton = new TextButton("Add Inner Vertex", skin);
        addInVertexButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                outerMode = false;
            }
        });
        window.add(addInVertexButton);
        TextButton changeElevationButton = new TextButton("Change Elevation", skin);
        changeElevationButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                if(mode != Mode.DO_NOTHING&&vertList.length>0) {
                    if (!moveMouse) {
                        simClick(0);
                        moveMouse = true;
                    }
                    //Replace any set start/end points
                    positions.set(0, vPosInst);
                    positions.set(1, vPosInst);
                    mode = Mode.ELEVATION_EDITOR;
                    obstacle = false;
                    walls.clear();
                    setBoundInfo(true, 0f,0f,0f,null);
                    counter = 0;
                    return true;
                }
                return true;
            }
        });
        window.add(changeElevationButton);
        TextButton setStartPos = new TextButton("Set Start Pos", skin);
        setStartPos.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                if(vertList.length>0) {
                    mode = Mode.SET_START;
                }
                return true;
            }
        });
        window.add(setStartPos);
        TextButton setEndPos = new TextButton("Set End Pos", skin);
        setEndPos.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                if(vertList.length>0) {
                    mode = Mode.SET_END;
                    counter = 0;
                }
                return true;
            }
        });
        window.add(setEndPos);
        TextButton toggleBounds = new TextButton("Toggle Boundaries", skin);
        toggleBounds.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                //updateBorders();
                showBounds = showBounds == false && mode != Mode.POINT_EDITOR;
                counter =0;
                return true;
            }});
        window.add(toggleBounds);

        TextButton toggleVerts = new TextButton("Toggle Vertices", skin);
        toggleVerts.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                hideVerts = !hideVerts;
                counter =0;
                return true;
            }});
        window.add(toggleVerts);

        TextButton resetHeight = new TextButton("Reset Height", skin);
        resetHeight.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                if(mode != Mode.DO_NOTHING &&mode != Mode.POINT_EDITOR) {
                    for (int i = 0; i < vertList.length / 8; i++) {
                        float[] target = new float[3];
                        target[0] = vertList[i * 8 + 1];
                        target[1] = vertList[i * 8 + 2];
                        target[2] = vertList[i * 8 + 3];
                        liftVertex(target, 0, true, i * 8 + 1);
                    }
                    if (vertList.length > 0) {
                        for (int i = 0; i < vertices.size(); i++) {
                            int cubeIndex = i;
                            Vector3 temp = new Vector3(vertList[i * 8], 0, vertList[i * 8 + 2]);
                            ModelInstance newVertPos = new ModelInstance(sphere, temp);
                            vertices.set(cubeIndex, newVertPos);
                        }
                    }
                    //Replace any set start/end points
                    positions.set(0, vPosInst);
                    positions.set(1, vPosInst);
                    obstacle = false;
                    walls.clear();
                    setBoundInfo(true, 0f,0f,0f,null);
                    counter = 0;
                    mode = Mode.ELEVATION_EDITOR;
                    if(indices.length>0) {
                        calcNorm();
                    }
                }
                return true;
            }});
        window.add(resetHeight);
        TextButton ResetButton = new TextButton("Reset", skin);
        ResetButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                newCourseDesigner();
            }
        });
        window.add(ResetButton);
        TextButton obstaclesButton = new TextButton("Add Obstacles", skin);
        obstaclesButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent e,float x, float y, int point, int button){
                if(mode!=Mode.DO_NOTHING&&mode!=Mode.POINT_EDITOR) {
                    mode = Mode.SET_OBSTACLES;
                }
                counter =0;
                return true;
        }});
        window.add(obstaclesButton);
        TextButton MainMenu = new TextButton("Main Menu", skin);
        MainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                game.showPauseMenu();

            }
        });
        window.add(MainMenu);
        window.setSize(1100, 125);
        stage.addActor(window);
    }

    public void setBoundInfo(boolean clear, float Length, float Height, float Angle, Vector3 Position){
        if(clear){
            boundInfo.clear();
        }else{
            BoundInfo bI = new BoundInfo();
            bI.length = Length;
            bI.height = Height;
            bI.rotAngle = Angle;
            bI.position = Position;
            boundInfo.add(bI);
        }
    }

    public void createWall(float[] vertList, boolean obstacle){
        if(obstacle){
            Vector3 current = new Vector3(vertList[0], vertList[1], vertList[2]);
            Vector3 next = new Vector3(vertList[3], vertList[4], vertList[5]);
            setHighLow();
            float localMax = 0;
            //set local max of the 2 points
            if (current.y>localMax){
                localMax =  current.y;
            }
            if(next.y>localMax){
                localMax =  next.y;
            }
            float distance = current.dst(next);
            Vector3 midPoint = ((next.sub(current)).scl(0.5f)).add(current);
            float obsHeight = (localMax-lowest)+0.25f;
            //midPoint.y += obsHeight/2-(localMax-localMin-0.1);
            if(lowest<0){
                //lower by a ratio so only upper part is showing. No idea if this is right
                midPoint.y-=((localMax-lowest)/4)-0.25f;
                midPoint.y-=0.15f;
            }else {
               midPoint.y = (obsHeight/2);

            }
            Model wall = modelBuilder.createBox(distance, obsHeight, 0.08f,
                    new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            ModelInstance boundaryInst = new ModelInstance(wall, midPoint);
            Vector3 difference = (next.sub(current));
            Vector3 xAxis = new Vector3(1, 0, 0);
            float dotProd = difference.dot(xAxis);
            Vector3 origin = new Vector3(0, 0, 0);
            dotProd = dotProd / (difference.dst(origin) * xAxis.dst(origin));
            //Convert
            double dotResult = (double) dotProd;
            //Arc cos
            double angle = Math.acos(dotResult);
            //Convert
            float floatAngle = (float) angle;
            if (difference.z > 0) {
                floatAngle *= -1;
            }
            boundaryInst.transform.rotateRad(new Vector3(0, 1, 0), floatAngle);
            walls.add(boundaryInst);
            setBoundInfo(false, distance, obsHeight, floatAngle, midPoint);
        }
        if(!obstacle) {
            for (int i = 0; i < outerCount; i++) {
                Vector3 current = new Vector3(vertList[i * 3], vertList[i * 3 + 1], vertList[i * 3 + 2]);
                Vector3 next;
                if (i == outerCount - 1) {
                    next = new Vector3(vertList[0], vertList[1], vertList[2]);
                } else {
                    next = new Vector3(vertList[(i + 1) * 3], vertList[(i + 1) *3 + 1], vertList[(i + 1) * 3 + 2]);
                }
                float distance = current.dst(next);
                Vector3 midPoint = ((next.sub(current)).scl(0.5f)).add(current);
                midPoint.y = (highest + lowest) / 2;
                Model wall = modelBuilder.createBox(distance, highest - lowest + 0.15f, 0.08f, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                float height = highest - lowest + 0.15f;
                ModelInstance boundaryInst = new ModelInstance(wall, midPoint);
                Vector3 kiddingMe = new Vector3(midPoint.x, midPoint.y, midPoint.z);
                borderPos.add(kiddingMe);
                borderDist.add(distance);

                Vector3 difference = (next.sub(current));
                Vector3 xAxis = new Vector3(1, 0, 0);
                float dotProd = difference.dot(xAxis);
                Vector3 origin = new Vector3(0, 0, 0);
                dotProd = dotProd / (difference.dst(origin) * xAxis.dst(origin));
                //Convert
                double dotResult = (double) dotProd;
                //Arc cos
                double angle = Math.acos(dotResult);
                //Convert
                float floatAngle = (float) angle;
                if (difference.z > 0) {
                    floatAngle *= -1;
                }
                boundAngles.add(floatAngle);
                boundaryInst.transform.rotateRad(new Vector3(0, 1, 0), boundAngles.get(i));
                boundary.add(boundaryInst);
                setBoundInfo(false, distance, height, boundAngles.get(i), midPoint);
            }
            updateBorders();
        }
    }

    public void updateBorders(){
        for(int i=0;i<outerCount;i++){
            float[] adjHeight = borderHeight();
            float max = adjHeight[0];
            float min = adjHeight[1];
            float dist = borderDist.get(i);
            Model wall = modelBuilder.createBox(dist, max-min+0.2f, 0.08f, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Vector3 Pos = new Vector3(borderPos.get(i));
            Pos.y = (max+min)/2;
            ModelInstance boundaryInst = new ModelInstance(wall, Pos);
            boundaryInst.transform.rotateRad(new Vector3(0, 1, 0), boundAngles.get(i));
            boundary.set(i, boundaryInst);
        }
    }
    public float[] borderHeight(){
        float max = 0;
        float min = 0;
        lowest = 0;
        for(int i=0;i<onlyOuter.size();i++){
            if(vertList[onlyOuter.get(i)]>max){
                max = vertList[onlyOuter.get(i)];
            }
            if(vertList[onlyOuter.get(i)]<min){
                min= vertList[onlyOuter.get(i)];
            }
        }
        for(int i=0;i<vertList.length/8;i++){
            if(vertList[i*8+1]<lowest){
                lowest = vertList[i*8+1];
            }
        }
        float[]heights = new float[2];
        heights[0] = max;
        heights[1] = min;
        return heights;
    }

    public void setHighLow(){
        highest = 0;
        lowest = 0;
        for(int i=0; i<outerCount;i++) {
            if(vertList[i*8+1]>highest){
                highest = vertList[i*8+1];
            }
            if(vertList[i*8+1]<lowest){
                lowest = vertList[i*8+1];
            }
        }
    }

    public void createNormMesh(){
        ArrayList<Float> normArray = new ArrayList<Float>();
        for(int i = 0; i<indices.length/3;i++){
            int index1 = indices[i*3];
            int index2 = indices[i*3+1];
            int index3 = indices[i*3+2];

            //Add first vec
            normArray.add(vertList[index1*8]);normArray.add(vertList[index1*8+1]);normArray.add(vertList[index1*8+2]);
            //Add second vec
            normArray.add(vertList[index2*8]);normArray.add(vertList[index2*8+1]);normArray.add(vertList[index2*8+2]);
            //Add third vec
            normArray.add(vertList[index3*8]);normArray.add(vertList[index3*8+1]);normArray.add(vertList[index3*8+2]);
        }
        int size = normArray.size()*3-normArray.size()/3;
        float[] array = new float[size];
        //copy
        for (int i=0;i<normArray.size()/3;i++){
            array[i*8] = normArray.get(i*3);
            array[i*8+1] = normArray.get(i*3+1);
            array[i*8+2] = normArray.get(i*3+2);
        }

        indices = new short[normArray.size()/3];
        for(int i=0;i<normArray.size()/3;i++){
            indices[i] = (short) i;
        }

        //update vertList
        vertList = new float[array.length];
        for(int i=0;i<array.length;i++){
            vertList[i] = array[i];
        }
        //Set the UV texture coods
        setUV(vertList);
        //fill in the values for the normal
        realINDsize = indices.length;
        calcNorm();
    }
    public void calcNorm(){
        for(int i=0;i<indices.length/3;i++){
            int index1 = indices[i*3];
            int index2 = indices[i*3+1];
            int index3 = indices[i*3+2];
            //Add vectors
            Vector3 one = new Vector3(vertList[index1*8], vertList[index1*8+1], vertList[index1*8+2]);
            Vector3 two = new Vector3(vertList[index2*8], vertList[index2*8+1], vertList[index2*8+2]);
            Vector3 three = new Vector3(vertList[index3*8], vertList[index3*8+1], vertList[index3*8+2]);
            float mx = (one.x+two.x+three.x)/3;
            float my = (one.y+two.y+three.y)/3;
            float mz = (one.z+two.z+three.z)/3;
            Vector3 mid = new Vector3(mx, my, mz);

            Vector3 a = two.sub(one);
            Vector3 b = three.sub(one);
            Vector3 normalTri = a.crs(b);
            normalTri.nor();
            float x = normalTri.x;
            float y = normalTri.y;
            float z = normalTri.z;
            //Set the normals of all 3 vectors to normalTri
            vertList[index1*8+5] = x;vertList[index1*8+6] = y;vertList[index1*8+7] = z;
            vertList[index2*8+5] = x;vertList[index2*8+6] = y;vertList[index2*8+7] = z;
            vertList[index3*8+5] = x;vertList[index3*8+6] = y;vertList[index3*8+7] = z;
            normalTri.scl(4f);

                Model arrow = modelBuilder.createArrow(new Vector3(0, 0, 0), normalTri, new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                normArrow = new ModelInstance(arrow, mid);
                if (normArrows.size() < realINDsize/3) {
                    normArrows.add(normArrow);
                    triNorms.add(normalTri);
                } else {
                    normArrows.set(i, normArrow);
                    triNorms.set(i, normalTri);
                }
            onceArrow = true;
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMux);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        cam.update();
        engine.update(delta);

        texture.bind();
        shader.begin();
        shader.setUniformMatrix("u_worldView", cam.combined);
        shader.setUniformi("u_texture", 0);
        if(uvSet) {
            mesh.render(shader, GL20.GL_TRIANGLES);
        }
        shader.end();

        modelBatch.begin(cam);
        // grid
        if(!hideVerts) {
            modelBatch.render(instance2, environment);
            //renders the arrows showing in
            /*
            if(normArrows.size()>0){
                for(int i=0;i<normArrows.size();i++){
                    modelBatch.render(normArrows.get(i), environment);
                }
            }*/
        }
        //walls
        if(walls.size()>0){
            for(int i=0;i<walls.size();i++){
                modelBatch.render(walls.get(i), environment);
            }
        }
        //vert spheres
        if(!hideVerts) {
            for (int i = 0; i < vertices.size(); i++) {
                modelBatch.render(vertices.get(i), environment);
            }
        }
        //Render start and end Position
        for(int i=0;i<positions.size();i++){
            modelBatch.render(positions.get(i), environment);
        }
        if (showBounds){
            for (int i=0; i<boundary.size();i++){
                modelBatch.render(boundary.get(i), environment);
            }
        }
        modelBatch.end();
        stage.act(delta);
        stage.draw();

        //Rotate Camera to side view after Elevation_Editor is selected
        if (bool&&cam.position.y>2.17&&cam.position.z<9.8) {
            ctrlPressed = false;
            cam.rotateAround(new Vector3(0, 0, 0), new Vector3(1, 0, 0), 1.5f);
            if (cam.position.y<2.13&&cam.position.y>2.17&&cam.position.z<9.79&&cam.position.z>9.70) {
                bool=false;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
       Gdx.gl.glViewport(0, 0, width, height);
       stage.getViewport().update(width, height);
       cam.viewportHeight = height;
       cam.viewportWidth = width;
       cam.update();
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

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.E) {
            Course test = new Course("assfuckery");
            test.setTerrainMesh(mesh);
            test.setEndNormal(endNorm);
            test.setEndPosition(endPos);
            test.setStartNormal(strNorm);
            test.setStartPosition(startPos);
            test.export();

        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            game.showPauseMenu();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        dragX = screenX;
        dragY = screenY;
        Ray pickRay = cam.getPickRay(screenX, screenY);
        intersection2 = new Vector3();
        if (mode == Mode.POINT_EDITOR) {
            Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0f, 1f, 0f), 0f), intersection2);
        }
        if(mode == Mode.ELEVATION_EDITOR){
            for (int i=0;i<vertList.length/8;i++) {
                Vector3 coords = new Vector3(vertList[i*8],vertList[i*8+1],vertList[i*8+2]);
                if (Intersector.intersectRaySphere(pickRay, coords, 0.1f, intersection2)==true);
            }
        }

        if(mode == Mode.SET_OBSTACLES && Intersector.intersectRayTriangles(pickRay,vertList, indices, 8,intersection2) && !ctrlPressed){
            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                counter++;
                if (counter == 1) {
                    obsCoords[0] = intersection2.x;
                    obsCoords[1] = intersection2.y;
                    obsCoords[2] = intersection2.z;
                }
                if (counter == 2) {
                    obsCoords[3] = intersection2.x;
                    obsCoords[4] = intersection2.y;
                    obsCoords[5] = intersection2.z;
                    obstacle = true;
                    createWall(obsCoords, obstacle);
                    counter = 0;
                }
            }
        }
        if(mode == Mode.SET_START || mode == Mode.SET_END) {
            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                for (int i = 0; i < indices.length / 3; i++) {
                    Vector3 t1 = new Vector3(vertList[i * 3 * 8], vertList[i * 3 * 8 + 1], vertList[i * 3 * 8 + 2]);
                    Vector3 t2 = new Vector3(vertList[(i * 3 + 1) * 8], vertList[(i * 3 + 1) * 8 + 1], vertList[(i * 3 + 1) * 8 + 2]);
                    Vector3 t3 = new Vector3(vertList[(i * 3 + 2) * 8], vertList[(i * 3 + 2) * 8 + 1], vertList[(i * 3 + 2) * 8 + 2]);
                    System.out.println(vertList[i * 8] + " " + vertList[(i + 1) * 8] + " " + vertList[(i + 1) * 8]);
                    if (Intersector.intersectRayTriangle(pickRay, t1, t2, t3, intersection2)) {
                        //Get normal of triangles
                        Vector3 thisNorm = triNorms.get(i).nor();
                        if (mode == Mode.SET_START) {
                            Model startingPos = modelBuilder.createBox(0.12f, 0.001f, 0.12f, new Material(ColorAttribute.createDiffuse(Color.GOLD)),
                                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                            ModelInstance strPos = new ModelInstance(startingPos, intersection2);
                            strPos.transform.rotate(new Vector3(0, 1, 0), thisNorm);
                            positions.set(0, strPos);
                            startSet = true;
                            startPos = intersection2;
                            strNorm = thisNorm;

                            System.out.println(startPos+"  str pos   "+strNorm+"   strNorm");
                        }
                        if (mode == Mode.SET_END) {
                            float holeRadius = 0.06f;
                            //QUATERNION SHIT
                        /*
                            vec3 w = cross(u, v);
    quat q = quat(dot(u, v), w.x, w.y, w.z);
    q.w += length(q);
    return normalize(q);
                         */

                            //END

                            Model endingPos = modelBuilder.createSphere(holeRadius * 2, 0.01f, holeRadius * 2, 20, 20, new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                            ModelInstance EndPos = new ModelInstance(endingPos, intersection2);
                            EndPos.transform.rotate(new Vector3(0, 1, 0), thisNorm);
                            positions.set(1, EndPos);
                            endSet = true;
                            endPos = intersection2;
                            endNorm = thisNorm;
                            System.out.println(endPos+"  end pos   "+endNorm+"   endNorm");
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Ray pickRay = cam.getPickRay(screenX, screenY);
        switch (mode) {
            case DO_NOTHING:
                break;
            case POINT_EDITOR:
                //Find a point on the XZ plane
                Vector3 intersection = new Vector3();
                Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0f, 1f, 0f), 0f), intersection);
                if(outerMode){
                    outerCount++;
                }else{
                    innerVec.add(intersection);
                }
                String verts = intersection.toString();
                verts = verts.replaceAll("[()]","");
                verts = verts.replaceAll("[,]",", ");
                String[] Array = verts.split(",");
                resizeArray(vertList);
                //Add new floats to list of vertices
                for(int i = 0; i < 3; i++) {
                    vertList[listSize+i] = Float.parseFloat(Array[i]);
                }
                listSize+=3;
                //Create box representing vertex
                //ModelInstance vPosInst = new ModelInstance(vertexPos,intersection);
                ModelInstance vPosInst = new ModelInstance(sphere,intersection);
                vertices.add(vPosInst);
                break;

            case ELEVATION_EDITOR:
                if (runOnce) {
                    createWall(vertList, false);
                    //Copy x and z elements from vertList and put them in a new array
                    int newLength = (2 * vertList.length) / 3;
                    int forLoopNum = (vertList.length / 3);
                    newVertList = new float[newLength];
                    ArrayList<Float> temp = new ArrayList<Float>();
                    for (int i = 0; i < forLoopNum; i++) {
                        temp.add(vertList[i * 3]);
                        temp.add(vertList[i * 3 + 2]);
                    }
                    for (int i = 0; i < temp.size(); i++) {
                        newVertList[i] = temp.get(i);
                    }
                    makeMesh1();
                    updateMesh();
                    simClick(1);
                    if(innerVec.size() ==0){
                        for(int i=0;i<vertList.length/8;i++) {
                           // Vector3 outer = new Vector3(vertList[i * 8], vertList[i * 8 + 1], vertList[i * 8 + 2]);
                            onlyOuter.add(i*8+1);
                        }
                    }else {
                        for (int i = 0; i < vertList.length / 8; i++) {
                            for (int j = 0; j < innerVec.size(); j++) {
                                if (vertList[i * 8] != innerVec.get(j).x && vertList[i * 8 + 2] != innerVec.get(j).z) {
                                    //Vector3 outer = new Vector3(vertList[i * 8], vertList[i * 8 + 1], vertList[i * 8 + 2]);
                                    onlyOuter.add(i*8+1);
                                }
                            }
                        }
                    }
                    //Make sure automatic camera rotation is performed only once
                    bool = true;
                    runOnce = false;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            if (screenX < dragX) {
                cam.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 1, 0), 3f);
                dragX = screenX;
            }
            if (screenX > dragX) {
                cam.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 1, 0), -3f);
                dragX = screenX;
            }
        }
        if (intersection2!=null&&!runOnce&&mode==Mode.ELEVATION_EDITOR&&!ctrlPressed) {
            String verts2 = intersection2.toString();
            verts2 = verts2.replaceAll("[()]", "");
            verts2 = verts2.replaceAll("[,]", ", ");
            String[] Array2 = verts2.split(",");
            float[] liftTarget = new float[3];
            for (int i = 0; i < 3; i++) {
                liftTarget[i] = Float.parseFloat(Array2[i]);
            }
            //Check if click is on a vertex
            if (checkLiftVertex(liftTarget)) {
                liftVertex(liftTarget, screenY, false, index);
            }
            calcNorm();
            updateBorders();
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if(amount == 1&&cam.fieldOfView<85){
            cam.fieldOfView += 4;
        }
        if(amount == -1&&cam.fieldOfView>13){
            cam.fieldOfView -=4;
        }
        return true;
    }

    enum Mode {
        POINT_EDITOR, //top down view of the course
        ELEVATION_EDITOR, // perspective view of the course
        DO_NOTHING, //Wait for user to select option first
        SET_START,//Set start position
        SET_END, //Set end hole position
        SET_OBSTACLES //Set obstacles
    }
}
