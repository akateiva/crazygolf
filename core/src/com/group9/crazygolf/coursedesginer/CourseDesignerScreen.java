package com.group9.crazygolf.coursedesginer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ShortArray;
import java.util.ArrayList;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.MouseEvent;

/**
 * Created by akateiva on 11/05/16.
 */
public class CourseDesignerScreen implements Screen, InputProcessor {
    Game game;
    Mode mode;
    Engine engine;
    PerspectiveCamera cam;
    InputMultiplexer inputMux;
    Stage stage;
    int dragY, listSize, index, dragX, U, V, outerCount, counter;
    float[] vertList = new float[0];
    float[] newVertList = new float[0];
    float[] obsCoords = new float[6];
    ModelBatch modelBatch;
    ModelBuilder modelBuilder;
    ModelInstance instance, instance2, vPosInst;
    Model model, grid;
    Model vertexPos, vertPos, sphere;
    Environment environment;
    Mesh mesh;
    ArrayList<ModelInstance> vertices, positions, boundary, walls;
    ArrayList<Float> boundAngles;
    boolean ctrlPressed, moveMouse,bool,runOnce = true, startSet, endSet, showBounds, calcAng, outerMode, hideVerts, obstacle, uvSet;
    Robot robot;
    Vector3 intersection2, startPos, endPos;
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

