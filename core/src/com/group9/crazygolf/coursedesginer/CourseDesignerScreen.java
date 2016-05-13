package com.group9.crazygolf.coursedesginer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
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

import java.util.ArrayList;
import java.util.Scanner;

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
    int dragX, dragY, listSize;
    float[] vertList = new float[0];
    ModelBatch modelBatch;
    ModelBuilder modelBuilder;
    ModelInstance instance;
    Model model;
    Environment environment;
    Mesh mesh;
    ArrayList<ModelInstance> vertices;



    public CourseDesignerScreen(Game game) {
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

        mesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
        //mesh.setVertices(new float[]{-1f, -1f, 0f,0f, -1f, 0f,0f, 0f, 0f,-1f, 0f, 0f,});
        mesh.setVertices(new float[]{0f, 0f, 0f,0f, 0f, 0,0f, 0f, 0f,0f, 0f, 0f,});
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0,});
        Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        modelBuilder.begin();
        modelBuilder.part("Temp", mesh, GL20.GL_TRIANGLES, material);
        model = modelBuilder.end();
        instance = new ModelInstance(model, 0,0,0);

        Model vertexPos = modelBuilder.createBox(0, 0, 0, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance vPosInst = new ModelInstance(vertexPos,0,0,0);
        vertices.add(vPosInst);
    }

    public void resizeArray(float[] oldVertList) {
        // create a new array of size+3
        int newSize = oldVertList.length + 3;
        float[] newArray = new float[newSize];
        System.arraycopy(oldVertList, 0, newArray, 0, oldVertList.length);
        vertList = newArray;
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMux);
    }

    private void createUI() {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Window window = new Window("Tools", skin);


        TextButton addVertexButton = new TextButton("Add Vertex", skin);
        addVertexButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
            {
                mode = Mode.POINT_EDITOR;
            }
        });
        window.add(addVertexButton);
        TextButton changeElevationButton = new TextButton("Change Elevation", skin);
        changeElevationButton.addListener(new ClickListener()
        {
           @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button)
           {
               mode = Mode.ELEVATION_EDITOR;
           }
        });
        window.add(changeElevationButton);
        TextButton ResetButton = new TextButton("Reset", skin);
        ResetButton.addListener(new ClickListener()
        {
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        engine.update(delta);
        stage.act(delta);
        stage.draw();

        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        for (int i=0;i<vertices.size();i++)
        {
            modelBatch.render(vertices.get(i), environment);
        }
        modelBatch.end();
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
        dragX = screenX;
        dragY = screenY;
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
                System.out.println(verts + " Intersector");
                //add new floats to list of vertices
                for(int i = 0; i < 3; i++) {
                    vertList[listSize+i] = Float.parseFloat(Array[i]);
                    //System.out.print(vertList[listSize+i]+ "  ");
                }
                listSize+=3;
                System.out.println(" ");

                Model vertexPos = modelBuilder.createBox(0.2f, 0f, 0.2f, new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                ModelInstance vPosInst = new ModelInstance(vertexPos,intersection);
                vertices.add(vPosInst);

                //System.out.println(verts);
                //System.out.print(vertList[vertList.length-3]+"  ");
                //System.out.print(vertList[vertList.length-2]+"  ");
                //System.out.println(vertList[vertList.length-1]);

                break;
            case ELEVATION_EDITOR:
                //vertices.clear();
                /*
                for (int i=0;i<vertList.length;i++)
                {
                    System.out.print(vertList[i]+"  ");
                }
                System.out.println(" Passing to mesh");
                */

                //Remove y float from vertList
                int newLength = (2*vertList.length)/3;
                int forLoopNum = (vertList.length/3);
                float[] newVertList  = new float[newLength];
                ArrayList<Float> temp = new ArrayList<Float>();
                //System.out.println(vertList.length);
                for (int i=0; i<forLoopNum; i++) {
                    temp.add(vertList[i*3]);
                    temp.add(vertList[i*3+2]);
                }
                for (int i=0; i<temp.size(); i++)
                {
                    newVertList[i] = temp.get(i);
                }

                EarClippingTriangulator triangulator = new EarClippingTriangulator();
                ShortArray meshIndices = triangulator.computeTriangles(newVertList);

                short[] indices = new short[meshIndices.size];
                for (int i=0; i<meshIndices.size;i++)
                {
                    indices[i] = meshIndices.get(i);
                    System.out.print(indices[i]+ " ");
                    System.out.println(meshIndices.get(i));
                }

                mesh = new Mesh(true, vertList.length, meshIndices.size,
                        new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
                mesh.setVertices(vertList);
                mesh.setIndices(indices);
                Material material = new Material(ColorAttribute.createDiffuse(Color.GREEN));
                modelBuilder.begin();
                modelBuilder.part("Course", mesh, GL20.GL_TRIANGLES, material);
                model = modelBuilder.end();
                instance = new ModelInstance(model, 0,0,0);

                break;
            case DO_NOTHING:
                break;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (screenX<dragX+75)
        {
            cam.rotateAround(new Vector3(0, 0, 0), new Vector3(0, 1, 0), 3f);
        }
        if (screenX>dragX-75)
        {
            cam.rotateAround(new Vector3(0,0,0), new Vector3(0,1,0), -3f);
        }
        if (screenY<dragY+75)
        {
            cam.rotateAround(new Vector3(0,0,0), new Vector3(1,0,0), 2.5f);
        }
        if (screenY>dragY-75)
        {
            cam.rotateAround(new Vector3(0,0,0), new Vector3(1,0,0), -2.5f);
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
