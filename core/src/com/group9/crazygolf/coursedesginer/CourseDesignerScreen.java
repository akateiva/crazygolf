package com.group9.crazygolf.coursedesginer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ShortArray;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
    int dragX, dragY, listSize, index;
    float[] vertList = new float[0];
    ModelBatch modelBatch;
    ModelBuilder modelBuilder;
    ModelInstance instance;
    Model model;
    Model vertexPos, vertPos;
    Environment environment;
    Mesh mesh;
    ArrayList<ModelInstance> vertices;
    boolean ctrlPressed, moveMouse,bool = false,runOnce = true;
    Robot robot;
    Vector3 intersection2;

    public CourseDesignerScreen(Game game) {
        ctrlPressed = false;
        this.game = game;
        stage = new Stage();
        //Because we want to check for events on an UI as well as clicks in the world, we must create an input multiplexer
        //Inputs will processed in the UI first, and if there are no events ( i.e. mouseDown returns false, then that that event is passed down to CourseDesignScreen event processor)
        inputMux = new InputMultiplexer();
        inputMux.addProcessor(stage);
        inputMux.addProcessor(this);

        engine = new Engine();

        /* Set up the camera */
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 0f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        mode = Mode.DO_NOTHING;
        vertices = new ArrayList<ModelInstance>();
        createUI();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();

        //Making a random mesh so it won't be null
        mesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
        mesh.setVertices(new float[]{0f, 0f, 0f,0f, 0f, 0,0f, 0f, 0f,0f, 0f, 0f,});
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0,});
        Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        modelBuilder.begin();
        modelBuilder.part("Temp", mesh, GL20.GL_TRIANGLES, material);
        model = modelBuilder.end();
        instance = new ModelInstance(model, 0,0,0);

        //Adding random model to ArrayList so it won't be null
        vertPos = modelBuilder.createBox(0, 0, 0, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance vPosInst = new ModelInstance(vertPos,0,0,0);
        vertices.add(vPosInst);

        //Make a cube model
        vertexPos = modelBuilder.createBox(0.1f, 0.1f, 0.1f, new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);


    }

    //Increace Array size by 3 (for new x,y,z coords)
    public void resizeArray(float[] oldVertList) {
        // create a new array of size+3
        int newSize = oldVertList.length + 3;
        float[] newArray = new float[newSize];
        System.arraycopy(oldVertList, 0, newArray, 0, oldVertList.length);
        vertList = newArray;
    }

    //Simulate mouse click in the middle of screen to get mesh to render
    public void simClick(int i){
        try{
            robot = new Robot();
        }
         catch(AWTException e){
             e.printStackTrace();
         }
        setInput(i);
        robot.delay(5);
        robot.mousePress(MouseEvent.BUTTON1_MASK);
        robot.mouseRelease(MouseEvent.BUTTON1_MASK);
    }

    @Override
    public void show() {
            Gdx.input.setInputProcessor(inputMux);
    }

    private void createUI() {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Window window = new Window("Tools", skin);

        TextButton addVertexButton = new TextButton("Add Vertex", skin);
        addVertexButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                mode = Mode.POINT_EDITOR;
            }
        });
        window.add(addVertexButton);
        TextButton changeElevationButton = new TextButton("Change Elevation", skin);
        changeElevationButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int point, int button) {
                if (!moveMouse) {
                    simClick(0);
                    moveMouse = true;
                }
                mode = Mode.ELEVATION_EDITOR;
                return true;
            }
        });
        window.add(changeElevationButton);
        TextButton ResetButton = new TextButton("Reset", skin);
        ResetButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                newCourseDesigner();
            }
        });
        window.add(ResetButton);

        window.setSize(350, 125);
        stage.addActor(window);
    }
    public void newCourseDesigner() {
        game.setScreen(new CourseDesignerScreen(game));
    }
    public void setInput(int i){
        if(i == 0) {
            Gdx.input.setInputProcessor(this);
        }
        if(i == 1) {
            Gdx.input.setInputProcessor(inputMux);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        engine.update(delta);
        cam.update();
        stage.act(delta);
        stage.draw();
        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        //For loop to render all box models representing vertices
        for (int i=0;i<vertices.size();i++) {
            modelBatch.render(vertices.get(i), environment);
        }
        modelBatch.end();

        //Rotate Camera after Elevation_Editor is selected
        if (bool&&cam.position.y>2.15&&cam.position.z<9.75) {
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
        if(keycode == Input.Keys.CONTROL_LEFT) {
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
        if(keycode == Input.Keys.CONTROL_LEFT) {
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
        Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0f, 1f, 0f), 0f), intersection2);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Ray pickRay = cam.getPickRay(screenX, screenY);
        switch (mode) {
            case POINT_EDITOR:
                //Find a point on the XZ plane
                Vector3 intersection = new Vector3();
                Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0f, 1f, 0f), 0f), intersection);
                String verts = intersection.toString();
                verts = verts.replaceAll("[()]","");
                verts = verts.replaceAll("[,]",", ");
                String[] Array = verts.split(",");
                resizeArray(vertList);
                //add new floats to list of vertices
                for(int i = 0; i < 3; i++) {
                    vertList[listSize+i] = Float.parseFloat(Array[i]);
                }
                listSize+=3;

                //Create box representing vertex
                ModelInstance vPosInst = new ModelInstance(vertexPos,intersection);
                vertices.add(vPosInst);

                break;
            case ELEVATION_EDITOR:
                if (runOnce) {
                    //Copy x and z elements from vertList and put them in a new array
                    int newLength = (2 * vertList.length) / 3;
                    int forLoopNum = (vertList.length / 3);
                    float[] newVertList = new float[newLength];
                    ArrayList<Float> temp = new ArrayList<Float>();
                    for (int i = 0; i < forLoopNum; i++) {
                        temp.add(vertList[i * 3]);
                        temp.add(vertList[i * 3 + 2]);
                    }
                    for (int i = 0; i < temp.size(); i++) {
                        newVertList[i] = temp.get(i);
                    }

                    //Getting Indices from x,z coords of vertices
                    EarClippingTriangulator triangulator = new EarClippingTriangulator();
                    ShortArray meshIndices = triangulator.computeTriangles(newVertList);

                    short[] indices = new short[meshIndices.size];
                    for (int i = 0; i < meshIndices.size; i++) {
                        indices[i] = meshIndices.get(i);
                    }

                    //Constructing mesh
                    mesh = new Mesh(true, vertList.length, indices.length,
                            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
                    mesh.setVertices(vertList);
                    mesh.setIndices(indices);
                    Material material = new Material(ColorAttribute.createDiffuse(Color.GREEN));
                    material.set(new IntAttribute(IntAttribute.CullFace, 1));
                    modelBuilder.begin();
                    modelBuilder.part("Course", mesh, GL20.GL_TRIANGLES, material);
                    model = modelBuilder.end();
                    instance = new ModelInstance(model, 0, 0, 0);
                    //Make sure automatic camera rotation is perfermoned only once
                    if (runOnce) {
                        bool = true;
                        runOnce = false;
                    }
                    simClick(1);
                }
                break;
            case DO_NOTHING:
                break;
        }
        return true;
    }

    public boolean checkLiftVertex(float[] liftTarget){
        for (int i=0;i<listSize/3;i++){
            if (liftTarget[0]<vertList[i*3]+0.2&&liftTarget[0]>vertList[i*3]-0.2){
                if (liftTarget[2]<vertList[i*3+2]+0.2&&liftTarget[2]>vertList[i*3+2]-0.2){
                    index = i*3+1;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        //Rotate around Y axis
        if (screenX<dragX+75&&ctrlPressed) {
            cam.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 1, 0), 3f);
        }
        if (screenX>dragX-75&&ctrlPressed) {
            cam.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 1, 0), -3f);
        }
        //Wont rotate if rotation around Y axis has happened. Only works in starting position. Commented out for now.
        /*//Rotate around X axis
        if (screenY<dragY+75&&ctrlPressed) {
            if (cam.position.y>2.08 &&cam.position.z<9.78) {
                cam.rotateAround(new Vector3(0, 0, 0), new Vector3(1, 0, 0), 2.5f);
            }
        }
        if (screenY>dragY-75&&ctrlPressed) {
            if (cam.position.y<5 &&cam.position.z>8.7)
            cam.rotateAround(new Vector3(0, 0, 0), new Vector3(1, 0, 0), -2.5f);
        }*/


        if (intersection2!=null) {
            String verts2 = intersection2.toString();
            verts2 = verts2.replaceAll("[()]", "");
            verts2 = verts2.replaceAll("[,]", ", ");
            String[] Array2 = verts2.split(",");
            float[] liftTarget = new float[3];
            for (int i = 0; i < 3; i++) {
                liftTarget[i] = Float.parseFloat(Array2[i]);
            }


            if (checkLiftVertex(liftTarget)) {
                if (screenY > dragY && !ctrlPressed) {
                    vertList[index] -= 0.03;
                    int cubeIndex = ((index - 1) / 3) + 1;
                    intersection2.add(0, vertList[index], 0);
                    ModelInstance newVertPos = new ModelInstance(vertPos, intersection2);
                    vertices.set(cubeIndex, newVertPos);
                    mesh.setVertices(vertList);
                }
                if (screenY < dragY && !ctrlPressed) {
                    vertList[index] += 0.03;
                    int cubeIndex = ((index - 1) / 3) + 1;
                    intersection2.add(0, vertList[index], 0);
                    ModelInstance newVertPos = new ModelInstance(vertPos, intersection2);
                    vertices.set(cubeIndex, newVertPos);
                    mesh.setVertices(vertList);
                }
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if(amount == 1){
            if (cam.fieldOfView<135)
                cam.fieldOfView += 10;
        }
        else if(amount == -1){
            if (cam.fieldOfView>15)
                cam.fieldOfView -=10;
        }
        return true;
    }

    enum Mode {
        POINT_EDITOR, //top down view of the course
        ELEVATION_EDITOR, // perspective view of the course
        DO_NOTHING //Wait for user to select option first
    }
}