    public CourseDesignerScreen(Game game) {
        this.game = game;
        stage = new Stage();
        engine = new Engine();
        mode = Mode.DO_NOTHING;
        vertices = new ArrayList<ModelInstance>();
        positions = new ArrayList<ModelInstance>();
        boundary = new ArrayList<ModelInstance>();
        walls = new ArrayList<ModelInstance>();
        boundAngles = new ArrayList<Float>();
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

        /* Set up the camera */
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 0f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

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
        sphere = modelBuilder.createSphere(0.2f,0.2f,0.2f,20,20, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        //Make a cube model
        vertexPos = modelBuilder.createBox(0.15f, 0.15f, 0.15f, new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        //Make a 32x32 grid composed of 1x1 squares
        grid = modelBuilder.createLineGrid(32, 32, 1, 1, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance2 = new ModelInstance(grid, 0,0,0);
    }

    public void liftVertex(float[]liftTarget, int screenY, boolean resHeight, int index){
        if (resHeight){
            vertList[index]=0;
            createWall(vertList, false);
        }else {
            //Lift vertex up if mouse moves up
            if (screenY > dragY && !ctrlPressed&&vertList[index]>-1f) {
                    vertList[index] -= 0.05;

            }
            //Drop vertex down if mouse moves down
            if (screenY < dragY && !ctrlPressed&&vertList[index]<3f) {
                vertList[index] += 0.05;
            }
        }
        //Update cube position
        int cubeIndex = ((index - 1) / 8) + 1;
        //intersection2.add(0, vertList[index], 0);
        Vector3 temp = new Vector3(vertList[index-1], vertList[index], vertList[index+1]);
        //ModelInstance newVertPos = new ModelInstance(vertexPos, temp);
        ModelInstance newVertPos = new ModelInstance(sphere, temp);
        vertices.set(cubeIndex, newVertPos);
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

    public void updateMesh(){
        //Getting Indices from x,z coords of vertices
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        //ShortArray meshIndices = triangulator.computeTriangles(newVertList);
        DelaunayTriangulator dt = new DelaunayTriangulator();
        ShortArray meshIndices = dt.computeTriangles(newVertList, false);

        indices = new short[meshIndices.size];
        for (int i = 0; i < meshIndices.size; i++) {
            indices[i] = meshIndices.get(i);
        }

        //Constructing mesh
        mesh = new Mesh(true, vertList.length, indices.length,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
        addTextCoor(vertList);
        mesh.setVertices(vertList);
        mesh.setIndices(indices);
        Material material = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        modelBuilder.begin();
        modelBuilder.part("Course", mesh, GL20.GL_TRIANGLES, material);
        model = modelBuilder.end();
        for (int i=0;i<vertList.length/8;i++){
            System.out.print(vertList[i*8+3]+"  "+vertList[i*8+4]+" ");
        }
        System.out.println(" OG");

        //setUV(vertList);

        for (int i=0;i<vertList.length/8;i++){
            System.out.print(vertList[i*8+3]+"  "+vertList[i*8+4]+" ");
        }
        System.out.println(" NEW");

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

        //set texture uv coords
        for (int i=0;i<newArray.length/5;i++){
                newArray[5 * i + 3] = U;
                newArray[5 * i + 4] = V;
                changeUV();
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
    public void changeUV(){
        if (U==0 &&V==0){
            V=1;
        }
        else if (U==0&&V==1){
            U=1;
        }
        else if (U==1&&V==1){
            V=0;
        }
        else if (U==1&&V==0){
            U=0;
        }
    }

    public void setUV(float[] newArray){
        float u = ((float) Gdx.graphics.getWidth()) / texture.getWidth();
        float v = ((float) Gdx.graphics.getHeight()) / texture.getHeight();
        System.out.println(u+"  "+v +"   UV");
        for (int i=0;i<vertList.length/8;i++){
            System.out.print(vertList[i*8]+"   "+vertList[i*8+1]+"   "+vertList[i*8+2]+" ||  ");
        }
        System.out.println("");
        for(int i=0;i<vertList.length/8;i++){
            /*
            BoundingBox UV = mesh.calculateBoundingBox();
            Vector3 vec = new Vector3(vertList[i*8],vertList[i*8+1],vertList[i*8+2]);
            vec.sub(UV.min);
            System.out.println(vec+"  Vec");
            Vector3 div = UV.max.sub(UV.min);
            System.out.println(div+"  Div");
            float newX = vec.x/div.x;
            float newY = vec.y/div.y;
            float newZ = vec.z/div.z;
            Vector3 results = new Vector3(newX, newY, newZ);
            System.out.println("X  "+newX+ "      \t\t Z\t\t"+newZ);
            newArray[8*i+3] = newX;
            newArray[8*i+4] = newZ;
            System.out.println("  );*/
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
        //Check if input matches location of a vertex within error bound of +-0.3f
        for (int i=0;i<vertList.length/8;i++){
            if (liftTarget[0]<vertList[i*8]+0.3&&liftTarget[0]>vertList[i*8]-0.3&&
                    liftTarget[2]<vertList[i*8+2]+0.3&&liftTarget[2]>vertList[i*8+2]-0.3){
                    index = i*8+1;
                return true;
            }
        }
        return false;
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
                if(mode == Mode.POINT_EDITOR) {
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
                mode = Mode.SET_START;
                return true;
            }
        });
        window.add(setStartPos);
        TextButton setEndPos = new TextButton("Set End Pos", skin);
        setEndPos.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                mode = Mode.SET_END;
                counter =0;
                return true;
            }
        });
        window.add(setEndPos);
        TextButton toggleBounds = new TextButton("Toggle Boundaries", skin);
        toggleBounds.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                if(showBounds == false&&mode!=Mode.POINT_EDITOR) {
                    showBounds = true;
                    createWall(vertList, false);
                }else{
                    showBounds=false;
                }
                counter =0;
                return true;
            }});
        window.add(toggleBounds);

        TextButton toggleVerts = new TextButton("Toggle Vertices", skin);
        toggleVerts.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                if(hideVerts){
                    hideVerts=false;
                }else{
                    hideVerts = true;
                }
                counter =0;
                return true;
            }});
        window.add(toggleVerts);

        TextButton resetHeight = new TextButton("Reset Height", skin);
        resetHeight.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                for(int i=0;i<vertList.length/8;i++) {
                    float[] target = new float[3];
                    target[0] = vertList[i*8+1];
                    target[1] = vertList[i*8+2];
                    target[2] = vertList[i*8+3];
                    liftVertex(target, 0, true, i*8+1);
                }
                //Replace any set start/end points
                positions.set(0, vPosInst);positions.set(1, vPosInst);
                obstacle = false;
                walls.clear();
                counter =0;
                mode = Mode.ELEVATION_EDITOR;
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
        window.setSize(1100, 125);
        stage.addActor(window);
    }

    public void createWall(float[] vertList, boolean obstacle){
        if(obstacle){
            Vector3 current = new Vector3(vertList[0], vertList[1], vertList[2]);
            Vector3 next = new Vector3(vertList[3], vertList[4], vertList[5]);
            setHighLow();
            float height;
            if (current.y>next.y){
                height =  current.y;
            } else {
                height =  next.y;
            }
            float distance = current.dst(next);
            Vector3 midPoint = ((next.sub(current)).scl(0.5f)).add(current);
            if (midPoint.y>0) {
                midPoint.y -= height / 4.5;
            }
            if (midPoint.y<0) {
                midPoint.y -= 0.02f;
            }
            Model wall = modelBuilder.createBox(distance, height+0.2f, 0.08f,
                    new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
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

        }
        if(!obstacle) {
            for (int i = 0; i < outerCount; i++) {
                Vector3 current = new Vector3(vertList[i * 8], vertList[i * 8 + 1], vertList[i * 8 + 2]);
                Vector3 next;
                if (i == outerCount - 1) {
                    next = new Vector3(vertList[0], vertList[1], vertList[2]);
                } else {
                    next = new Vector3(vertList[(i + 1) * 8], vertList[(i + 1) * 8 + 1], vertList[(i + 1) * 8 + 2]);
                }
                setHighLow();

                float distance = current.dst(next);
                Vector3 midPoint = ((next.sub(current)).scl(0.5f)).add(current);
                midPoint.y = (highest + lowest) / 2;
                Model wall = modelBuilder.createBox(distance, highest - lowest + 0.1f, 0.08f, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
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
                if (!calcAng) {
                    boundAngles.add(floatAngle);
                }
                boundaryInst.transform.rotateRad(new Vector3(0, 1, 0), boundAngles.get(i));
                if (boundary.size() < outerCount * 2) {
                    boundary.add(boundaryInst);
                } else {
                    boundary.set(i, boundaryInst);
                }
                lowest = 0;
                highest = 0;
            }
        }
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



    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMux);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        engine.update(delta);
        cam.update();

        modelBatch.begin(cam);
        // grid
        if(!hideVerts) {
            modelBatch.render(instance2, environment);
        }
        modelBatch.end();

        texture.bind();
        shader.begin();
        shader.setUniformMatrix("u_worldView", cam.combined);
        shader.setUniformi("u_texture", 0);
        if(uvSet) {
            mesh.render(shader, GL20.GL_TRIANGLES);
        }
        shader.end();

        modelBatch.begin(cam);
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

        //Render start and end Position
        for(int i=0;i<positions.size();i++){
            modelBatch.render(positions.get(i), environment);
        }
        if (showBounds){
            for (int i=0; i<boundary.size();i++){
                modelBatch.render(boundary.get(i), environment);
            }
        }
        //Rotate Camera to side view after Elevation_Editor is selected
        if (bool&&cam.position.y>2.15&&cam.position.z<9.75) {
            ctrlPressed = false;
            cam.rotateAround(new Vector3(0, 0, 0), new Vector3(1, 0, 0), 1.5f);
            if (cam.position.y<2.08&&cam.position.y>2.06&&cam.position.z<9.79&&cam.position.z>9.77) {
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
        if(keycode == Input.Keys.CONTROL_LEFT||keycode == Input.Keys.CONTROL_RIGHT) {
            ctrlPressed = true;
        }
        //Reset camera
        if (keycode== Input.Keys.BACKSPACE) {
            cam.position.set(0f, 2.07f, 9.78f);
            cam.lookAt(0f,0f,0f);
            cam.near = 1f;
            cam.far = 300f;
            cam.up.set(0, 0, -1);
            cam.fieldOfView = 60;
            cam.update();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.CONTROL_LEFT||keycode == Input.Keys.CONTROL_RIGHT) {
            ctrlPressed = false;
        }
        return true;
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
        if(mode == Mode.SET_START && Intersector.intersectRayTriangles(pickRay,vertList, indices, 8,intersection2) && !ctrlPressed){
            Model startingPos = modelBuilder.createBox(0.3f, 0.001f, 0.3f, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            ModelInstance strPos = new ModelInstance(startingPos, intersection2);
            positions.set(0, strPos);
            startSet = true;
            startPos = intersection2;
        }

        if(mode == Mode.SET_END && Intersector.intersectRayTriangles(pickRay,vertList, indices, 8,intersection2) && !ctrlPressed){
            Model endingPos = modelBuilder.createSphere(0.4f, 0f, 0.4f, 20, 20, new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            ModelInstance EndPos = new ModelInstance(endingPos, intersection2);
            endSet = true;
            positions.set(1, EndPos);
            endPos = intersection2;
        }
        if(mode == Mode.SET_OBSTACLES && Intersector.intersectRayTriangles(pickRay,vertList, indices, 8,intersection2) && !ctrlPressed){
            counter++;
            if(counter==1){
                obsCoords[0] = intersection2.x;
                obsCoords[1] = intersection2.y;
                obsCoords[2] = intersection2.z;
            }
            if(counter ==2){
                obsCoords[3] = intersection2.x;
                obsCoords[4] = intersection2.y;
                obsCoords[5] = intersection2.z;
                obstacle = true;
                createWall(obsCoords, obstacle);
                counter =0;
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
                if(outerMode){
                    outerCount++;
                }
                //Find a point on the XZ plane
                Vector3 intersection = new Vector3();
                Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0f, 1f, 0f), 0f), intersection);
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
                    updateMesh();
                    simClick(1);
                    createWall(vertList, false);
                    calcAng = true;

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
        //Rotate around Y axis
        if (screenX<dragX&&ctrlPressed) {
            cam.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 1, 0), 3f);
            dragX = screenX;
        }
        if (screenX>dragX&&ctrlPressed) {
            cam.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 1, 0), -3f);
            dragX = screenX;
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
            createWall(vertList, false);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if(amount == 1&&cam.fieldOfView<135){
                cam.fieldOfView += 4;
        }
        if(amount == -1&&cam.fieldOfView>15){
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
